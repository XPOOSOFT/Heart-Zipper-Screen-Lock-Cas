package livewallpaper.aod.screenlock.lib.UgameLib.Data

import android.content.Context
import livewallpaper.aod.screenlock.zipper.R

object DataColector {
    fun GetAppName(context: Context): String {
        return context.getString(R.string.app_name)
    }

    fun GetPackageName(context: Context): String {
        return context.applicationContext.packageName
    }
}