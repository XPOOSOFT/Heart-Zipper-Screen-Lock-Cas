package livewallpaper.aod.screenlock.zipper.utilities

import android.content.Context
import android.view.View
import android.widget.Switch
import android.widget.TextView
import livewallpaper.aod.screenlock.zipper.utilities.DataBasePref.LoadPref
import livewallpaper.aod.screenlock.zipper.utilities.DataBasePref.SavePref

object CheckBoxUpdater {
    fun UCC(
        z: Boolean,
        str: String?,
        context: Context?
    ): Boolean {
        if (z) {
            SavePref(str, "1", context!!)
        } else {
            SavePref(str, "0", context!!)
        }
        return false
    }

    fun UC(
        switchR: Switch,
        z: Boolean,
        str: String?,
        context: Context?,
        z2: Boolean,
        textView: TextView?,
    ): Boolean {
        val z3 = !z
        if (z3) {
            switchR.isChecked = true
            if (textView != null) {
                textView.text = "on"
            }
            if (z2) {
                SavePref(str, "1", context!!)
            }
        } else {
            if (textView != null) {
                textView.text = "off"
            }
            switchR.isChecked = false
            if (z2) {
                SavePref(str, "0", context!!)
            }
        }
        return z3
    }

    @JvmStatic
    fun UL(str: String?, context: Context?, switchBtn: Switch?): Boolean {
        if (LoadPref(str, context!!) == 0) {
            switchBtn?.isChecked = false
            return false
        }
        switchBtn?.isChecked = true
        return true
    }

    fun UL(switchR: Switch?, str: String?, context: Context?): Boolean {
        if (switchR == null) {
            return false
        }
        switchR.layoutParams.width = Uscreen.width / 7
        if (LoadPref(str, context!!) == 0) {
            switchR.isChecked = false
            return false
        }
        switchR.isChecked = true
        return true
    }
}