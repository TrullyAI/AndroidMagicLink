package ai.trully.webview

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

class WebviewApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }
}