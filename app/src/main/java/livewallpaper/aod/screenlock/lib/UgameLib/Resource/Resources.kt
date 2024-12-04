package livewallpaper.aod.screenlock.lib.UgameLib.Resource

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.util.Log
import livewallpaper.aod.screenlock.lib.Glide.GlideBitmapFactory

object Resources {
    fun Inicial() {}
    fun GetRectOfImage(bitmap: Bitmap): Rect {
        return Rect(0, 0, bitmap.width, bitmap.height)
    }

    fun CreateBitmap(i: Int, context: Context?): Bitmap? {
        if (context == null) {
            Log.i("ctx null cls Resources", "context is null on class resource createRectofimage")
            return null
        }
        return try {
            GlideBitmapFactory.decodeResource(context.resources, i)
        } catch (unused: Exception) {
            null
        }
    }

    fun CreateBitmapFromPath(str: String?): Bitmap? {
        return try {
            val options = BitmapFactory.Options()
            options.inSampleSize = 1
            BitmapFactory.decodeFile(str, options)
        } catch (unused: Exception) {
            null
        }
    }
}