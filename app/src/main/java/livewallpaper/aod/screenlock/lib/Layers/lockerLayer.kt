package livewallpaper.aod.screenlock.lib.Layers

import android.util.Log
import android.view.View
import livewallpaper.aod.screenlock.lib.AppConfig.AppConfig
import livewallpaper.aod.screenlock.lib.Media.Media
import livewallpaper.aod.screenlock.lib.UgameLib.Animations.Deplace
import livewallpaper.aod.screenlock.lib.UgameLib.GameAdapters.GameAdapter
import livewallpaper.aod.screenlock.lib.UgameLib.GameAdapters.Screen
import livewallpaper.aod.screenlock.lib.UgameLib.GameAdapters.Timer
import livewallpaper.aod.screenlock.lib.UgameLib.Shapes.Uimage
import livewallpaper.aod.screenlock.lib.UgameLib.Transition.Transition.GetValue
import livewallpaper.aod.screenlock.lib.UgameLib.Transition.Transition_Type
import livewallpaper.aod.screenlock.zipper.service.LockScreenService

object lockerLayer {
    var ClickDown = false
    var ClickPassword = false

    @JvmField
    var Duration = 550
    var LastY = 0.0
    var MaxSpikeRotation = 40.0

    @JvmField
    var PasswordCorrect = true
    var deplace: Deplace? = null

    @JvmField
    var locker: Uimage? = null

    @JvmField
    var transitiontype = Transition_Type.easeinOutbounce
    var unlocktype = UnlockType.Type3

    @JvmStatic
    fun CleareMemory() {
        locker = null
        deplace = null
    }

    @JvmStatic
    fun Inicial() {
        locker = Uimage(0.0, 0.0, Screen.Width / 5.0, Screen.Width / 2.4, Media.zipper)
        GameAdapter.GetMainRect()?.AddChild(locker ?: return)
        locker?.left = Screen.Width / 2.0 - (locker?.Width() ?: return) / 2.0

        locker?.addOnClickDownListner { d, d2 ->
            ClickDown = true
            if (deplace != null) {
                deplace?.remove()
            }
        }
        locker?.addOnClickUpListner { d, d2 ->
            ClickDown = false
            if ((locker?.top
                    ?: return@addOnClickUpListner) < Screen.Height / 3.5
            ) {
                Log.e("locker touch up", "touch up 1" + locker?.top)
                SllideUp()
            } else if ((locker?.top ?: return@addOnClickUpListner) >= Screen.Height / 3.5) {
                Log.e("locker touch up", "touch up 2" + locker?.top)
                if ((locker?.top ?: return@addOnClickUpListner) <= Screen.Height / 1.6) {
                    if(!PasswordCorrect && ClickPassword){
                        SllideDownBottom()
                    }else{
                        SllideDownCenter()
                    }
                } else if ((locker?.top ?: return@addOnClickUpListner) > Screen.Height / 1.6) {
                    Log.e("locker touch up", "touch up 3" + locker?.top)
                    SllideDown()
                }
            } else {
                Log.e("locker touch up", "touch up else")
                SllideDown()
            }

//            if ((locker?.top ?: return@addOnClickUpListner) > Screen.Height / 1.6) {
//                SllideDown()
//            }

        }
        locker?.addOnTouchMoveListner { urect, _, d2 ->
            if (ClickDown && d2 > urect.Height() / 2.0) {
                LastY = urect.top
                var Height = d2 - urect.Height() / 1.6
                if (!PasswordCorrect && ClickPassword && Height >= Screen.Height * 0.8) {
//                    Height = Screen.Height * 0.8
                    Log.e("tag_move", "SllideDownBottom")
                    SllideDownBottom()
                } else {
                    Log.e("tag_move", "else---11")
                    if (Height >= Screen.Height * 0.8) {
                        Log.e("tag_move", "else---if----11")
//                    Height = Screen.Height * 0.8
                        SlideDownAfterTime(1)
                    }else{
                        Log.e("tag_move", "else---else----11")
                        if(ClickPassword && Height >= Screen.Height * 0.8){
                            SllideDownBottom()
                        }
                    }
                }
                urect.top = Height
            }
        }
        locker?.OnUpdateListner { urect ->
            if (unlocktype == UnlockType.Type1) {
                UpdateType1(urect.top)
            } else if (unlocktype == UnlockType.Type2) {
                UpdateType2(urect.top)
            } else if (unlocktype == UnlockType.Type3) {
                UpdateType3(urect.top)
            }
            val i =
                if (urect.top / (Screen.Height / 2.0) * 175.0 + 50.0 > 255.0)
                    1
                else
                    if (urect.top / (Screen.Height / 2.0) * 175.0 + 50.0 == 255.0) 0
                    else -1

        }

        InitalRightPanel()
    }

