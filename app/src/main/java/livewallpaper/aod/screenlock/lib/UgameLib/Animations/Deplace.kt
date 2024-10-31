package livewallpaper.aod.screenlock.lib.UgameLib.Animations

import livewallpaper.aod.screenlock.lib.UgameLib.GameAdapters.Timer
import livewallpaper.aod.screenlock.lib.UgameLib.Shapes.Urect
import livewallpaper.aod.screenlock.lib.UgameLib.Transition.Transition
import livewallpaper.aod.screenlock.lib.UgameLib.Transition.Transition_Type


class Deplace {
    var AnimationTimer: Timer? = null
    var CurentTime: Int
    var Done = false
    var Fx: Double
    var Fy: Double
    var JustX = false
    var JustY = false
    var Obj: Urect? = null
    var Time: Int
    var Tx: Double
    var Ty: Double
    var Wait = 0
    var WaitTimer: Timer? = null
    var transition_type: Transition_Type

    constructor(
        urect: Urect?,
        d: Double,
        d2: Double,
        d3: Double,
        d4: Double,
        d5: Double,
        transition_Type: Transition_Type,
        d6: Double,
        z: Boolean,
        z2: Boolean
    ) {
        if (Obj != null) {
            Obj?.setDeplaceAnnimation(this)
        }
        Obj = urect
        Fx = d
        Fy = d2
        Tx = d3
        Ty = d4
        Time = d5.toInt()
        CurentTime = 0
        transition_type = transition_Type
        if (list == null) {
            list = ArrayList()
        }
        list?.add(this)
        Wait = d6.toInt()
        ManipulateTimers()
    }

    fun ManipulateTimers(): Boolean {
        if (Obj != null) {
            Obj?.setDeplaceAnnimation(this)
        }
        if (AnimationTimer != null) {
            return false
        }
        AnimationTimer = Timer(Time, 0)
        WaitTimer = Timer(Wait, 0)
        WaitTimer?.start()
        AnimationTimer?.OnTimerFinishCounting { timer ->
            Calc()
            Done = true
            timer?.Remove()
        }
        AnimationTimer?.OnEveryStep { i, timer -> Calc() }
        WaitTimer?.OnTimerFinishCounting { timer ->
            AnimationTimer?.start()
            timer?.Remove()
        }
        return true
    }

    constructor(
        urect: Urect?,
        d: Double,
        d2: Double,
        d3: Double,
        d4: Double,
        d5: Double,
        transition_Type: Transition_Type,
        d6: Double
    ) {
        if (Obj != null) {
            Obj?.setDeplaceAnnimation(this)
        }
        Obj = urect
        Fx = d
        Fy = d2
        Tx = d3
        Ty = d4
        Time = d5.toInt()
        CurentTime = 0
        transition_type = transition_Type
        if (list == null) {
            list = ArrayList()
        }
        list?.add(this)
        Wait = d6.toInt()
        JustX = JustX
        JustY = JustY
        ManipulateTimers()
    }

    constructor(
        urect: Urect,
        d: Double,
        d2: Double,
        d3: Double,
        transition_Type: Transition_Type,
        d4: Double
    ) {
        if (Obj != null) {
            Obj?.setDeplaceAnnimation(this)
        }
        Obj = urect
        Fx = urect.relativeLeft
        Fy = urect.relativeTop
        Tx = d
        Ty = d2
        Time = d3.toInt()
        CurentTime = 0
        transition_type = transition_Type
        if (list == null) {
            list = ArrayList()
        }
        list?.add(this)
        Wait = d4.toInt()
        ManipulateTimers()
    }

    constructor(
        urect: Urect,
        d: Double,
        d2: Double,
        d3: Double,
        transition_Type: Transition_Type,
        d4: Double,
        z: Boolean,
        z2: Boolean
    ) {
        if (Obj != null) {
            Obj?.setDeplaceAnnimation(this)
        }
        Obj = urect
        Fx = urect.relativeLeft
        Fy = urect.relativeTop
        Tx = d
        Ty = d2
        Time = d3.toInt()
        CurentTime = 0
        transition_type = transition_Type
        if (list == null) {
            list = ArrayList()
        }
        list?.add(this)
        Wait = d4.toInt()
        JustX = z
        JustY = z2
        ManipulateTimers()
    }

    fun Calc() {
        if (!JustY && !JustX) {
            Obj?.left = Transition.GetValue(
                transition_type,
                AnimationTimer?.CurentTime?.toDouble()?:return,
                Fx,
                Tx,
                AnimationTimer?.MaxTime?.toDouble()?:return
            )
            Obj?.top = Transition.GetValue(
                transition_type,
                AnimationTimer?.CurentTime?.toDouble()?:return,
                Fy,
                Ty,
                AnimationTimer?.MaxTime?.toDouble()?:return
            )
        } else if (JustX) {
            Obj?.left = Transition.GetValue(
                transition_type,
                AnimationTimer?.CurentTime?.toDouble()?:return,
                Fx,
                Tx,
                AnimationTimer?.MaxTime?.toDouble()?:return
            )
        } else if (JustY) {
            Obj?.top = Transition.GetValue(
                transition_type,
                AnimationTimer?.CurentTime?.toDouble()?:return,
                Fy,
                Ty,
                AnimationTimer?.MaxTime?.toDouble()?:return
            )
        }
    }

    fun PauseAnimation() {
        if (WaitTimer != null) {
            WaitTimer?.pause()
        }
        if (AnimationTimer != null) {
            AnimationTimer?.pause()
        }
    }

    fun ResumeAnimation() {
        if (WaitTimer != null) {
            WaitTimer?.resume()
        }
        if (AnimationTimer != null) {
            AnimationTimer?.resume()
        }
    }

    fun finish() {
        if (WaitTimer != null) {
            WaitTimer?.pause()
            WaitTimer?.Remove()
        }
        if (AnimationTimer != null) {
            AnimationTimer?.pause()
            AnimationTimer?.Remove()
        }
    }

    fun remove() {
        if (AnimationTimer != null) {
            AnimationTimer?.Remove()
            WaitTimer?.Remove()
        }
        Done = true
        list?.remove(this)
    }

    companion object {
        var list: MutableList<Deplace?>? = ArrayList()
        fun DeleteIfExist(urect: Urect): Boolean {
            for (i in list?.indices?:return false) {
                if (list?.get(i)?.Obj === urect) {
                    list?.get(i)?.Done = true
                    return true
                }
            }
            return false
        }

        fun Exist(urect: Urect): Boolean {
            for (i in list?.indices?:return false) {
                if (list?.get(i)?.Obj === urect) {
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
                        if (!list?.get(i)?.Done!!) {
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