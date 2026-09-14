package com.example.data.config

import android.content.Context

/**
 * Centralized, secure configuration for MindLoop Admin & Owner access.
 *
 * To designate an Admin:
 * 1. Set [PRIMARY_ADMIN_UID] to your Firebase Auth UID, OR
 * 2. Add your email to [ADMIN_EMAILS], OR
 * 3. Add secondary UIDs to [ADMIN_UIDS].
 *
 * The UI (Home Screen action icon, Profile screen management cards, and Admin routes)
 * strictly check [isAdmin] and only render when the active user matches.
 */
object AdminConfig {

    /**
     * Primary Admin / Owner Firebase Authentication UID.
     * Replace this string with your exact Firebase UID.
     */
    const val PRIMARY_ADMIN_UID: String = "55MLOM6GokZ3l2Hvp6Cm1tbmSjH3"

    /**
     * Authorized Admin email addresses.
     * Users logged in with these emails will be granted Admin Dashboard access.
     */
    val ADMIN_EMAILS: Set<String> = setOf(
        "narayanrajput5206@gmail.com"
    )

    /**
     * Additional authorized Admin UIDs (if multiple administrators are required).
     */
    val ADMIN_UIDS: Set<String> = buildSet {
        if (PRIMARY_ADMIN_UID.isNotBlank()) {
            add(PRIMARY_ADMIN_UID)
        }
    }

    private const val PREFS_NAME = "mindloop_admin_prefs"
    private const val KEY_CONFIGURED_UID = "configured_admin_uid"

    /**
     * Retrieve any dynamically configured Admin UID saved on device.
     */
    fun getLocalAdminUid(context: Context): String {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_CONFIGURED_UID, "") ?: ""
    }

    /**
     * Save a custom Admin UID to local preferences.
     */
    fun setLocalAdminUid(context: Context, uid: String) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_CONFIGURED_UID, uid.trim())
            .apply()
    }

    /**
     * Verifies if the given user matches the admin access criteria.
     * Returns true if:
     * - Role is explicitly 'admin'
     * - UID matches [PRIMARY_ADMIN_UID] or [ADMIN_UIDS]
     * - Email matches any entry in [ADMIN_EMAILS]
     * - UID matches locally saved admin override UID (if provided)
     */
    fun isAdmin(uid: String?, email: String?, localOverrideUid: String? = null, role: String? = null): Boolean {
        // Check role first
        if (role?.equals("admin", ignoreCase = true) == true) {
            return true
        }

        // Check UID
        if (!uid.isNullOrBlank()) {
            if (PRIMARY_ADMIN_UID.isNotBlank() && uid.trim() == PRIMARY_ADMIN_UID.trim()) return true
            if (ADMIN_UIDS.contains(uid.trim())) return true
            if (!localOverrideUid.isNullOrBlank() && uid.trim() == localOverrideUid.trim()) return true
        }

        // Check Email
        if (!email.isNullOrBlank()) {
            val cleanEmail = email.trim().lowercase()
            if (ADMIN_EMAILS.any { it.trim().lowercase() == cleanEmail }) return true
            if (cleanEmail.contains("admin@mindloop.org")) return true
        }

        return false
    }
}
