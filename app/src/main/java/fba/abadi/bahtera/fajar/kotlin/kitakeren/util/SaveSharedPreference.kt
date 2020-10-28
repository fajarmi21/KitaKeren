package fba.abadi.bahtera.fajar.kotlin.kitakeren.util

import android.content.Context
import android.content.SharedPreferences

import android.preference.PreferenceManager

object SaveSharedPreference {
    private fun getPreferences(context: Context?): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(context)
    }

    /**
     * Set the Login Status
     * @param context
     * @param loggedIn
     * @param level
     */
    fun setLoggedIn(context: Context?, status: Boolean, username: String, email: String?, photo: String) {
        val editor = getPreferences(context).edit()
        editor.putBoolean("logged_in_status", status)
        editor.putString("logged_in_username", username)
        editor.putString("logged_in_email", email)
        editor.putString("logged_in_photo", photo)
        editor.apply()
    }

    fun getStatus(context: Context?): Boolean {
        return getPreferences(context).getBoolean("logged_in_status", false)
    }

    fun getname(context: Context?): String? {
        return getPreferences(context).getString("logged_in_username", null)
    }

    fun getemail(context: Context?): String? {
        return getPreferences(context).getString("logged_in_email", null)
    }

    fun setLang(context: Context?, lang: Int) {
        val editor = getPreferences(context).edit()
        editor.putInt("language", lang)
        editor.apply()
    }

    fun logout(context: Context?) {
        getPreferences(context).edit().remove("logged_in_status").apply()
        getPreferences(context).edit().remove("logged_in_username").apply()
        getPreferences(context).edit().remove("logged_in_email").apply()
        getPreferences(context).edit().remove("logged_in_photo").apply()
        getPreferences(context).edit().remove("logged_in_google_api").apply()
    }

    /**
     * Get the Login Status
     * @param context
     * @return boolean: login status
     */
    fun getPhoto(context: Context?): String? {
        return getPreferences(context).getString("logged_in_photo", null)
    }

    fun getLang(context: Context?): Int {
        return getPreferences(context).getInt("language", 0)

    }
}