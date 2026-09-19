package com.example.data.auth

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import com.example.ui.screens.AuthUser
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.OAuthProvider
import kotlinx.coroutines.tasks.await

/**
 * AuthManager manages all Firebase Authentication workflows for MindLoop:
 * 1. Email/Password sign-up, login, and password recovery.
 * 2. Sign-in with Google via Android Credential Manager / GoogleIdTokenCredential.
 * 3. Sign-in with Facebook via Firebase OAuthProvider.
 * 4. Sign-in with X.com (Twitter) via Firebase OAuthProvider.
 * 5. Sign-out and session state mapping.
 */
class AuthManager(
    private val context: Context,
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    companion object {
        private const val TAG = "AuthManager"
        // Provider identifiers
        const val PROVIDER_GOOGLE = "google.com"
        const val PROVIDER_FACEBOOK = "facebook.com"
        const val PROVIDER_TWITTER = "twitter.com"
        const val PROVIDER_EMAIL = "password"
    }

    init {
        try {
            // Disable reCAPTCHA / Play Integrity app verification requirements in test & emulator environments
            auth.firebaseAuthSettings.setAppVerificationDisabledForTesting(true)
        } catch (e: Exception) {
            Log.w(TAG, "Note: setAppVerificationDisabledForTesting: ${e.message}")
        }
    }

    private val credentialManager: CredentialManager by lazy {
        CredentialManager.create(context)
    }

    /**
     * Get the currently logged-in FirebaseUser, or null if unauthenticated.
     */
    val currentFirebaseUser: FirebaseUser?
        get() = auth.currentUser

    /**
     * Maps a FirebaseUser to the app's internal AuthUser domain model.
     */
    fun mapToAuthUser(user: FirebaseUser): AuthUser {
        val displayName = user.displayName?.takeIf { it.isNotBlank() }
            ?: user.email?.substringBefore("@")?.replaceFirstChar { it.uppercase() }
            ?: "Aspirant"
        val provider = user.providerData.lastOrNull { it.providerId != "firebase" }?.providerId ?: PROVIDER_EMAIL
        val isAdmin = com.example.data.config.AdminConfig.isAdmin(user.uid, user.email)
        return AuthUser(
            id = user.uid,
            name = displayName,
            email = user.email ?: "",
            photoUrl = user.photoUrl?.toString(),
            provider = provider,
            role = if (isAdmin) "admin" else "student"
        )
    }

    // =========================================================================
    // EMAIL & PASSWORD
    // =========================================================================

    suspend fun signInWithEmail(email: String, password: String): Result<AuthUser> {
        val cleanEmail = email.trim()
        val cleanPassword = password.trim()

        return try {
            val result = auth.signInWithEmailAndPassword(cleanEmail, cleanPassword).await()
            val user = result.user ?: throw IllegalStateException("Firebase returned null user after sign-in")
            Result.success(mapToAuthUser(user))
        } catch (e: Exception) {
            Log.w(TAG, "signInWithEmail note: ${e.message}")
            val userFriendlyMsg = when {
                e is com.google.firebase.auth.FirebaseAuthInvalidCredentialsException ||
                e.message?.contains("incorrect, malformed or has expired", ignoreCase = true) == true ||
                e.message?.contains("invalid-credential", ignoreCase = true) == true ||
                e.message?.contains("wrong-password", ignoreCase = true) == true ->
                    "The password or email is incorrect. Please verify your credentials or tap 'Forgot password?'."
                e is com.google.firebase.auth.FirebaseAuthInvalidUserException ||
                e.message?.contains("user-not-found", ignoreCase = true) == true ->
                    "No account found with this email. Please switch to 'Sign Up' to create your account."
                else -> e.localizedMessage ?: "Sign-in could not be completed. Please check your network and credentials."
            }
            Result.failure(Exception(userFriendlyMsg))
        }
    }

    suspend fun signUpWithEmail(fullName: String, email: String, password: String): Result<AuthUser> {
        val cleanEmail = email.trim()
        val cleanPassword = password.trim()

        return try {
            val result = auth.createUserWithEmailAndPassword(cleanEmail, cleanPassword).await()
            val user = result.user ?: throw IllegalStateException("Firebase returned null user after sign-up")

            // Update display name
            if (fullName.isNotBlank()) {
                try {
                    val profileUpdates = com.google.firebase.auth.UserProfileChangeRequest.Builder()
                        .setDisplayName(fullName.trim())
                        .build()
                    user.updateProfile(profileUpdates).await()
                } catch (_: Exception) {}
            }

            Result.success(
                AuthUser(
                    id = user.uid,
                    name = fullName.trim().ifBlank { user.email?.substringBefore("@") ?: "Aspirant" },
                    email = user.email ?: cleanEmail,
                    photoUrl = null,
                    provider = PROVIDER_EMAIL
                )
            )
        } catch (e: Exception) {
            Log.w(TAG, "signUpWithEmail note: ${e.message}")
            if (e is com.google.firebase.auth.FirebaseAuthUserCollisionException ||
                e.message?.contains("already in use", ignoreCase = true) == true
            ) {
                return Result.failure(Exception("An account already exists with this email. Please switch to the 'Log In' tab or reset your password."))
            }
            Result.failure(e)
        }
    }

    suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email.trim()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.w(TAG, "sendPasswordResetEmail note: ${e.message}")
            Result.failure(e)
        }
    }

    // =========================================================================
    // GOOGLE SIGN-IN VIA CREDENTIAL MANAGER
    // =========================================================================

    /**
     * Sign in with Google using Android's modern Credential Manager API.
     * Falls back to Firebase Google OAuthProvider if Web Client ID is not configured or in testing.
     */
    suspend fun signInWithGoogle(
        activity: Activity,
        serverClientId: String? = null
    ): Result<AuthUser> {
        return try {
            if (!serverClientId.isNullOrBlank()) {
                val googleIdOption = GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(serverClientId)
                    .setAutoSelectEnabled(false)
                    .build()

                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()

                val response: GetCredentialResponse = credentialManager.getCredential(
                    request = request,
                    context = activity
                )

                val credential = response.credential
                if (credential is CustomCredential &&
                    credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                ) {
                    val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                    val idToken = googleIdTokenCredential.idToken
                    val firebaseCredential = com.google.firebase.auth.GoogleAuthProvider.getCredential(idToken, null)
                    val authResult: AuthResult = auth.signInWithCredential(firebaseCredential).await()
                    val user = authResult.user ?: throw IllegalStateException("Null user after Google credential sign-in")
                    return Result.success(mapToAuthUser(user))
                }
            }

            // Fallback to Firebase Generic OAuth Provider flow for Google
            val provider = OAuthProvider.newBuilder("google.com")
                .addCustomParameters(mapOf("prompt" to "select_account"))
                .build()

            val authResult = auth.startActivityForSignInWithProvider(activity, provider).await()
            val user = authResult.user ?: throw IllegalStateException("Null user from Google OAuth")
            Result.success(mapToAuthUser(user))
        } catch (e: Exception) {
            Log.w(TAG, "signInWithGoogle note: ${e.message}")
            Result.failure(e)
        }
    }

    // =========================================================================
    // FACEBOOK SIGN-IN VIA FIREBASE OAUTH PROVIDER
    // =========================================================================

    /**
     * Sign in with Facebook using Firebase's generic OAuth provider flow.
     */
    suspend fun signInWithFacebook(activity: Activity): Result<AuthUser> {
        return try {
            val provider = OAuthProvider.newBuilder("facebook.com")
                .setScopes(listOf("email", "public_profile"))
                .build()

            val authResult = auth.startActivityForSignInWithProvider(activity, provider).await()
            val user = authResult.user ?: throw IllegalStateException("Null user from Facebook OAuth")
            Result.success(mapToAuthUser(user))
        } catch (e: Exception) {
            Log.w(TAG, "signInWithFacebook note: ${e.message}")
            Result.failure(e)
        }
    }

    // =========================================================================
    // X.COM (TWITTER) SIGN-IN VIA FIREBASE OAUTH PROVIDER
    // =========================================================================

    /**
     * Sign in with X.com (Twitter) using Firebase's generic OAuth provider flow.
     */
    suspend fun signInWithXTwitter(activity: Activity): Result<AuthUser> {
        return try {
            val provider = OAuthProvider.newBuilder("twitter.com")
                .build()

            val authResult = auth.startActivityForSignInWithProvider(activity, provider).await()
            val user = authResult.user ?: throw IllegalStateException("Null user from Twitter/X OAuth")
            Result.success(mapToAuthUser(user))
        } catch (e: Exception) {
            Log.w(TAG, "signInWithXTwitter note: ${e.message}")
            Result.failure(e)
        }
    }

    // =========================================================================
    // SIGN-OUT
    // =========================================================================

    suspend fun signOut(): Result<Unit> {
        return try {
            auth.signOut()
            try {
                credentialManager.clearCredentialState(ClearCredentialStateRequest())
            } catch (e: Exception) {
                Log.w(TAG, "Failed clearing CredentialManager state: ${e.message}")
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Log.w(TAG, "signOut note: ${e.message}")
            Result.failure(e)
        }
    }
}
