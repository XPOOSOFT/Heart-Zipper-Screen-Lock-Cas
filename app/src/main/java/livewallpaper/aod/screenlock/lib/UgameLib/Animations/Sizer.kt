package livewallpaper.aod.screenlock.lib.UgameLib.Animations

import livewallpaper.aod.screenlock.lib.UgameLib.GameAdapters.Timer
import livewallpaper.aod.screenlock.lib.UgameLib.Shapes.Urect
import livewallpaper.aod.screenlock.lib.UgameLib.Transition.Transition
import livewallpaper.aod.screenlock.lib.UgameLib.Transition.Transition_Type

class Sizer(
    urect: Urect,
    d: Double,
    d2: Double,
    d3: Double,
    d4: Double,
    d5: Double,
    transition_Type: Transition_Type,
    i: Int
) {
    var CurentTime = 0.0
    var Fh: Double
    var Fw: Double
    var Obj: Urect
    var Th: Double
    var Time: Double
    var Tw: Double
    var transition_type: Transition_Type
    var waitTimer: Timer

    init {
        waitTimer = Timer(i, 0)
        waitTimer.start()
        Obj = urect
        Fw = d
        Fh = d2
        Tw = d3
        Th = d4
        Time = d5
        transition_type = transition_Type
        list?.add(this)
        Obj.setSizerAnnimation(this)
    }

    fun Calc(): Boolean {
        if (!waitTimer.IsFinished()) {
            return true
        }
        Obj.setWidth(Transition.GetValue(transition_type, CurentTime, Fw, Tw, Time))
        Obj.setHeight(Transition.GetValue(transition_type, CurentTime, Fh, Th, Time))
        if (CurentTime == Time) {
            return false
        }
        CurentTime += 1.0
        return true
    }

    companion object {
        var list: MutableList<Sizer?>? = ArrayList()
        fun DeleteIfExist(urect: Urect): Boolean {
            for (i in list?.indices?:return false) {
                if (list!![i]?.Obj === urect) {
                    list?.removeAt(i)
                    return true
                }
            }
            return false
        }

        fun update() {
            if (list != null) {
                var i = 0
                while (i < list?.size?:return) {
                    try {
                        if (!(list!![i]?.Calc()?:return)) {
                            list!![i]?.waitTimer?.Remove()
                            list?.removeAt(i)
                            i--
                        }
                    } catch (unused: Exception) {
                    }
                    i++
                }
            }
        }
    }
}