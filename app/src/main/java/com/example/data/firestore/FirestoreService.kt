package com.example.data.firestore

import android.content.Context
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.PersistentCacheIndexManager
import java.util.UUID

object FirestoreService {
    private const val TAG = "FirestoreService"
    private const val PREFS_NAME = "mindloop_firestore_prefs"
    private const val KEY_USER_ID = "active_user_id"

    @Volatile
    private var firestoreInstance: FirebaseFirestore? = null

    @Volatile
    private var activeUserId: String? = null

    @Volatile
    private var isUsingFallbackConfig: Boolean = false

    fun initialize(context: Context): FirebaseFirestore {
        return firestoreInstance ?: synchronized(this) {
            val appContext = context.applicationContext ?: context

            // Ensure FirebaseApp is initialized
            if (FirebaseApp.getApps(appContext).isEmpty()) {
                try {
                    FirebaseApp.initializeApp(appContext)
                } catch (e: Exception) {
                    Log.w(TAG, "Standard FirebaseApp.initializeApp failed: ${e.message}")
                }
            }

            // Fallback initialization with FirebaseOptions if still not initialized
            if (FirebaseApp.getApps(appContext).isEmpty()) {
                try {
                    val options = FirebaseOptions.Builder()
                        .setApplicationId("1:50808292388:android:5fe946514b1424db5a44b1")
                        .setProjectId("mindloop-80c94")
                        .setApiKey("AIzaSyAlI226N3xEtPZCJk2_9c6OpWTd4nu9B9I")
                        .setStorageBucket("mindloop-80c94.firebasestorage.app")
                        .build()
                    FirebaseApp.initializeApp(appContext, options)
                    isUsingFallbackConfig = false
                    Log.i(TAG, "FirebaseApp initialized with project mindloop-80c94")
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to initialize FirebaseApp: ${e.message}", e)
                }
            }

            // Retrieve or generate persistent user_id, prioritizing FirebaseAuth currentUser UID
            val firebaseAuthUid = try {
                com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid
            } catch (e: Exception) {
                null
            }

            if (!firebaseAuthUid.isNullOrBlank()) {
                activeUserId = firebaseAuthUid
                val prefs = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                prefs.edit().putString(KEY_USER_ID, firebaseAuthUid).apply()
            } else if (activeUserId == null) {
                val prefs = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                var uid = prefs.getString(KEY_USER_ID, null)
                if (uid.isNullOrBlank()) {
                    uid = "student_" + UUID.randomUUID().toString().take(8)
                    prefs.edit().putString(KEY_USER_ID, uid).apply()
                }
                activeUserId = uid
            }

            val app = try {
                FirebaseApp.getInstance()
            } catch (e: Exception) {
                Log.w(TAG, "FirebaseApp.getInstance failed: ${e.message}")
                null
            }

            val db = if (app != null) {
                FirebaseFirestore.getInstance(app)
            } else {
                FirebaseFirestore.getInstance()
            }

            // Enable Firestore's offline persistence
            val settings = FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build()

            db.firestoreSettings = settings

            // Enable local indexing in cache if supported
            try {
                db.persistentCacheIndexManager?.apply {
                    enableIndexAutoCreation()
                }
            } catch (e: Exception) {
                Log.w(TAG, "PersistentCacheIndexManager auto-indexing setup note: ${e.message}")
            }

            // Explicitly enable network so remote cloud sync always operates
            try {
                db.enableNetwork()
                Log.i(TAG, "Firestore live cloud network synchronization active")
            } catch (e: Exception) {
                Log.w(TAG, "enableNetwork notice: ${e.message}")
            }

            firestoreInstance = db
            db
        }
    }

    fun disableNetworkSafely() {
        try {
            getDb().disableNetwork()
            Log.i(TAG, "Firestore network disabled safely to prevent remote permission conflicts")
        } catch (e: Exception) {
            Log.w(TAG, "disableNetworkSafely notice: ${e.message}")
        }
    }

    fun enableNetworkSafely() {
        try {
            getDb().enableNetwork()
            Log.i(TAG, "Firestore network re-enabled")
        } catch (e: Exception) {
            Log.w(TAG, "enableNetworkSafely notice: ${e.message}")
        }
    }

    fun getDb(): FirebaseFirestore {
        return firestoreInstance ?: FirebaseFirestore.getInstance()
    }

    fun getUserId(): String {
        val authUid = try {
            com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid
        } catch (e: Exception) {
            null
        }
        if (!authUid.isNullOrBlank()) {
            return authUid
        }
        return activeUserId ?: "student_default"
    }

    fun getUserEmail(): String {
        return try {
            com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.email ?: ""
        } catch (e: Exception) {
            ""
        }
    }

    fun setUserId(context: Context, newUserId: String) {
        if (newUserId.isNotBlank()) {
            activeUserId = newUserId
            val appContext = context.applicationContext ?: context
            val prefs = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit().putString(KEY_USER_ID, newUserId).apply()
            Log.i(TAG, "Active Firebase User ID updated to: $newUserId")
            // Ensure network is active
            enableNetworkSafely()
        }
    }

    fun getUserDocumentPath(): String {
        return "users/${getUserId()}"
    }

    // User-scoped subcollections (users/{userId}/...)
    fun getUserDocument() = getDb().collection("users").document(getUserId())
    fun getNotesCollection() = getDb().collection("users/${getUserId()}/notes")
    fun getQuestionsCollection() = getDb().collection("users/${getUserId()}/questions")
    fun getStudySessionsCollection() = getDb().collection("users/${getUserId()}/study_sessions")
    fun getQuestionAttemptsCollection() = getDb().collection("users/${getUserId()}/question_attempts")
    fun getReelsCollection() = getDb().collection("users/${getUserId()}/reels")

    // Root collections for direct global visibility in Firebase Console
    fun getRootNotesCollection() = getDb().collection("notes")
    fun getRootQuestionsCollection() = getDb().collection("questions")
    fun getRootStudySessionsCollection() = getDb().collection("study_sessions")
    fun getRootQuestionAttemptsCollection() = getDb().collection("question_attempts")
    fun getRootReelsCollection() = getDb().collection("reels")
}
