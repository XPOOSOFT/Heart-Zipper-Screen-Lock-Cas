package livewallpaper.aod.screenlock.lib.UgameLib.Animations

import livewallpaper.aod.screenlock.lib.UgameLib.Shapes.Urect
import livewallpaper.aod.screenlock.lib.UgameLib.Transition.Transition_Type

class FadeInOut(
    i: Int,
    var Obj: Urect,
    d: Double,
    d2: Double,
    var Time: Double,
    transition_Type: Transition_Type
) {
    var CurentFlash = 0.0
    var CurentTime = 0.0
    var FA: Double
    var FlashNumber: Double
    var TA: Double
    var tr: Transition_Type

    init {
        FlashNumber = i.toDouble()
        list!!.add(this)
        tr = transition_Type
        FA = d
        TA = d2
        Fade(Obj, FA, TA, Time, tr)
    }

    fun Calc(): Boolean {
        if (CurentTime == Time) {
            if (FlashNumber == -1.0) {
                val d = FA
                FA = TA
                TA = d
                CurentTime = 0.0
                Fade(Obj, FA, TA, Time, tr)
            } else if (CurentFlash >= FlashNumber) {
                return false
            } else {
                CurentFlash += 1.0
                CurentTime = 0.0
                val d2 = FA
                FA = TA
                TA = d2
                Fade(Obj, FA, TA, Time, tr)
            }
        }
        CurentTime += 1.0
        return true
    }

    companion object {
        var list: MutableList<FadeInOut?>? = ArrayList()
        fun DeleteIfExist(urect: Urect): Boolean {
            for (i in list!!.indices) {
                if (list!![i]!!.Obj === urect) {
                    list!!.removeAt(i)
                    return true
                }
            }
            return false
        }

        fun update() {
            if (list != null) {
                var i = 0
                while (i < list!!.size) {
                    try {
                        if (!list!![i]!!.Calc()) {
                            list!!.removeAt(i)
                            i--
                        }
                        i++
                    } catch (unused: Exception) {
                        return
                    }
                }
            }
        }
    }
}