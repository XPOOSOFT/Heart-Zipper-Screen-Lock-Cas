package livewallpaper.aod.screenlock.lib.UgameLib.Animations

import livewallpaper.aod.screenlock.lib.UgameLib.GameAdapters.Timer
import livewallpaper.aod.screenlock.lib.UgameLib.Shapes.Urect
import livewallpaper.aod.screenlock.lib.UgameLib.Transition.Transition
import livewallpaper.aod.screenlock.lib.UgameLib.Transition.Transition_Type


class Fade {
    var AnimationTimer: Timer? = null
    var CurentTime = 0
    var Done = false
    var FromApha: Double
    var Obj: Urect? = null
    var Time: Int
    var ToAlpha: Double
    var WaitTime = 0
    var WaitTimer: Timer? = null
    var transition_type: Transition_Type

    constructor(
        urect: Urect?,
        d: Double,
        d2: Double,
        d3: Double,
        transition_Type: Transition_Type,
    ) {
        var d = d
        var d2 = d2
        if (Obj != null) {
            Obj?.setFadeAnnimation(this)
        }
        Obj = urect
        d = if (d > 255.0) 255.0 else d
        d2 = if (d2 < 0.0) 0.0 else d2
        FromApha = d
        ToAlpha = d2
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
        i: Int,
    ) {
        var d = d
        var d2 = d2
        if (Obj != null) {
            Obj?.setFadeAnnimation(this)
        }
        Obj = urect
        WaitTime = i
        d = if (d > 255.0) 255.0 else d
        d2 = if (d2 < 0.0) 0.0 else d2
        FromApha = d
        ToAlpha = d2
        Time = d3.toInt()
        transition_type = transition_Type
        list?.add(this)
        ManipulateTimers()
    }

    fun ManipulateTimers(): Boolean {
        if (Obj != null) {
            Obj?.setFadeAnnimation(this)
        }
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
        Obj?.setAlpha(
            Transition.GetValue(
            transition_type,
            AnimationTimer?.CurentTime?.toDouble()?:return,
            FromApha,
            ToAlpha,
            AnimationTimer?.MaxTime?.toDouble()?:return
        ))
    }

    fun Remove() {
        if (AnimationTimer != null) {
            AnimationTimer?.Remove()
            WaitTimer?.Remove()
        }
        Done = true
        list?.remove(this)
    }

    companion object {
        var list: MutableList<Fade?>? = ArrayList()
        fun DeleteIfExist(urect: Urect?): Boolean {
            return false
        }

        fun update() {
            if (list != null) {
                var i = 0
                while (i < list?.size?:return) {
                    val fade = list?.get(i)?:return
                    if (!fade.Done) {
                        list?.remove(fade)
                        i--
                    }
                    i++
                }
            }
        }
    }
}