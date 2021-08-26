package ng.max.vams.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import ng.max.vams.data.manager.AppManager
import ng.max.vams.data.manager.SharedPrefsManager
import ng.max.vams.data.manager.UserManager
import javax.inject.Inject

@HiltAndroidApp
class App: Application() {
    @Inject
    lateinit var prefsManager: SharedPrefsManager
    override fun onCreate() {
        super.onCreate()
        UserManager.initPrefsManager(prefsManager)
        AppManager.initPrefsManager(prefsManager)
    }
}