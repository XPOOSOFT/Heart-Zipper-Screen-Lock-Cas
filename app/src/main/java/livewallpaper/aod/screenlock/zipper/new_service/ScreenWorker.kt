package livewallpaper.aod.screenlock.zipper.new_service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.startForegroundService
import androidx.work.Worker
import androidx.work.WorkerParameters
import livewallpaper.aod.screenlock.zipper.R
import livewallpaper.aod.screenlock.zipper.service.LiveService
import livewallpaper.aod.screenlock.zipper.service.LockScreenService.Companion.Start
import livewallpaper.aod.screenlock.zipper.service.LockScreenService.Companion.Stop

class ScreenWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {

    var service: LiveService? = null
    override fun doWork(): Result {
        val screenState = inputData.getString("screen_state")
        Log.d("ScreenWorker", "Triggered: $screenState")

        when (screenState) {
            "screen_on" -> {
                // Replace with your own logic
                StartTask()
            }
            "screen_off" -> {
                // Replace with your own logic
                StopTask()
            }
        }

        showNotification(screenState ?: "Unknown")

        return Result.success()
    }

    private fun StartTask() {
        Log.d("ScreenWorker", "Start Task Triggered")
        Start(applicationContext)
    }

    private fun StopTask() {
        Log.d("ScreenWorker", "Stop Task Triggered")
        Stop(applicationContext)
    }

    private fun showNotification(state: String) {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "screen_channel",
                "Screen Notification",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(applicationContext, "screen_channel")
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Screen Event")
            .setContentText("Detected: $state")
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        notificationManager.notify((0..1000).random(), builder.build())
    }
}
