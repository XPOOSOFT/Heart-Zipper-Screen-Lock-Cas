package livewallpaper.aod.screenlock.lib.UgameLib.Shapes

import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.Log

class UimagePart : Uimage {
    var ImageRect: Urect? = null
    var LogedImageRecycled = false

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    constructor(
        d: Double,
        d2: Double,
        d3: Double,
        d4: Double,
        urect: Urect?,
        bitmap: Bitmap?
    ) : super(d, d2, d3, d4, bitmap) {
        ImageRect = urect
    }

    override fun Draw(canvas: Canvas) {
        val save = canvas.save()
        canvas.rotate(
            getRotate().toInt().toFloat(), GetCenterX().toInt().toFloat(), centerY.toInt().toFloat()
        )
        paint.alpha = getAlpha().toInt()
        canvas.skew(skewX.toInt().toFloat(), skewY.toInt().toFloat())
        if (!image!!.isRecycled || LogedImageRecycled) {
            canvas.drawBitmap(image?:return, ImageRect?.GetRect(), GetRect(), paint)
        } else {
            LogedImageRecycled = true
            Log.i("img recycled UimagePart", "image recycled UimagePart")
        }
        canvas.restoreToCount(save)
        drawChildrens(canvas)
    }


}