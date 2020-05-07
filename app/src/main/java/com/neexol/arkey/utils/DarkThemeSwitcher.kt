package com.neexol.arkey.utils

import androidx.appcompat.app.AppCompatDelegate

fun setDarkThemeEnabled(isEnabled: Boolean) {
    AppCompatDelegate.setDefaultNightMode(
        if (isEnabled) {
            AppCompatDelegate.MODE_NIGHT_YES
        } else {
            AppCompatDelegate.MODE_NIGHT_NO
        }
    )
}