    fun InitalRightPanel() {
        val d = Screen.Width
    }

    @JvmStatic
    fun SllideUp() {
        deplace = Deplace(
            locker ?: return,
            locker?.left ?: return,
            0.0,
            AppConfig.AnimationSpeed.toDouble(),
            Transition_Type.easeOutQuad,
            0.0
        )
    }

    @JvmStatic
    fun SlideDownAfterTime(i: Int) {
        Log.e("tag1", "SlideDownAfterTime")
        val timer = Timer(i, 0)
        timer.OnTimerFinishCounting { SllideDown() }
        timer.start()
    }

    fun SllideDownCenter() {
        Log.e("tag1", "SllideDown")
        var d = Screen.Height * 2.0
        var i = AppConfig.AnimationSpeed * 2
        if(!PasswordCorrect ){
            d = Screen.Height * 1.6
            i = AppConfig.AnimationSpeed
            deplace =
                Deplace(
                    locker?:return,
                    locker?.left ?: return,
                    d,
                    i.toDouble(),
                    Transition_Type.easeOutQuad,
                    0.0
                )
        }else{
            d = Screen.Height * 0.8
            i = AppConfig.AnimationSpeed
            deplace =
                Deplace(
                    locker?:return,
                    locker?.left ?: return,
                    d,
                    i.toDouble(),
                    Transition_Type.easeOutQuad,
                    0.0
                )
        }

    }

    fun SllideDownBottom() {
        Log.e("tag1", "SllideDown")
        var d = Screen.Height * 2.0
        var i = AppConfig.AnimationSpeed * 2
        if (!PasswordCorrect) {
            d = Screen.Height * 1.6
            i = AppConfig.AnimationSpeed

        } else {
//        LockScreenService.cc?.runOnUiThread {
//            LockScreenService.cc?.PlayUnzipSound()
//        }
        }
        deplace =
            Deplace(
                locker?:return,
                locker?.left ?: return,
                d,
                i.toDouble(),
                Transition_Type.easeOutQuad,
                0.0
            )
    }

