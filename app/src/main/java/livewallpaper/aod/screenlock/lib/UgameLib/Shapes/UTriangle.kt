package livewallpaper.aod.screenlock.lib.UgameLib.Shapes

import android.graphics.Canvas
import android.graphics.Paint

class UTriangle : Urect {
    constructor(d: Double, d2: Double, d3: Double, d4: Double) : super(d, d2, d3, d4) {
        paint.style = Paint.Style.FILL_AND_STROKE
        paint.strokeWidth = 4.0f
    }

    constructor(d: Double, d2: Double, d3: Double, d4: Double, i: Int) : super(d, d2, d3, d4, i) {
        paint.style = Paint.Style.FILL_AND_STROKE
        paint.strokeWidth = 4.0f
    }

    override fun Draw(canvas: Canvas) {
        setRotate(getRotate() + 45.0)
        super.Draw(canvas)
        setRotate(getRotate() - 45.0)
    }
}