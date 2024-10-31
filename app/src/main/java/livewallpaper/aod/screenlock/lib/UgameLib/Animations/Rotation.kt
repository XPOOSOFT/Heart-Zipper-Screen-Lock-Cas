package livewallpaper.aod.screenlock.lib.UgameLib.Animations

import livewallpaper.aod.screenlock.lib.UgameLib.GameAdapters.Timer
import livewallpaper.aod.screenlock.lib.UgameLib.Shapes.Urect
import livewallpaper.aod.screenlock.lib.UgameLib.Transition.Transition
import livewallpaper.aod.screenlock.lib.UgameLib.Transition.Transition_Type


class Rotation {
    var AnimationTimer: Timer? = null
    var CurentTime = 0
    var Done = false
    var FromRot: Double
    var Obj: Urect?
    var Time: Int
    var ToRot: Double
    var WaitTime = 0
    var WaitTimer: Timer? = null
    var transition_type: Transition_Type

    constructor(
        urect: Urect?,
        d: Double,
        d2: Double,
        d3: Double,
        transition_Type: Transition_Type
    ) {
        urect?.setRotationAnimation(this)
        Obj = urect
        FromRot = d
        ToRot = d2
        WaitTime = 0
        Time = d3.toInt()
        transition_type = transition_Type
        list?.add(this)
        ManipulateTimers()
    }

    constructor(
        urect: Urect?,
        d: Double,
        d2: Double,
        d3: Double,
        transition_Type: Transition_Type,
        i: Int
    ) {
        urect?.setRotationAnimation(this)
        Obj = urect
        WaitTime = i
        FromRot = d
        ToRot = d2
        Time = d3.toInt()
        transition_type = transition_Type
        list?.add(this)
        ManipulateTimers()
    }

    fun ManipulateTimers(): Boolean {
        if (AnimationTimer != null) {
            return false
        }
        AnimationTimer = Timer(Time, 0)
        WaitTimer = Timer(WaitTime, 0)
        WaitTimer?.start()
        AnimationTimer?.OnTimerFinishCounting { timer ->
            Calc()
            Done = true
            timer.Remove()
        }
        AnimationTimer?.OnEveryStep { i, timer -> Calc() }
        WaitTimer?.OnTimerFinishCounting { timer ->
            AnimationTimer?.start()
            timer.Remove()
        }
        return true
    }

    fun Calc() {
        Obj?.rotate = Transition.GetValue(
            transition_type,
            AnimationTimer?.CurentTime?.toDouble()?:return,
            FromRot,
            ToRot,
            AnimationTimer?.MaxTime?.toDouble()?:return
        )
    }

    fun remove() {
        Done = true
        WaitTimer?.Remove()
        AnimationTimer?.Remove()
        list?.remove(this)
    }

    companion object {
        var list: MutableList<Rotation?>? = ArrayList()
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
                    if (!list?.get(i)?.Done!!) {
                        list?.removeAt(i)
                        i--
                    }
                    i++
                }
            }
        }
    }
}