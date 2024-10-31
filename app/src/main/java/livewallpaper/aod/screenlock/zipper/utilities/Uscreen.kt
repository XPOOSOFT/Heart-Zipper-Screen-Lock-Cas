package livewallpaper.aod.screenlock.zipper.utilities

import android.app.Activity
import android.graphics.Point

object Uscreen {
    var height = 0
    @JvmField
    var width = 0
    fun Init(activity: Activity?) {
        val defaultDisplay = activity?.windowManager?.defaultDisplay
        val point = Point()
        defaultDisplay?.getSize(point)
        width = point.x
        height = point.y
    }
}