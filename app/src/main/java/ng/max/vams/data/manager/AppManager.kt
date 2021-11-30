package ng.max.vams.data.manager

class AppManager {

    companion object{
        private lateinit var prefsManager: SharedPrefsManager
        private const val vehicleTableTag = "VEHICLE"
        private const val userRoleTag = "user_role"

        fun initPrefsManager(pref: SharedPrefsManager){
            prefsManager = pref
        }

        private val userRoleKeys = mapOf(
            Pair(userRoleTag, userRoleTag),
        )

        fun logout() {
            UserManager.clearUserData()
            clearUserRoleData()
        }

        fun getVehicleTableFlag(): Int = prefsManager.getInt(vehicleTableTag)

        fun setVehicleTableFlag(flag: Int){
            prefsManager.saveInt(vehicleTableTag, flag)
        }

        fun saveUserRole(userRole: String) {
            prefsManager.saveString(userRoleKeys.getValue(userRoleTag), userRole)
        }

        fun getUserRole() : String? = prefsManager.getString(userRoleKeys.getValue(userRoleTag))

        fun clearUserRoleData() {
            for (key in userRoleKeys.keys) {
                prefsManager.remove(key)
            }
        }
    }
}