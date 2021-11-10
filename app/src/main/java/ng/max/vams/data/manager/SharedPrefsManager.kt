package ng.max.vams.data.manager

import android.content.Context
import android.content.SharedPreferences
import ng.max.vams.BuildConfig
import javax.inject.Inject

class SharedPrefsManager @Inject constructor(context: Context) {

    private val sharedPref: SharedPreferences = context.getSharedPreferences(KEY_PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        const val KEY_PREFS_NAME = "${BuildConfig.APPLICATION_ID}-prefs"
    }

    private fun edit(): SharedPreferences.Editor = sharedPref.edit()

    fun saveBoolean(key: String, value: Boolean) =
        edit().putBoolean(key, value).apply()

    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean =
        sharedPref.getBoolean(key, defaultValue)

    fun remove(key: String) {
        edit().remove(key).apply()
    }

    fun saveString(key: String, value: String) = edit().putString(key, value).apply()

    fun getString(key: String): String? = sharedPref.getString(key, null)

    fun saveInt(key: String, value: Int) = edit().putInt(key, value).apply()

    fun getInt(key: String): Int = sharedPref.getInt(key, 0)

}
