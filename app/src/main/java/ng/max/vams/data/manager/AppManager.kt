package ng.max.vams.data.manager

import androidx.navigation.NavController
import ng.max.vams.R

class AppManager {

    companion object{
        private lateinit var prefsManager: SharedPrefsManager

        fun initPrefsManager(pref: SharedPrefsManager){
            prefsManager = pref
        }

        fun logout(navController: NavController) {
            UserManager.clearUserData()
            navController.popBackStack(R.id.homeFragment, false)
        }

    }
}