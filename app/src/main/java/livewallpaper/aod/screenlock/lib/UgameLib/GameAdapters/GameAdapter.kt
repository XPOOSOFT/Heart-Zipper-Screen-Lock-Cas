package livewallpaper.aod.screenlock.lib.UgameLib.GameAdapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.os.Handler
import android.util.Log
import androidx.core.internal.view.SupportMenu.CATEGORY_MASK
import livewallpaper.aod.screenlock.lib.AppConfig.AppConfig
import livewallpaper.aod.screenlock.lib.Layers.BackgroundLayer
import livewallpaper.aod.screenlock.lib.Layers.BackgroundLayer.clearMemory
import livewallpaper.aod.screenlock.lib.Layers.actionLayer
import livewallpaper.aod.screenlock.lib.Layers.lockerLayer
import livewallpaper.aod.screenlock.lib.Media.Media.Clear
import livewallpaper.aod.screenlock.lib.Media.Media.inicial
import livewallpaper.aod.screenlock.lib.UgameLib.Animations.AnimationAdapter
import livewallpaper.aod.screenlock.lib.UgameLib.Animations.AnimationAdapter.Init
import livewallpaper.aod.screenlock.lib.UgameLib.Animations.Fade
import livewallpaper.aod.screenlock.lib.UgameLib.Resource.Resources
import livewallpaper.aod.screenlock.lib.UgameLib.Shapes.ULabel
import livewallpaper.aod.screenlock.lib.UgameLib.Shapes.Urect
import livewallpaper.aod.screenlock.lib.UgameLib.Transition.Transition_Type
import livewallpaper.aod.screenlock.zipper.service.LockScreenService
import livewallpaper.aod.screenlock.zipper.utilities.ConstantValues.SpeedActivePref
import livewallpaper.aod.screenlock.zipper.utilities.DataBasePref.LoadPref

@SuppressLint("StaticFieldLeak")
object GameAdapter {
    var Background: Urect? = null
    var Inicialed = false
    var Ispaused = true
    var ObjCount: ULabel? = null
    var Scene: Urect? = null
    var btesla: String? = null
    var closing = false

    @JvmField
    var ctx: Context? = null

    @JvmField
    var drawer: Drawer? = null
    var finalFinishCalled = false

    @JvmField
    var finalFinishReached = false
    var itesla: String? = null
    var looper: ULooper? = null
    var readyToClose = false

    @JvmField
    var stopUpdates = false
    fun GetMainRect(): Urect? {
        return Background
    }

    @SuppressLint("RestrictedApi")
    @JvmStatic
    fun StartGame(context: Context): Boolean {
        Ispaused = true
        closing = false
        ctx = context
        readyToClose = false
        Inicialed = true
        Screen.Inicialize()
        Resources.Inicial()
        inicial()
        Background = Urect(0.0, 0.0, Screen.Width, Screen.Height, 0)
        Scene = Urect(0.0, 0.0, Screen.Width, Screen.Height, 0)
        Scene?.AddChild(Background)
        Scene?.setColor(0)
        Scene?.alpha = 255.0
        ObjCount = ULabel(0.0, 0.0, Screen.Width, Screen.Height, "0")
        ObjCount?.SetTextSize(Screen.Width / 10.0)
        ObjCount?.setColor(CATEGORY_MASK)
        ObjCount?.OnUpdateListner { urect: Urect? -> }
        finalFinishReached = false
        finalFinishCalled = false
        stopUpdates = false
        Init()
        drawer?.isSoundEffectsEnabled = false
        looper = ULooper()
        AppConfig.AnimationSpeed = 500 - LoadPref(SpeedActivePref, context) + 150
        BackgroundLayer.Inicial()
        actionLayer.Inicial()
        lockerLayer.Inicial()
        drawer?.addOnDrawListner { canvas -> Draw(canvas) }
        Background?.addOnTouchMoveListner { urect: Urect?, d: Double, d2: Double -> }
        Resume()
        Pause()
        return true
    }

    @SuppressLint("ClickableViewAccessibility")
    @JvmStatic
    fun SetListeners() {
        drawer?.setOnClickListener { Urect.CheckRectsClickUp() }
        drawer?.setOnTouchListener { view, motionEvent ->
            if (motionEvent != null && view!==null) {
                if (motionEvent.action == 0) {
                    GetMainRect()?.checkClickDown(
                        motionEvent.x.toDouble(),
                        motionEvent.y.toDouble()
                    )
                    false
                } else if (motionEvent.action != 2) {
                    false
                } else {
                    Urect.CheckRectTouchMove(motionEvent.x.toDouble(), motionEvent.y.toDouble())
                    false
                }
            }
            false
        }
    }

    @JvmStatic
    fun Pause(): Boolean {
        if (Ispaused) {
            Log.i("GAdapter pause skiped", "gameAdapter pause skiped")
            return false
        }
        Log.i("gameAdapter pause", "gameAdapter pause")
        if (drawer == null) {
            Log.i("drawer null 1", "drawer null 1")
            return false
        }
        drawer?.pause()
        if (looper != null) {
            looper?.Pause()
        }
        looper = null
        Ispaused = true
        return false
    }

    @JvmStatic
    fun Resume(): Boolean {
        if (!Ispaused) {
            Log.i("GAdapter resume skiped", "gameAdapter resume skiped")
            return false
        }
        Log.i("gameAdapter Resume", "gameAdapter Resume")
        if (drawer == null) {
            Log.i("drawer null 1", "drawer null 1")
            return false
        }
        Ispaused = false
        drawer?.resume()
        looper = ULooper()
        looper?.Resume()
        return true
    }

    @JvmStatic
    fun Update(): Boolean {
        try {
            return if (finalFinishReached && !stopUpdates) {
                Log.e("tag6", "enter final finish")
                if (!finalFinishCalled) {
                    finalFinishCalled = true
                    LockScreenService.cc?.finish()
                    stopUpdates = true
                    return false
                }
                closing = true
                closeThatShet()
                true
            } else if (stopUpdates) {
                false
            } else {
                AnimationAdapter.Update()
                Timer.Update()
                val GetMainRect = GetMainRect()
                GetMainRect?.CheckObjUpdates()
                if (!closing && lockerLayer.locker != null && (lockerLayer.locker?.top
                        ?: return false) >= Screen.Height * 1.7
                ) {
                    closing = true
                    closeThatShet()
                }
                true
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    private fun closeThatShet() {
        Fade(Scene, Scene?.alpha ?: return, 0.0, 200.0, Transition_Type.Special4)
        val timer = Timer(220, 0)
        timer.start()
        timer.OnTimerFinishCounting {
            Handler(ctx?.mainLooper ?: return@OnTimerFinishCounting).post {
                LockScreenService.cc?.finish()
                Inicialed = false
            }
        }
    }

    private fun Draw(canvas: Canvas?) {
        if (Scene != null) {
            Scene?.Draw(canvas)
        }
    }

    @JvmStatic
    fun close() {
        Inicialed = false
        actionLayer.CleareMemory()
        lockerLayer.CleareMemory()
        AnimationAdapter.CleareMemory()
        clearMemory()
        if (drawer != null) {
            drawer?.pause()
            drawer?.stopDrawing()
            drawer?.IsFinished = true
        }
        if (looper != null) {
            looper?.Pause()
        }
        if (GetMainRect() != null) {
            GetMainRect()?.clearChilds()
            GetMainRect()?.Delete()
        }
        Timer.Clear()
        Clear()
        Background = null
        Scene = null
        drawer = null
        looper = null
        ObjCount = null
        ctx = null
    }

    fun UnlockTrue() {
        closeThatShet()
    }

}