package ng.max.vams.data.manager

class AppManager {

    companion object{
        private lateinit var prefsManager: SharedPrefsManager
        private const val vehicleTableTag = "VEHICLE"
        private const val reasonTableTag = "REASON"
        private const val notificationTag = "NOTIFICATION"

        fun initPrefsManager(pref: SharedPrefsManager){
            prefsManager = pref
        }

        fun logout() {
            UserManager.clearUserData()
        }

        fun getVehicleTableFlag(): Int = prefsManager.getInt(vehicleTableTag)

        fun getReasonTableFlag(): Int = prefsManager.getInt(vehicleTableTag)

        fun setVehicleTableFlag(flag: Int){
            prefsManager.saveInt(vehicleTableTag, flag)
        }

        fun setReasonTableFlag(flag: Int){
            prefsManager.saveInt(reasonTableTag, flag)
        }

        fun saveMessagingServiceToken(token: String) {
            prefsManager.saveString(notificationTag, token)
        }

        fun getMessagingServiceToken(): String? = prefsManager.getString(notificationTag)

    }
}