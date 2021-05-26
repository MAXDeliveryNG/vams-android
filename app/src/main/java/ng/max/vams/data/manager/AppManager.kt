package ng.max.vams.data.manager

class AppManager {

    companion object{
        private lateinit var prefsManager: SharedPrefsManager

        fun initPrefsManager(pref: SharedPrefsManager){
            prefsManager = pref
        }

        fun logout() {
            UserManager.clearUserData()
        }

    }
}