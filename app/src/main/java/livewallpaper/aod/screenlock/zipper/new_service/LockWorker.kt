package livewallpaper.aod.screenlock.zipper.new_service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.content.Context.VIBRATOR_SERVICE
import android.content.Context.WINDOW_SERVICE
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.ServiceInfo
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.Point
import android.media.SoundPool
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.Vibrator
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewStub
import android.view.WindowManager
import android.view.WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
import android.view.WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
import android.view.WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY
import android.view.inputmethod.InputMethodManager
import android.view.inputmethod.InputMethodManager.HIDE_IMPLICIT_ONLY
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.RemoteViews
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.clap.whistle.phonefinder.utilities.DbHelper
import com.skydoves.powerspinner.PowerSpinnerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import livewallpaper.aod.screenlock.lib.Layers.lockerLayer
import livewallpaper.aod.screenlock.lib.Layers.lockerLayer.SlideDownAfterTime
import livewallpaper.aod.screenlock.lib.Layers.lockerLayer.SllideUp
import livewallpaper.aod.screenlock.lib.UgameLib.GameAdapters.Drawer
import livewallpaper.aod.screenlock.lib.UgameLib.GameAdapters.GameAdapter
import livewallpaper.aod.screenlock.lib.UgameLib.GameAdapters.GameAdapter.Resume
import livewallpaper.aod.screenlock.lib.UgameLib.GameAdapters.GameAdapter.SetListeners
import livewallpaper.aod.screenlock.lib.UgameLib.GameAdapters.GameAdapter.StartGame
import livewallpaper.aod.screenlock.lib.UgameLib.GameAdapters.GameAdapter.close
import livewallpaper.aod.screenlock.zipper.MainActivity
import livewallpaper.aod.screenlock.zipper.R
import livewallpaper.aod.screenlock.zipper.service.LockScreenService
import livewallpaper.aod.screenlock.zipper.service.LockScreenService.Companion.cc
import livewallpaper.aod.screenlock.zipper.service.LockScreenService.Companion.lockStarted
import livewallpaper.aod.screenlock.zipper.new_service.OverlayWorker
import livewallpaper.aod.screenlock.zipper.utilities.AppAdapter.IsSoundActive
import livewallpaper.aod.screenlock.zipper.utilities.AppAdapter.IsVibrateActive
import livewallpaper.aod.screenlock.zipper.utilities.NOTIFY_CHANNEL_ID
import livewallpaper.aod.screenlock.zipper.utilities.PasswordAdapter.LoadPassword
import livewallpaper.aod.screenlock.zipper.utilities.PasswordAdapter.checkPasswordAct
import livewallpaper.aod.screenlock.zipper.utilities.SECURITY_ANS
import livewallpaper.aod.screenlock.zipper.utilities.SECURITY_QUESTION
import livewallpaper.aod.screenlock.zipper.utilities.ZIPPER_SOUND
import livewallpaper.aod.screenlock.zipper.utilities.isSplash
import java.util.concurrent.TimeUnit

class LockWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    private var sq: String? = ""
    private var sa: String? = ""
    private var viewStubDrawer: ViewStub? = null
    var viewStubPasswordHolder: ViewStub? = null
    private var viewStubQuestionHolder: ViewStub? = null
    private var count: Int = 0
    private var applyPassword = ""
    private var Passcode: String? = ""
    private var mOverlay: RelativeLayout? = null
    private var beep = 0
    private var soundActive = false
    private var svsHandler: Handler? = null
    private var time: TextView? = null
    private var vibrateActive = false
    private var vibrator: Vibrator? = null
    private var sharedPrefUtils: DbHelper? = null
    var soundPool: SoundPool? = null

    override suspend fun doWork(): Result {
        try {

            isSplash = false
            val notification = createNotification()
            setForeground(ForegroundInfo(1, notification))
            svsHandler = Handler()
            InitialWindowAndObjects()
            viewStubDrawer = mOverlay?.findViewById(R.id.viewStubMain)
            viewStubPasswordHolder = mOverlay?.findViewById(R.id.viewStubPasswordHolder)
            viewStubQuestionHolder = mOverlay?.findViewById(R.id.viewStubSecuritydHolder)
            sharedPrefUtils = DbHelper(applicationContext)
            Passcode = LoadPassword(applicationContext)
            sq = sharedPrefUtils?.getStringData(applicationContext, SECURITY_QUESTION, "")
            sa = sharedPrefUtils?.getStringData(applicationContext, SECURITY_ANS, "")
            lockerLayer.PasswordCorrect = checkPasswordAct(applicationContext) != true
            soundPool = build(10, 3, 1)
            vibrator = applicationContext.getSystemService(VIBRATOR_SERVICE) as Vibrator
            beep =
                soundPool?.load(
                    applicationContext,
                    loadSound(sharedPrefUtils?.getTone(applicationContext, ZIPPER_SOUND) ?: 0),
                    1
                )
                    ?: R.raw.unzip
            soundActive = IsSoundActive(applicationContext)
            vibrateActive = IsVibrateActive(applicationContext)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return Result.success()
    }

//    override fun onDestroy() {
//        svsHandler?.removeCallbacksAndMessages(null)
////        Log.e("LockScreenService", "on destroy: ")
//        isSplash = true
//        cc = null
//        mOverlay = null
//        viewStubQuestionHolder = null
//        viewStubPasswordHolder = null
//        viewStubDrawer = null
//        GameAdapter.drawer = null
//        soundPool?.release()
//        soundPool = null
//        close()
//        super.onDestroy()
////        System.gc()
//    }

    fun build(i: Int, i2: Int, i3: Int): SoundPool {
        return SoundPool.Builder().build()
    }

    private fun loadSound(position: Int): Int {
        when (position) {
            0 -> {
                return R.raw.unzip
            }

            1 -> {
                return R.raw.unzip1
            }

            2 -> {
                return R.raw.unzip2
            }

            3 -> {
                return R.raw.unzip3
            }

            4 -> {
                return R.raw.unzip5
            }

            5 -> {
                return R.raw.unzip6
            }

            6 -> {
                return R.raw.unzip7
            }


        }
        return R.raw.unzip
    }

    private fun lockFromPasswordCorect() {
        lockerLayer.PasswordCorrect = false
        if (viewStubPasswordHolder != null) {
            (applicationContext.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
                viewStubPasswordHolder?.windowToken, HIDE_IMPLICIT_ONLY
            )
        }
        SllideUp()
    }

    private fun UnlockFromPasswordCorect() {
        lockerLayer.PasswordCorrect = true
        if (viewStubPasswordHolder != null) {
            (applicationContext.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
                viewStubPasswordHolder?.windowToken, HIDE_IMPLICIT_ONLY
            )
        }
        SlideDownAfterTime(10)
    }

    fun PlayUnzipSound() {
        try {
            if (soundActive) {
                soundPool?.play(beep, 1.0f, 1.0f, 0, 0, 1.0f)
            }
            Vibrate()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun InitialWindowAndObjects() {
        try {
            val layoutParams = WindowManager.LayoutParams(
                MATCH_PARENT, MATCH_PARENT,
                if (Build.VERSION.SDK_INT >= 26)
                    TYPE_APPLICATION_OVERLAY else TYPE_SYSTEM_OVERLAY,
                FLAG_LAYOUT_IN_SCREEN,
                PixelFormat.RGB_888
            )
            layoutParams.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
            layoutParams.gravity = Gravity.TOP
            val lockScreenService = cc ?: return
            mOverlay =
                (lockScreenService.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(
                    R.layout.zipper_lock_screen_new,
                    null as ViewGroup?
                ) as RelativeLayout
            val windowManager = cc?.getSystemService(WINDOW_SERVICE) as WindowManager
            val defaultDisplay = windowManager.defaultDisplay
            layoutParams.flags = (WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                    or FLAG_LAYOUT_IN_SCREEN
                    or WindowManager.LayoutParams.FLAG_FULLSCREEN
                    )
            val point = Point()
            defaultDisplay.getSize(point)
            if (point.x > point.y) {
                layoutParams.height = point.x
                layoutParams.width = point.y
            } else {
                layoutParams.height = point.y
                layoutParams.width = point.x
            }
            layoutParams.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            windowManager.addView(mOverlay, layoutParams)
            viewStubDrawer = mOverlay?.findViewById(R.id.viewStubMain)
            viewStubDrawer?.visibility = View.VISIBLE
            GameAdapter.drawer = mOverlay?.findViewById<View>(R.id.drawer) as Drawer
            StartGame(applicationContext)
            SetListeners()
            Resume()
        } catch (unused: Exception) {
            Log.i("Probleem a3chiri", "Probleem a3chiri")
        }
    }

    private fun Vibrate() {
        CoroutineScope(Dispatchers.Default).launch {
            if (vibrateActive) {
                vibrator?.vibrate(500)
            }
        }
    }

    fun runOnUiThread(runnable: Runnable?) {
        Handler(Looper.getMainLooper()).post(runnable ?: return)
    }

    fun finish() {
        try {
            GameAdapter.stopUpdates = true
            viewStubDrawer = null
            viewStubPasswordHolder = null
            viewStubQuestionHolder = null
            close()
            (cc?.getSystemService(WINDOW_SERVICE) as WindowManager).removeViewImmediate(
                mOverlay
            )
//            stopForeground(true)
//            stopSelf()
            cc = null
            lockStarted = false
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setZipPasswordListener() {
        mOverlay?.findViewById<ImageView>(R.id.goldNum0)?.setOnClickListener()
        {
            applyPassword += "0"
            passwordDotViews(applyPassword.length)
        }
        mOverlay?.findViewById<ImageView>(R.id.goldNum1)?.setOnClickListener()
        {
            applyPassword += "1"
            passwordDotViews(applyPassword.length)

        }
        mOverlay?.findViewById<ImageView>(R.id.goldNum2)?.setOnClickListener()
        {
            applyPassword += "2"
            passwordDotViews(applyPassword.length)

        }
        mOverlay?.findViewById<ImageView>(R.id.goldNum3)?.setOnClickListener()
        {
            applyPassword += "3"

            passwordDotViews(applyPassword.length)
        }
        mOverlay?.findViewById<ImageView>(R.id.goldNum4)?.setOnClickListener()
        {
            applyPassword += "4"
            passwordDotViews(applyPassword.length)

        }
        mOverlay?.findViewById<ImageView>(R.id.goldNum5)?.setOnClickListener()
        {
            applyPassword += "5"
            passwordDotViews(applyPassword.length)

        }
        mOverlay?.findViewById<ImageView>(R.id.goldNum6)?.setOnClickListener()
        {
            applyPassword += "6"
            passwordDotViews(applyPassword.length)

        }
        mOverlay?.findViewById<ImageView>(R.id.goldNum7)?.setOnClickListener()
        {
            applyPassword += "7"
            passwordDotViews(applyPassword.length)

        }
        mOverlay?.findViewById<ImageView>(R.id.goldNum8)?.setOnClickListener()
        {
            applyPassword += "8"
            passwordDotViews(applyPassword.length)

        }
        mOverlay?.findViewById<ImageView>(R.id.goldNum9)?.setOnClickListener()
        {
            applyPassword += "9"
            passwordDotViews(applyPassword.length)

        }
        mOverlay?.findViewById<TextView>(R.id.goldBackBtn)?.setOnClickListener()
        {
            if (applyPassword.length > 1) {
                applyPassword = applyPassword.substring(0, applyPassword.length - 1)
                passwordDotViews(applyPassword.length)
            } else {
                applyPassword = ""
                passwordDotViews(0)
            }
        }
        mOverlay?.findViewById<ImageView>(R.id.goldNumX)?.setOnClickListener()
        {
            applyPassword = ""
            passwordDotViews(0)
            lockFromPasswordCorect()
        }

    }

    private fun passwordDotViews(passwordLength: Int) {
        vibrator?.vibrate(100)
        when (passwordLength) {
            0 -> {
                setDotVisibility(
                    R.drawable.gold_unfill_dot,
                    R.drawable.gold_unfill_dot,
                    R.drawable.gold_unfill_dot,
                    R.drawable.gold_unfill_dot
                )
            }

            1 -> {
                setDotVisibility(
                    R.drawable.gold_fill_dot,
                    R.drawable.gold_unfill_dot,
                    R.drawable.gold_unfill_dot,
                    R.drawable.gold_unfill_dot
                )
            }

            2 -> {
                setDotVisibility(
                    R.drawable.gold_fill_dot,
                    R.drawable.gold_fill_dot,
                    R.drawable.gold_unfill_dot,
                    R.drawable.gold_unfill_dot
                )

            }

            3 -> {
                setDotVisibility(
                    R.drawable.gold_fill_dot,
                    R.drawable.gold_fill_dot,
                    R.drawable.gold_fill_dot,
                    R.drawable.gold_unfill_dot
                )
            }

            4 -> {
                setDotVisibility(
                    R.drawable.gold_fill_dot,
                    R.drawable.gold_fill_dot,
                    R.drawable.gold_fill_dot,
                    R.drawable.gold_fill_dot
                )
                isPasswordWrite()

            }
        }

    }

    private fun setDotVisibility(dot1: Int, dot2: Int, dot3: Int, dot4: Int) {
        mOverlay?.findViewById<ImageView>(R.id.dotGold1)?.setImageResource(dot1)
        mOverlay?.findViewById<ImageView>(R.id.goldDot2)?.setImageResource(dot2)
        mOverlay?.findViewById<ImageView>(R.id.goldDot3)?.setImageResource(dot3)
        mOverlay?.findViewById<ImageView>(R.id.goldDot4)?.setImageResource(dot4)
    }

    private fun isPasswordWrite() {

        if (applyPassword == Passcode) {
            UnlockFromPasswordCorect()
            applyPassword = ""
        } else {
            ++count
            if (count < 4) {
                Toast.makeText(applicationContext, "Invalid Password", Toast.LENGTH_SHORT).show()
                applyPassword = ""
            } else {
                if (sa.equals("")) {
                    applyPassword = ""
                    Toast.makeText(applicationContext, "Invalid Password", Toast.LENGTH_SHORT).show()
                    return
                }
                applyPassword = ""
                viewStubPasswordHolder?.visibility = View.GONE
//                viewStubQuestionHolder =mOverlay?.findViewById<View>(R.id.mainsecurity) as ConstraintLayout
//                securitQuestion =mOverlay?.findViewById<View>(R.id.mainsecurity) as ConstraintLayout
                viewStubQuestionHolder?.visibility = View.VISIBLE
                val no = mOverlay?.findViewById<Button>(R.id.cancl_btn)
                val yes = mOverlay?.findViewById<Button>(R.id.cnfrm_del_btn)
                val editTextText = mOverlay?.findViewById<EditText>(R.id.editTextText)
                val powerSpinnerView =
                    mOverlay?.findViewById<PowerSpinnerView>(R.id.powerSpinnerView)

                no?.setOnClickListener {
                    viewStubQuestionHolder?.visibility = View.GONE
                    viewStubPasswordHolder?.visibility = View.VISIBLE
                }
                yes?.setOnClickListener {
                    Log.d("security_question", "isPasswordWrite:1 $sq")
                    Log.d("security_question", "isPasswordWrite:1 $sa")
                    Log.d(
                        "security_question",
                        "isPasswordWrite:2 ${powerSpinnerView?.text?.toString()}"
                    )
                    Log.d(
                        "security_question",
                        "isPasswordWrite:2 ${editTextText?.text?.toString()}"
                    )

                    if (editTextText?.text?.toString()?.isEmpty() == true) {
                        editTextText.error = applicationContext.getText(R.string.empty_field)
                        return@setOnClickListener
                    }
                    if (editTextText?.text?.toString()
                            .equals(sa) && powerSpinnerView?.text?.toString()?.equals(sq) == true
                    ) {
                        viewStubQuestionHolder?.visibility = View.GONE
                        viewStubPasswordHolder?.visibility = View.GONE
                        UnlockFromPasswordCorect()
                    } else {
                        editTextText?.error = applicationContext.getText(R.string.wrong_ans)
                    }
                }
            }
        }

        setDotVisibility(
            R.drawable.gold_unfill_dot,
            R.drawable.gold_unfill_dot,
            R.drawable.gold_unfill_dot,
            R.drawable.gold_unfill_dot
        )

    }

    private fun createNotification(): Notification {
        val channelId = "lock_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Lock Tasks", NotificationManager.IMPORTANCE_LOW)
            val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
        return NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Lock Screen Active")
            .setContentText("Performing lock task...")
            .build()
    }
 /*   private fun startForeground() {
        createNotificationChannel()
        val remoteViews = RemoteViews(applicationContext.packageName, R.layout.notification_collapsed)
        var z = false
        val activity = PendingIntent.getActivity(
            applicationContext,
            0,
            Intent(applicationContext, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        remoteViews.setTextViewText(R.id.text_view_collapsed_2, "Lockscreen working")
        val i = applicationContext.resources.configuration.uiMode and 48
        if (!(i == 0 || i == 16 || i != 32)) {
            z = true
        }
        if (z) {
            remoteViews.setTextColor(R.id.text_view_collapsed_2, Color.parseColor("#FFFFFF"))
            remoteViews.setTextColor(R.id.text_view_collapsed_1, Color.parseColor("#FFFFFF"))
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            startForeground(
                5,
                NotificationCompat.Builder(
                    applicationContext,
                    NOTIFY_CHANNEL_ID
                ).setOngoing(true)
                    .setColor(-1)
                    .setSmallIcon(R.drawable.ic_notification)
                    .setCustomContentView(remoteViews)
                    .setContentIntent(activity).build(),
                ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
            )
        } else {
            startForeground(
                5,
                NotificationCompat.Builder(
                    applicationContext,
                    NOTIFY_CHANNEL_ID
                ).setOngoing(true)
                    .setColor(-1)
                    .setSmallIcon(R.drawable.ic_notification)
                    .setContentIntent(activity).build()
            )
        }
    }*/

/*    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= 26) {
            val channel = NotificationChannel(
                NOTIFY_CHANNEL_ID,
                "Foreground Service Channel",
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(
                channel
            )
        }
    }*/

    companion object {
        @SuppressLint("StaticFieldLeak")
        var cc: LockScreenService? = null
        var lockStarted = false

        @JvmStatic
        fun Stop(context: Context) {
            context.stopService(Intent(context, LockScreenService::class.java))
        }

        @JvmStatic
        fun Start(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                try {
                    context.startForegroundService(Intent(context, LockScreenService::class.java))
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                try {
                    context.startService(Intent(context, LockScreenService::class.java))
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        private fun enqueueLockScreenTaskWithWorkManager(context: Context) {
            val overlayWorkRequest = OneTimeWorkRequestBuilder<OverlayWorker>()
                .setInitialDelay(200, TimeUnit.MILLISECONDS) // Just an example delay
                .build()

            WorkManager.getInstance(context).enqueue(overlayWorkRequest)
        }
    }

}
