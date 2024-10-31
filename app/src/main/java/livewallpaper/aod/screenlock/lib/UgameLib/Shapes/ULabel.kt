package livewallpaper.aod.screenlock.lib.UgameLib.Shapes

import android.graphics.Canvas
import android.graphics.Paint.Align
import android.graphics.Typeface
import livewallpaper.aod.screenlock.lib.Media.Media

class ULabel : Urect {
    var text: String

    constructor(d: Double, d2: Double, d3: Double, d4: Double, i: Int, str: String) : super(
        d,
        d2,
        d3,
        d4,
        i
    ) {
        text = str
//        font = Media.font1
        paint.textAlign = Align.CENTER
    }

    constructor(d: Double, d2: Double, d3: Double, d4: Double, str: String) : super(d, d2, d3, d4) {
        text = str
//        font = Media.font1
        paint.textAlign = Align.CENTER
    }

    var textAlign: Align?
        get() = paint.textAlign
        set(align) {
            paint.textAlign = align
        }

    fun SetTextSize(d: Double) {
        paint.textSize = d.toFloat()
    }

    val textSize: Double
        get() = paint.textSize.toDouble()
    var font: Typeface?
        get() = paint.typeface
        set(typeface) {
            if (typeface != null) {
                paint.typeface = typeface
            }
        }


    override fun Draw(canvas: Canvas) {
        try {
            val save = canvas.save()
            canvas.skew(skewX.toInt().toFloat(), skewY.toInt().toFloat())
            canvas.rotate(
                getRotate().toInt().toFloat(), GetCenterX().toInt().toFloat(), centerY.toInt().toFloat()
            )
            paint.alpha = getAlpha().toInt()
            canvas.drawText(text, GetCenterX().toFloat(), (centerY + Height() / 5.0).toFloat(), paint)
            canvas.restoreToCount(save)
            drawChildrens(canvas)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}