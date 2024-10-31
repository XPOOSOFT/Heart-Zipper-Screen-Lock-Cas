package livewallpaper.aod.screenlock.lib.Layers

import android.graphics.Bitmap
import android.graphics.Paint
import livewallpaper.aod.screenlock.lib.AppConfig.AppConfig
import livewallpaper.aod.screenlock.lib.Media.Media
import livewallpaper.aod.screenlock.lib.UgameLib.GameAdapters.GameAdapter
import livewallpaper.aod.screenlock.lib.UgameLib.GameAdapters.Screen
import livewallpaper.aod.screenlock.lib.UgameLib.Shapes.ULabel
import livewallpaper.aod.screenlock.lib.UgameLib.Shapes.Uimage
import livewallpaper.aod.screenlock.lib.UgameLib.Shapes.UimagePart
import livewallpaper.aod.screenlock.lib.UgameLib.Shapes.Urect
import livewallpaper.aod.screenlock.lib.UgameLib.Transition.Transition.GetValue
import livewallpaper.aod.screenlock.zipper.utilities.ConstantValues
import livewallpaper.aod.screenlock.zipper.utilities.DataBasePref
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object actionLayer {
    private var uimagePart3: UimagePart? = null
    private var urect: Urect? = null
    private var uimagePart2: UimagePart? = null
    private var d11:Double ? = null
    private var d9:Double ? = null
    private var d10:Double ? = null
    private var d3:Double ? = null
    private var d4:Double ? = null
    private var d5:Double ? = null
    private var d6:Double ? = null
    private var d7:Double ? = null
    private var bitmap: Bitmap? = null
    var uimage: Uimage? = null
    var uimage2: Uimage? = null
    var BateryCover: Uimage? = null
    var BateryHolder: Urect? = null
    var BateryLabel: ULabel? = null
    var BateryLevel: UimagePart? = null
    var Date: ULabel? = null
    var HoursMinutes: ULabel? = null
    var Seconds: ULabel? = null
    var TimeHolder: Urect? = null
    var baterryWidth = 0.0
    var c: Calendar? = null
    var count = 30
    var curFormater: SimpleDateFormat? = null
    var d: Date? = null
    var left: MutableList<UimagePart?>? = null
    var prt: UimagePart? = null
    var right: MutableList<UimagePart?>? = null
    var space = 0.0
    var space2 = 0.0


    fun Inicial() {
        space2 = Screen.Width / count.toDouble()
        val d2 = Screen.Width / (count * 3).toDouble()
        left = ArrayList()
        right = ArrayList()
        bitmap = Media.SelectedBg
        d3 = Screen.Width / 2.0
        d4 = Screen.Height / count.toDouble()
        var width = (bitmap?.width!! / 2).toDouble()
        var height = (bitmap?.height!! / count).toDouble()
        var uimagePart: UimagePart? = null
        var i = 0
        while (i < count) {
            d5 = i.toDouble()
            d6 = height
            d7 = width
            uimagePart2 = uimagePart
            urect = Urect(0.0, (d5?:return) * height, width, d6?:return)
            var d8 = (d5?:return) * (d4?:return)
            if (uimagePart2 != null) {
                d8 = uimagePart2?.bottom?:return
            }
            prt = UimagePart(-d2, d8, d3?:return, d4?:return, urect, Media.SelectedBg)
            left?.add(prt)
            uimagePart3 = prt?:return
            GameAdapter.GetMainRect()?.AddChild(prt?:return)
            uimage = Uimage(0.0, 0.0, (d4?:return) * 2.0, (d4?:return), Media.ChainLeft)
            uimage?.left = (prt?.Width()?:return) - (uimage?.Width()?:return) + d2 + (uimage?.Width()?:return) / 3.3
            prt?.AddChild(uimage)
            i++
            uimagePart = uimagePart3
            height = d6?:return
            width = d7?:return
        }
        d9 = height
        d10 = width
        var uimagePart4: UimagePart? = null
        for (i2 in 0 until count + 1) {
            d11 = i2.toDouble()
            prt = UimagePart(
                (d3?:return) + d2,
                uimagePart4?.bottom ?: (d11?:return) * (d4?:return) - (d4?:return) / 2.0,
                d3?:return,
                d4?:return,
                Urect((d10?:return), (d11?:return) * (d9?:return) - (d9?:return) / 2.0, (d10?:return), (d9?:return)),
                Media.SelectedBg
            )
            right?.add(prt)
            uimagePart4 = prt
            GameAdapter.GetMainRect()?.AddChild((prt?:return))
            uimage2 = Uimage(0.0, 0.0, (d4?:return) * 2.0, (d4?:return), Media.ChainRight)
            uimage2?.left = -d2 - (uimage2?.Width()?:return) / 3.3
            prt?.AddChild(uimage2)
        }
        CreateWidgets()
    }


    fun CleareMemory() {
        if (left != null) {
            for (i in left?.indices?:return) {
                left?.get(i)?.image = null
            }
            for (i2 in right?.indices?:return) {
                right?.get(i2)?.image = null
            }
            left?.clear()
            right?.clear()
            left = null
            right = null
            BateryCover?.image = null
            BateryLevel?.image = null
            prt?.image = null
            BateryLevel = null
            BateryCover = null
            prt = null
        }
    }

    private fun CreateWidgets() {
        curFormater = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
        TimeHolder = Urect(Screen.Width / 100.0, Screen.Width / 4.0, 0.0, 0.0)
        HoursMinutes = ULabel(Screen.Width / 50.0, 0.0, 0.0, Screen.Width / 4.0, "")
        Seconds = ULabel(Screen.Width / 36.0, Screen.Width / 10.0, 0.0, Screen.Width / 4.0, "10:05")
        Date = ULabel(Screen.Width / 36.0, Screen.Width / 6.0, 0.0, Screen.Width / 4.0, "")
        HoursMinutes?.SetTextSize(Screen.Width / 7.0)
        HoursMinutes?.setColor(AppConfig.FontColor)
        HoursMinutes?.textAlign = Paint.Align.LEFT
//        HoursMinutes?.font = Media.font1
        Seconds?.SetTextSize(Screen.Width / 10.0)
        Seconds?.textAlign = Paint.Align.LEFT
        Seconds?.setColor(AppConfig.FontColor)
//        Seconds?.font = Media.font1
        Date?.SetTextSize(Screen.Width / 18.0)
        Date?.setColor(AppConfig.FontColor)
        Date?.textAlign = Paint.Align.LEFT
//        Date?.font = Media.font1
        if (GameAdapter.ctx?.let { DataBasePref.LoadPref(ConstantValues.DActivePref, it) } != 0) {
            TimeHolder?.AddChild(Date?:return)
        }
        if (GameAdapter.ctx?.let { DataBasePref.LoadPref(ConstantValues.TActivePref, it) } != 0) {
            TimeHolder?.AddChild(HoursMinutes?:return)
           // TimeHolder?.AddChild(Seconds?:return)
        }
        GameAdapter.GetMainRect()?.AddChild(TimeHolder?:return)
        TimeHolder?.OnUpdateListner { UpdateDateAndTime() }
        baterryWidth = Screen.Width * 0.35
        BateryHolder = Urect(Screen.Width / 40.0, 0.8 * Screen.Width, Screen.Width / 2.0, 0.0)
        if (GameAdapter.ctx?.let { DataBasePref.LoadPref(ConstantValues.BateryActivePref, it) } != 0) {
          //  GameAdapter.GetMainRect()?.AddChild(BateryHolder?:return)
        }
        BateryHolder?.AddChild(BateryLevel?:return)
        BateryLabel = ULabel(
            0.0,
            BateryLevel?.relativeBottom?:return,
            BateryCover?.Width()?:return,
            BateryCover?.Height()?:return,
            "50%"
        )
        BateryLabel?.setColor(AppConfig.FontColor)
        BateryLabel?.SetTextSize(Screen.Width / 8.0)
//        BateryLabel?.font = Media.font1
        BateryHolder?.AddChild(BateryLabel?:return)
    }

    private fun UpdateDateAndTime() {
        c = Calendar.getInstance()
        var str = c?.getTime()?.hours.toString() + ""
        var str2 = c?.getTime()?.minutes.toString() + ""
        var str3 = c?.getTime()?.seconds.toString() + ""
        if (str2.length < 2) {
            str2 = "0$str2"
        }
        if (str.length < 2) {
            str = "0$str"
        }
        if (str3.length < 2) {
            str3 = "0$str3"
        }
        HoursMinutes?.text = "$str:$str2"
//        Seconds?.text = str3
        Date?.text =SimpleDateFormat("d MMM yyyy").format(Calendar.getInstance().time)
    }


    fun FixBateryLevelWidth(d2: Double) {
        val d3 = d2 / 100.0
        val uLabel = BateryLabel
        uLabel?.text = d2.toInt().toString() + "%"
        BateryLevel?.ImageRect?.setWidth((BateryLevel?.image?.width?.toDouble()?:return) * 0.97 * d3 + (BateryLevel?.image?.width?.toDouble()?:return) * 0.03)
        BateryLevel?.setWidth(baterryWidth * 0.97 * d3 + baterryWidth * 0.03)
    }

    fun UpdateWidgets(d2: Double) {
        if (d2 >= HoursMinutes?.top?:return) {
            val top = d2 - (HoursMinutes?.top?:return)
            val d3 = Screen.Width / 50.0
            val d4 = -Screen.Width / 2.0
            val d5 = lockerLayer.Duration.toDouble()
            if (top < d5) {
                HoursMinutes?.left =
                    GetValue(lockerLayer.transitiontype, top, d3, d4, d5)
            }
            if (top < d5 / 2.0) {
                HoursMinutes?.setAlpha( GetValue(
                    lockerLayer.transitiontype,
                    top,
                    255.0,
                    0.0,
                    d5 / 1.4
                )
                )
            }
        } else {
            HoursMinutes?.left = Screen.Width / 50.0
            Seconds?.setAlpha(255.0)
        }
        if (d2 >= Seconds?.top?:return) {
            val top2 = d2 - (Seconds?.top?:return)
            val d6 = Screen.Width / 36.0
            val d7 = -Screen.Width / 2.0
            val d8 = lockerLayer.Duration.toDouble()
            if (top2 < d8) {
                Seconds?.left = GetValue(
                    lockerLayer.transitiontype,
                    top2,
                    d6,
                    d7,
                    d8
                )
                if (top2 < d8 / 2.0) {
                    Seconds?.setAlpha( GetValue(
                        lockerLayer.transitiontype,
                        top2,
                        255.0,
                        0.0,
                        d8 / 1.4
                    )
                    )
                }
            }
        } else {
            Seconds?.left = Screen.Width / 36.0
            Seconds?.setAlpha(255.0)
        }
        if (d2 >= Date?.top?:return) {
            val top3 = d2 - (Date?.top?:return)
            val d10 = Screen.Width / 36.0
            val d11 = -Screen.Width / 2.0
            val d12 = lockerLayer.Duration.toDouble()
            if (top3 < d12) {
                Date?.left = GetValue(
                    lockerLayer.transitiontype,
                    top3,
                    d10,
                    d11,
                    d12
                )
                if (top3 < d12 / 2.0) {
                    Date?.setAlpha(GetValue(
                        lockerLayer.transitiontype,
                        top3,
                        255.0,
                        0.0,
                        d12 / 1.4
                    ))
                }
            }
        } else {
            Date?.left = Screen.Width / 36.0F
            Date?.setAlpha(255.0)
        }
        if (d2 >= BateryCover?.top?:return) {
            val top4 = ((d2 - (BateryCover?.top?: return)))
            val d14 = Screen.Width / 36.0
            val d15 = -Screen.Width / 2.0
            val d16 = lockerLayer.Duration.toDouble()
            if (top4 < d16) {
                val GetValue = GetValue(
                    lockerLayer.transitiontype,
                    top4,
                    d14,
                    d15,
                    d16
                )
                BateryCover?.left = GetValue
                BateryLevel?.left = GetValue
                if (top4 < d16 / 2.0) {
                    val GetValue2 = GetValue(
                        lockerLayer.transitiontype,
                        top4,
                        255.0,
                        0.0,
                        d16 / 1.4
                    )
                    BateryCover?.setAlpha(GetValue2)
                    BateryLevel?.setAlpha(GetValue2)
                }
            }
        } else {
            BateryCover?.left = Screen.Width / 36.0
            BateryCover?.setAlpha(255.0)
            BateryLevel?.left = Screen.Width / 36.0
            BateryLevel?.setAlpha(255.0)
        }
        if (d2 >= BateryLabel?.top?:return) {
            val top5 = d2 - (BateryLabel?.top?:return)
            val d18 = Screen.Width / 36.0
            val d19 = -Screen.Width / 2.0
            val d20 = lockerLayer.Duration.toDouble()
            if (top5 < d20) {
                BateryLabel?.left = GetValue(
                    lockerLayer.transitiontype,
                    top5,
                    d18,
                    d19,
                    d20
                )
                if (top5 < d20 / 2.0) {
                    BateryLabel?.setAlpha(GetValue(
                        lockerLayer.transitiontype,
                        top5,
                        255.0,
                        0.0,
                        d20 / 1.4
                    ))
                    return
                }
                return
            }
            return
        }
        BateryLabel?.left = Screen.Width / 36.0
        BateryLabel?.setAlpha(255.0)
    }
}