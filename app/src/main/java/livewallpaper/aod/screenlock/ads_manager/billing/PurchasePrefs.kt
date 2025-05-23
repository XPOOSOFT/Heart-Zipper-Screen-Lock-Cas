package livewallpaper.aod.screenlock.ads_manager.billing

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.text.TextUtils
import java.util.Arrays

class PurchasePrefs(appContext: Context?) {
    private var preferences: SharedPreferences? = null

    init {
        preferences = PreferenceManager.getDefaultSharedPreferences(appContext)
    }

    // Getters
    fun getInt(key: String?): Int? {
        return preferences?.getInt(key, 0)
    }

    fun getListInt(key: String?): ArrayList<Int> {
        val myList = TextUtils.split(preferences?.getString(key, ""), "‚‗‚")
        val arrayToList = ArrayList(listOf(*myList))
        val newList = ArrayList<Int>()
        for (item in arrayToList) newList.add(item.toInt())
        return newList
    }

    fun getString(key: String?): String? {
        return preferences?.getString(key, "")
    }

    fun getBoolean(key: String?): Boolean {
        return preferences?.getBoolean(key, false)?:return false
    }

    // Put methods
    fun putInt(key: String?, value: Int) {
        checkForNullKey(key)
        preferences?.edit()?.putInt(key, value)?.apply()
    }

    fun putLong(key: String?, value: Long) {
        checkForNullKey(key)
        preferences?.edit()?.putLong(key, value)?.apply()
    }

    fun putBoolean(key: String?, value: Boolean) {
        checkForNullKey(key)
        preferences?.edit()?.putBoolean(key, value)?.apply()
    }

    fun remove(key: String?) {
        preferences?.edit()?.remove(key)?.apply()
    }

    fun clear() {
        preferences?.edit()?.clear()?.apply()
    }

    private fun checkForNullKey(key: String?) {
        if (key == null) {
            throw NullPointerException()
        }
    }
}