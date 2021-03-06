package ng.max.vams.data.manager

import com.google.gson.Gson
import ng.max.vams.data.remote.response.User

class UserManager {

    companion object{
        private lateinit var prefsManager: SharedPrefsManager
        private const val userTag = "user"
        private const val tokenTag = "token"
        private const val userRoleTag = "user_role"
        private const val userCityTag = "user_city"


        fun initPrefsManager(pref: SharedPrefsManager){
            prefsManager = pref
        }

        private val keys = mapOf(
            Pair(userTag, userTag),
            Pair(tokenTag, tokenTag),
            Pair(userRoleTag, userRoleTag),
            Pair(userCityTag, userCityTag)
        )


        fun getUser(): User? {
            val userJson = prefsManager.getString(keys.getValue(userTag))
            return Gson().fromJson(userJson, User::class.java)
        }

        fun saveUser(user: User) {
            val userJson = Gson().toJson(user, User::class.java)
            prefsManager.saveString(keys.getValue(userTag), userJson)

        }

        fun saveToken(token: String) {
            prefsManager.saveString(keys.getValue(tokenTag), token)
        }

        fun getToken(): String? {
            return prefsManager.getString(keys.getValue(tokenTag))
        }

        fun saveUserRole(userRole: String) {
            prefsManager.saveString(keys.getValue(userRoleTag), userRole)
        }

        fun getUserRole() : String? = prefsManager.getString(keys.getValue(userRoleTag))


        fun isLoggedIn(): Boolean {
            return getUser() != null
        }

        fun clearUserData() {
            for (key in keys.keys) {
                prefsManager.remove(key)
            }
        }

    }
}