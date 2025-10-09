package com.atg.autonexo.core.data

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferences @Inject constructor(
    @ApplicationContext context: Context
) {
    private val prefs: SharedPreferences = context.getSharedPreferences(
        "autonexo_prefs",
        Context.MODE_PRIVATE
    )
    
    // ========== Auth Token Management ==========
    
    fun saveAuthToken(token: String) {
        prefs.edit().putString(KEY_AUTH_TOKEN, token).apply()
    }
    
    fun getAuthToken(): String? {
        return prefs.getString(KEY_AUTH_TOKEN, null)
    }
    
    fun clearAuthToken() {
        prefs.edit().remove(KEY_AUTH_TOKEN).apply()
    }
    
    fun isLoggedIn(): Boolean {
        return getAuthToken() != null
    }
    
    // ========== User ID Management ==========
    
    fun saveUserId(userId: Long) {
        prefs.edit().putLong(KEY_USER_ID, userId).apply()
    }
    
    fun getUserId(): Long? {
        val id = prefs.getLong(KEY_USER_ID, -1L)
        return if (id == -1L) null else id
    }
    
    // ========== Workshop Management ==========
    
    fun setIsWorkshopManager(isManager: Boolean) {
        prefs.edit().putBoolean(KEY_IS_WORKSHOP_MANAGER, isManager).apply()
    }
    
    fun isWorkshopManager(): Boolean {
        return prefs.getBoolean(KEY_IS_WORKSHOP_MANAGER, false)
    }
    
    fun setHasWorkshop(hasWorkshop: Boolean) {
        prefs.edit().putBoolean(KEY_HAS_WORKSHOP, hasWorkshop).apply()
    }
    
    fun hasWorkshop(): Boolean {
        return prefs.getBoolean(KEY_HAS_WORKSHOP, false)
    }
    
    // ========== Clear All ==========
    
    fun clearUserData() {
        prefs.edit().clear().apply()
    }
    
    companion object {
        private const val KEY_AUTH_TOKEN = "auth_token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_IS_WORKSHOP_MANAGER = "is_workshop_manager"
        private const val KEY_HAS_WORKSHOP = "has_workshop"
    }
}