    fun SllideDown() {
        try {
            Log.e("tag1", "SllideDown")
            var d = Screen.Height * 2.0
            var i = AppConfig.AnimationSpeed * 2
            if (!PasswordCorrect) {
                d = Screen.Height * 0.8
                i = AppConfig.AnimationSpeed
            } else {
                LockScreenService.cc?.runOnUiThread {
                    LockScreenService.cc?.PlayUnzipSound()
                }
            }
            deplace =
                Deplace(
                    locker?:return,
                    locker?.left ?: return,
                    d,
                    i.toDouble(),
                    Transition_Type.easeOutQuad,
                    0.0
                )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun UpdateType3(d: Double) {
        var i: Int
        actionLayer.UpdateWidgets(d)
        LockScreenService.cc?.runOnUiThread {
            if (d < Screen.Height * 0.8 || PasswordCorrect) {
                ClickPassword=true
                LockScreenService.cc?.viewStubPasswordHolder?.visibility = View.GONE
            } else {
                ClickPassword=false
                LockScreenService.cc?.viewStubPasswordHolder?.visibility = View.VISIBLE
                LockScreenService.cc?.setZipPasswordListener()
            }
        }
        var i2 = 0
        for (i3 in actionLayer.left?.indices?:return) {
            val uimagePart = actionLayer.left?.get(i3) ?: return
            if (d > uimagePart.top) {
                val d3 = Screen.Width / 2.0
                val d4 = actionLayer.space2
                val top = d - uimagePart.top
                val d5 = Screen.Height / 2.0
                var GetValue = GetValue(
                    transitiontype,
                    top,
                    d3,
                    d4,
                    d5
                )
                if (top > d5) {
                    GetValue = d4
                }
                uimagePart.setWidth(GetValue)
                uimagePart.ImageRect?.setWidth(uimagePart.Width() / Screen.Width * (uimagePart.image?.width?.toDouble()?:return))
                val uimage = uimagePart.childrens?.get(0) as Uimage
                uimage.left =
                    uimagePart.right - uimage.Width() + actionLayer.space + actionLayer.space2
                if (uimage.left > Screen.Width / 4.0) {
                    uimage.rotate = GetValue(
                        transitiontype,
                        top,
                        0.0,
                        -MaxSpikeRotation,
                        d5 / 2.0
                    )
                } else if (top < d5) {
                    val d6 = d5 / 2.0
                    uimage.rotate = GetValue(
                        transitiontype,
                        top - d6,
                        -MaxSpikeRotation,
                        0.0,
                        d6
                    )
                } else {
                    uimage.rotate = 0.0
                }
            } else {
                uimagePart.setWidth(Screen.Width / 2.0)
                val uimage2 = uimagePart.childrens?.get(0) as Uimage
                uimagePart.ImageRect?.setWidth(((uimagePart.image?.width?:return) / 2).toDouble())
                uimage2.left =
                    uimagePart.right - uimage2.Width() + actionLayer.space + actionLayer.space2
                uimage2.rotate = 0.0
            }
        }
        var i4 = 0
        while (i4 < actionLayer.right?.size?:return) {
            val uimagePart2 = actionLayer.right?.get(i4)
            val uimage3 = uimagePart2?.childrens?.get(i2) as Uimage
            if (d > uimagePart2.top) {
                val d7 = Screen.Width / 2.0 + actionLayer.space2
                var d8 = Screen.Width
                val top2 = (d - uimagePart2.top)
                val d9 = Screen.Height / 2.0
                val GetValue2 = GetValue(
                    transitiontype,
                    top2,
                    d7,
                    d8,
                    d9
                )
                if (top2 <= d9) {
                    d8 = GetValue2
                }
                uimagePart2.left = d8
                i = i4
                uimagePart2.ImageRect?.left =
                    uimagePart2.left / Screen.Width * (uimagePart2.image?.width?.toDouble()
                        ?: return) - 1.0
                if (uimagePart2.GetCenterX() < Screen.Width / 4.0 * 3.0) {
                    uimage3.rotate = GetValue(
                        transitiontype,
                        top2,
                        0.0,
                        MaxSpikeRotation,
                        d9 / 2.0
                    )
                } else if (uimagePart2.left < Screen.Width) {
                    val d10 = d9 / 2.0
                    uimage3.rotate = GetValue(
                        transitiontype,
                        top2 - d10,
                        MaxSpikeRotation,
                        0.0,
                        d10
                    )
                } else {
                    uimage3.rotate = 0.0
                }
            } else {
                i = i4
                uimagePart2.left = Screen.Width / 2.0 + actionLayer.space2
                uimagePart2.setWidth(Screen.Width / 2.0)
                uimagePart2.ImageRect?.left =
                    uimagePart2.left / Screen.Width * (uimagePart2.image?.width?.toDouble()
                        ?: return) - 1.0
                uimage3.rotate = 0.0
            }
            i4 = i + 1
            i2 = 0
        }
    }

    fun UpdateType1(d: Double) {
        for (i in actionLayer.left?.indices?:return) {
            val uimagePart = actionLayer.left?.get(i)
            if (uimagePart != null) {
                if (d > uimagePart.top) {
                    if (uimagePart != null) {
                        uimagePart.left = uimagePart.top - d - actionLayer.space2
                    }
                } else {
                    uimagePart.left = -actionLayer.space2
                    uimagePart.skewX = 0.0
                }
            }
        }
        for (i2 in actionLayer.right?.indices?:return) {
            val uimagePart2 = actionLayer.right?.get(i2)
            if (uimagePart2 != null) {
                if (d > uimagePart2.top) {
                    uimagePart2.left = Screen.Width / 2.0 + d - uimagePart2.top + actionLayer.space2
                } else {
                    uimagePart2.left = Screen.Width / 2.0 + actionLayer.space2
                }
            }
        }
    }

    fun UpdateType2(d: Double) {
        val size = Screen.Height / (actionLayer.left?.size?.toDouble() ?: return)
        for (i in actionLayer.left?.indices?:return) {
            val uimagePart = actionLayer.left?.get(i)?:return
            if (d > uimagePart.top) {
                uimagePart.setHeight(size - (d - uimagePart.top) / (Screen.Height - uimagePart.top) * size)
            } else {
                uimagePart.setHeight(size)
            }
        }
        for (i2 in actionLayer.right?.indices?:return) {
            val uimagePart2 = actionLayer.right?.get(i2)?:return
            if (d > uimagePart2.top + uimagePart2.Height() / 2.0) {
                uimagePart2.setHeight(size - (d - uimagePart2.top) / (Screen.Height - uimagePart2.top) * size)
            } else {
                uimagePart2.setHeight(size)
            }
        }
    }

    enum class UnlockType {
        Type1, Type2, Type3
    }
}