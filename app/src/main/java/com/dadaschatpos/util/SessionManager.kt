package com.dadaschatpos.util

import android.content.Context
import androidx.core.content.edit

class SessionManager(context: Context) {
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    val isOnboardingComplete: Boolean
        get() = prefs.getBoolean(KEY_ONBOARDING_COMPLETE, false)

    val isRememberedLogin: Boolean
        get() = prefs.getBoolean(KEY_LOGGED_IN, false) && prefs.getBoolean(KEY_REMEMBER, false)

    val currentUserId: Long?
        get() = prefs.getLong(KEY_USER_ID, -1L).takeIf { it > 0 }

    val defaultMenuVersion: Int
        get() = prefs.getInt(KEY_DEFAULT_MENU_VERSION, 0)

    fun completeOnboarding() {
        prefs.edit { putBoolean(KEY_ONBOARDING_COMPLETE, true) }
    }

    fun saveSession(userId: Long, remember: Boolean) {
        prefs.edit {
            putLong(KEY_USER_ID, userId)
            putBoolean(KEY_LOGGED_IN, true)
            putBoolean(KEY_REMEMBER, remember)
        }
    }

    fun logout() {
        prefs.edit {
            remove(KEY_USER_ID)
            putBoolean(KEY_LOGGED_IN, false)
            putBoolean(KEY_REMEMBER, false)
        }
    }

    fun saveDefaultMenuVersion(version: Int) {
        prefs.edit { putInt(KEY_DEFAULT_MENU_VERSION, version) }
    }

    companion object {
        private const val PREFS_NAME = "dadas_chat_pos_session"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_LOGGED_IN = "logged_in"
        private const val KEY_REMEMBER = "remember"
        private const val KEY_ONBOARDING_COMPLETE = "onboarding_complete"
        private const val KEY_DEFAULT_MENU_VERSION = "default_menu_version"
    }
}
