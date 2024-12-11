package livewallpaper.aod.screenlock.zipper.utilities

import android.content.Context
import android.util.Log
import livewallpaper.aod.screenlock.zipper.utilities.ConstantValues.SpeedActivePref

object AppAdapter {
    @JvmStatic
    fun getSelectedFontNumber(context: Context?): Int {
        val LoadPref = context?.let { DataBasePref.LoadPref(ConstantValues.SelectedFontPref, it) }
        return if (LoadPref == 0) {
            1
        } else LoadPref?: 0
    }

    fun IsFirstUse(context: Context?): Boolean {
        return context?.let { DataBasePref.LoadPref(ConstantValues.FirstUsePref, it) } == 0
    }

    fun SetFirstUseTrue(context: Context?) {
        DataBasePref.SavePref(ConstantValues.FirstUsePref, "1", context ?: return)
//        DataBasePref.SavePref(ConstantValues.SoActivePref, "1", context)
//        DataBasePref.SavePref(ConstantValues.VibratActivePref, "1", context)
        DataBasePref.SavePref(ConstantValues.DActivePref, "1", context)
        DataBasePref.SavePref(ConstantValues.TActivePref, "1", context)
        DataBasePref.SavePref(ConstantValues.BateryActivePref, "1", context)
        DataBasePref.SavePref(SpeedActivePref, "350", context)
    }

//    fun GetSelectedPhont(context: Context?): String {
//        val selectedFontNumber = getSelectedFontNumber(context)
//        if (selectedFontNumber == 1) {
//            return "android_7.ttf"
//        }
//        if (selectedFontNumber == 2) {
//            return "Arizonia-Regular.ttf"
//        }
//        if (selectedFontNumber == 3) {
//            return "ArnoProRegular.otf"
//        }
//        return if (selectedFontNumber == 4) "TitilliumText22L003.otf" else "GSTIGNRM.TTF"
//    }

    @JvmStatic
    fun SaveFont(context: Context?, i: Int) {
        val str = ConstantValues.SelectedFontPref
        if (context != null) {
            DataBasePref.SavePref(str, i.toString() + "", context)
        }
    }

    fun getSelectedWallpaperNumber(context: Context?): Int {
        val LoadPref = context?.let { DataBasePref.LoadPref(ConstantValues.SelectedWallpaper, it) }
        return LoadPref ?: 0
    }

    fun getSelectedWallpaperBgNumber(context: Context?): Int {
        val LoadPref = context?.let {
            DataBasePref.LoadPref(
                ConstantValues.SelectedWallpaperbg,
                it
            )
        }
        return if (LoadPref == 0) {
            1
        } else LoadPref!!
    }

    @JvmStatic
    fun SaveWallpaper(context: Context?, i: Int) {
        val str = ConstantValues.SelectedWallpaper
        DataBasePref.SavePref(str, i.toString() + "", context ?: return)
    }

    @JvmStatic
    fun SaveWallpaperBg(context: Context?, i: Int) {
        val str = ConstantValues.SelectedWallpaperbg
        DataBasePref.SavePref(str, i.toString() + "", context ?: return)
    }

    fun IsHomeKeyLocked(context: Context?): Boolean {
        return context?.let { DataBasePref.LoadPref(ConstantValues.LockHomeKeyPref, it) } != 0
    }

    fun IsDateActive(context: Context?): Boolean {
        return context?.let { DataBasePref.LoadPref(ConstantValues.DActivePref, it) } != 0
    }

    fun IsTimeActive(context: Context?): Boolean {
        return context?.let { DataBasePref.LoadPref(ConstantValues.TActivePref, it) } != 0
    }

    @JvmStatic
    fun IsSoundActive(context: Context?): Boolean {
        return context?.let { DataBasePref.LoadPref(ConstantValues.SoActivePref, it) } != 0
    }

    @JvmStatic
    fun IsVibrateActive(context: Context?): Boolean {
        return context?.let { DataBasePref.LoadPref(ConstantValues.VibratActivePref, it) } != 0
    }

    fun IsPedometerActive(context: Context?): Boolean {
        return context?.let { DataBasePref.LoadPref(ConstantValues.DepomeActivePref, it) } != 0
    }

    fun IsWeahterActive(context: Context?): Boolean {
        return context?.let { DataBasePref.LoadPref(ConstantValues.WeatActivePref, it) } != 0
    }

    fun IsUnlockBtnHideen(context: Context?): Boolean {
        return context?.let { DataBasePref.LoadPref(ConstantValues.HideUnlockBtnPref, it) } != 0
    }
}