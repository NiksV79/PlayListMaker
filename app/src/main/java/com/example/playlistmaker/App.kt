package com.example.playlistmaker

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate

class App : Application() {
    var isDarkTheme = false
    lateinit var prefs: SharedPreferences

    override fun onCreate() {
        super.onCreate()
        prefs = getSharedPreferences(AppPrefKeys.NAME, MODE_PRIVATE)
        isDarkTheme = prefs.getBoolean(AppPrefKeys.KEY_DARKTHEME, isDarkTheme)
        applyTheme()
    }

    fun switchTheme(newIsDarkTheme: Boolean) {
        isDarkTheme = newIsDarkTheme
        prefs.edit()
            .putBoolean(AppPrefKeys.KEY_DARKTHEME,isDarkTheme)
            .apply()
        applyTheme()
    }

    fun applyTheme() {
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkTheme) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}

object AppPrefKeys {
    const val NAME = "playlistmaker"
    const val KEY_DARKTHEME = "spv_key_darktheme"
    const val KEY_TRACKS_HISTORY = "spv_key_tracks_history"
}
