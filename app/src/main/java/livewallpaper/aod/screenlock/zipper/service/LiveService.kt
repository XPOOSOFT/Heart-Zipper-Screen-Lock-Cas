package livewallpaper.aod.screenlock.zipper.service

import android.annotation.SuppressLint
import android.app.KeyguardManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_SCREEN_OFF
import android.content.Intent.ACTION_SCREEN_ON
import android.content.IntentFilter
import android.content.pm.ServiceInfo
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import livewallpaper.aod.screenlock.zipper.MainActivity
import livewallpaper.aod.screenlock.zipper.R
import livewallpaper.aod.screenlock.zipper.service.LockScreenService.Companion.Start
import livewallpaper.aod.screenlock.zipper.service.LockScreenService.Companion.Stop
import livewallpaper.aod.screenlock.zipper.utilities.NOTIFY_CHANNEL_ID

class LiveService : Service() {

    var service: LiveService? = null
    var mybroadcast: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action
            @SuppressLint("WrongConstant") val keyguardManager =
                context?.getSystemService("keyguard") as KeyguardManager
            if (action == ACTION_SCREEN_ON) {
                Start(this@LiveService)
                return
            }
            if (action == ACTION_SCREEN_OFF ) {
                Stop(this@LiveService)
                return
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        service = this
        return null
    }

    override fun onStartCommand(intent: Intent?, i: Int, i2: Int): Int {
        try {
            service = this
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return START_STICKY
    }

    private fun startForeground() {
        createNotificationChannel()
        val remoteViews = RemoteViews(packageName, R.layout.notification_collapsed)
        var z = false
        val activity = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        remoteViews.setTextViewText(R.id.text_view_collapsed_2, resources.getString(R.string.app_name))
        val i = resources.configuration.uiMode and 48
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
                    this,
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
                    this,
                    NOTIFY_CHANNEL_ID
                ).setOngoing(true)
                    .setColor(-1)
                    .setSmallIcon(R.drawable.ic_notification)
                    .setContentIntent(activity).build()
            )
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= 26) {
            val channel = NotificationChannel(
                NOTIFY_CHANNEL_ID,
                resources.getString(R.string.app_name),
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(
                channel
            )
        }
    }

    private fun RegiserScreenLock() {
        try {
            registerReceiver(mybroadcast, IntentFilter(ACTION_SCREEN_ON ))
            registerReceiver(mybroadcast, IntentFilter(ACTION_SCREEN_OFF ))
        } catch (unused: Exception) {
           unused.printStackTrace()
        }
    }
    override fun onLowMemory() {
        super.onLowMemory()
       onDestroy()
    }
    override fun onDestroy() {
        try {
            unregisterReceiver(this.mybroadcast)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        super.onDestroy()
    }

    override fun onCreate() {
        super.onCreate()
        try {
            service = this
            RegiserScreenLock()
            startForeground()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


}