//package livewallpaper.aod.screenlock.lib.UgameLib.Shapes
//
//import android.graphics.Canvas
//import android.graphics.Rect
//import android.graphics.drawable.Drawable
//import android.util.Log
//import livewallpaper.aod.screenlock.lib.UgameLib.GameAdapters.GameAdapter
//import livewallpaper.aod.screenlock.lib.UgameLib.Resource.Resources
//import livewallpaper.aod.screenlock.lib.UgameLib.scripts.uIimageSizeType
//
//class UImageDrawable : Urect {
//    @JvmField
//    var image: Drawable? = null
//    var sizeType = uIimageSizeType.FitXY
//
//    constructor(d: Double, d2: Double, d3: Double, d4: Double, bitmap: Drawable?) : super(
//        d,
//        d2,
//        d3,
//        d4
//    ) {
//        paint.color = 0
//        image = bitmap
//    }
//
//    constructor(d: Double, d2: Double, d3: Double, d4: Double, i: Int, i2: Int) : super(
//        d,
//        d2,
//        d3,
//        d4
//    ) {
////        image = Resources.CreateDrawable(i, GameAdapter.ctx)
//    }
//
//    constructor(i: Int) : super(0.0, 0.0, 0.0, 0.0) {
//        image = Resources.CreateD(i, GameAdapter.ctx)
//        setHeight(image?.height?.toDouble()!!)
//        setWidth(image?.width?.toDouble()!!)
//    }
//
//    constructor(bitmap: Drawable) : super(
//        0.0,
//        0.0,
//        bitmap.width.toDouble(),
//        bitmap.height.toDouble()
//    ) {
//        image = bitmap
//    }
//
//    constructor(d: Double, d2: Double, d3: Double, d4: Double, i: Int) : super(d, d2, d3, d4, i)
//
//    override fun Draw(canvas: Canvas) {
//        val save = canvas.save()
//        canvas.rotate(
//            getRotate().toInt().toFloat(), GetCenterX().toInt().toFloat(), centerY.toInt().toFloat()
//        )
//        paint.alpha = getAlpha().toInt()
//        canvas.skew(skewX.toInt().toFloat(), skewY.toInt().toFloat())
//        if (image?.isRecycled == true) {
//            Log.i("image recycled", "image recycled")
//            return
//        }
//        when (sizeType) {
//            uIimageSizeType.FitXY -> canvas.drawD(
//                image?:return, Resources.GetRectOfImage(
//                    image ?: return
//                ), GetRect(), paint
//            )
//
//            uIimageSizeType.FitX -> {
//                val GetRectOfImage = Resources.GetRectOfImage(
//                    image ?: return
//                )
//                Rect()
//                val GetRect = GetRect()
//                canvas.drawD(
//                    image?:return, Rect(
//                        0,
//                        0,
//                        GetRectOfImage.width(),
//                        (GetRectOfImage.height() * (GetRect.height() / GetRect.width())).toDouble()
//                            .toInt()
//                    ), GetRect(), paint
//                )
//            }
//
//            else -> {}
//        }
//        canvas.restoreToCount(save)
//        drawChildrens(canvas)
//    }
//
//    val relativeRotation: Double
//        get() = rotate
//}