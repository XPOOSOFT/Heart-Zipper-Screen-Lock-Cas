package livewallpaper.aod.screenlock.lib.UgameLib.GameAdapters

import android.content.Context.WINDOW_SERVICE
import android.graphics.Bitmap
import android.util.DisplayMetrics
import android.view.WindowManager

class Screen {
    var DecalX = 0.0
    var DecalY = 0.0

    companion object {
        var Height = 0.0
        var Width = 0.0
        fun Inicialize() {
            val displayMetrics = DisplayMetrics()
            (GameAdapter.ctx?.getSystemService(WINDOW_SERVICE) as WindowManager).defaultDisplay.getMetrics(
                displayMetrics
            )
            Height = displayMetrics.heightPixels.toDouble()
            Width = displayMetrics.widthPixels.toDouble()
        }

        fun GetXCenter(): Double {
            return Width / 2.0
        }

        fun GetYCenter(): Double {
            return Height / 2.0
        }

        fun GetX(d: Double): Double {
            return d * Width / 1000.0
        }

        fun Gety(d: Double): Double {
            return d * Height / 500.0
        }

        fun getRedimentionedHeight(d: Double, bitmap: Bitmap): Double {
            return bitmap.width.toDouble() * (d / bitmap.width.toDouble())
        }

        fun getRedimentionedwidth(d: Double, bitmap: Bitmap): Double {
            return bitmap.width.toDouble() * (d / bitmap.height.toDouble())
        }
    }
}