package livewallpaper.aod.screenlock.zipper.utilities

import android.content.Context
import android.content.SharedPreferences

object DataBasePref {
    var DataFileName = "IDFU.data"
    var Tpdata: SharedPreferences? = null
    @JvmStatic
    fun LoadPref(str: String?, context: Context): Int {
        Tpdata = context.getSharedPreferences(DataFileName, 0)
        return try {
            Tpdata?.getString(str, "0")?.toInt()?:return 0
        } catch (unused: Exception) {
            0
        }
    }

    @JvmStatic
    fun LoadPrefString(str: String?, context: Context): String? {
        Tpdata = context.getSharedPreferences(DataFileName, 0)
        return try {
            Tpdata?.getString(str, "")
        } catch (unused: Exception) {
            ""
        }
    }

    @JvmStatic
    fun SavePref(str: String?, str2: String?, context: Context) {
        if (Tpdata == null) {
            Tpdata = context.getSharedPreferences(DataFileName, 0)
        }
        val edit = Tpdata!!.edit()
        edit.putString(str, str2)
        edit.commit()
    }

    fun SavePrefString(str: String?, str2: String?, context: Context) {
        if (Tpdata == null) {
            Tpdata = context.getSharedPreferences(DataFileName, 0)
        }
        val edit = Tpdata!!.edit()
        edit.putString(str, str2)
        edit.commit()
    }
}