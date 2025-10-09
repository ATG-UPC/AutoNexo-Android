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
    
    fun clearUserData() {
        prefs.edit().clear().apply()
    }
    
    companion object {
        private const val KEY_IS_WORKSHOP_MANAGER = "is_workshop_manager"
        private const val KEY_HAS_WORKSHOP = "has_workshop"
    }
}

