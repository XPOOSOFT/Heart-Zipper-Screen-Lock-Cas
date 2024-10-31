package livewallpaper.aod.screenlock.zipper.utilities

import android.content.Context
import livewallpaper.aod.screenlock.zipper.utilities.ConstantValues.PasswordSet

object PasswordAdapter {
    var Password: String? = null

    @JvmStatic
    fun checkPasswordAct(context: Context?): Boolean? {
        return context?.let { DataBasePref.LoadPref(PasswordSet, it) } != 0
    }


    @JvmStatic
    fun LoadPassword(context: Context?): String? {
        if (context?.let { DataBasePref.LoadPref(PasswordSet, it) } == 0) {
            Password = ""
        } else {
            Password = context?.let { DataBasePref.LoadPrefString("pass", it) }
        }
        return Password
    }

    @JvmStatic
    fun SavePassword(context: Context?, str: String?) {
        if (context != null) {
            DataBasePref.SavePref(PasswordSet, "1", context)
        }
        if (context != null) {
            DataBasePref.SavePref("pass", str, context)
        }
    }

    @JvmStatic
    fun enablePassword(context: Context?, str: String?) {
        if (context != null) {
            DataBasePref.SavePref(PasswordSet, "1", context)
        }
        if (context != null) {
            DataBasePref.SavePref("pass", str, context)
        }
    }

    @JvmStatic
    fun disablePassword(context: Context?) {
        if (context != null) {
            DataBasePref.SavePref(PasswordSet, "0", context)
        }
        if (context != null) {
            DataBasePref.SavePref("pass", "", context)
        }
    }
}