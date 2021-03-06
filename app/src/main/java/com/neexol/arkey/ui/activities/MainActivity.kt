package com.neexol.arkey.ui.activities

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import com.neexol.arkey.R
import com.neexol.arkey.persistence.SettingsPreferences
import com.neexol.arkey.utils.blockScreenCapture
import com.neexol.arkey.utils.hideSoftInput
import com.neexol.arkey.utils.setDarkThemeEnabled
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {

    private val settingsPrefs: SettingsPreferences by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_App)
        setDarkThemeEnabled(settingsPrefs.isDarkThemeEnabled)
        super.onCreate(savedInstanceState)
        blockScreenCapture()
        setContentView(R.layout.activity_main)
        paintStatusBar()
    }

    private fun paintStatusBar() {
        if (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_NO){
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        window.statusBarColor = android.R.attr.windowBackground
    }

    fun enableNavigateButton(toolbar: Toolbar) {
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
            hideSoftInput(it.windowToken)
        }
    }
}