package ng.max.vams.data.manager

class AppManager {

    companion object{
        private lateinit var prefsManager: SharedPrefsManager
        private const val vehicleTableTag = "VEHICLE"

        fun initPrefsManager(pref: SharedPrefsManager){
            prefsManager = pref
        }

        fun logout() {
            UserManager.clearUserData()
        }

        fun getVehicleTableFlag(): Int = prefsManager.getInt(vehicleTableTag)

        fun setVehicleTableFlag(flag: Int){
            prefsManager.saveInt(vehicleTableTag, flag)
        }

    }
}