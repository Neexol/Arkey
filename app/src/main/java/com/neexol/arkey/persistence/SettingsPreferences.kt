package com.neexol.arkey.persistence

import android.content.Context

class SettingsPreferences(context: Context) {

    companion object {
        private const val DARK_THEME_KEY = "DARK_THEME"
    }

    private val preferences = context.getSharedPreferences(
        "SettingsPrefs",
        Context.MODE_PRIVATE
    )

    var isDarkThemeEnabled = preferences.getBoolean(DARK_THEME_KEY, false)
        private set

    fun storeDarkTheme() {
        isDarkThemeEnabled = true
        preferences.edit().putBoolean(DARK_THEME_KEY, true).apply()
    }

    fun storeLightTheme() {
        isDarkThemeEnabled = false
        preferences.edit().putBoolean(DARK_THEME_KEY, false).apply()
    }
}