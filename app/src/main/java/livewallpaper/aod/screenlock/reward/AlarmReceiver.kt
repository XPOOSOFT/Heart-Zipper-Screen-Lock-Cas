package livewallpaper.aod.screenlock.reward

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.clap.whistle.phonefinder.utilities.DbHelper
import livewallpaper.aod.screenlock.zipper.MainActivity
import livewallpaper.aod.screenlock.zipper.R

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
//        val workRequest = OneTimeWorkRequestBuilder<DailyRewardWorker>().build()
//        WorkManager.getInstance(context).enqueueUniqueWork(
//            "DailyRewardWork",
//            ExistingWorkPolicy.REPLACE,
//            workRequest
//        )
        DbHelper(context).saveData(context, "IS_UNLOCK", true)
        Log.d("alaram_triger", "onReceive: Hit Call")
        val prefs = RewardPreferences(context)
        val lastOpenDate = prefs.getLastOpenDate()
        val currentTime = System.currentTimeMillis()
        val oneDayMillis = 24 * 60 * 60 * 1000L
        Log.e("work_manager", "main")
        // Check if it’s the first time or if the user missed a day
        if (prefs.isInitialLaunch()) {
            prefs.setInitialLaunchDone()
            prefs.resetDayCounter()
            prefs.setLastOpenDate(currentTime)
            unlockNextCategory(context)
            Log.e("work_manager", "1")
        } else if (currentTime - lastOpenDate >= oneDayMillis) {
            Log.e("work_manager", "2")
            unlockNextCategory(context)
        } else {
            unlockNextCategory(context)
        }

    }

    private fun unlockNextCategory(context: Context) {
        val prefs = RewardPreferences(context)
        val currentDay = prefs.getCurrentDay()
        if (currentDay <= RewardConstants.TOTAL_DAYS) {
            // Unlock the category based on the day
            // e.g., Unlock logic here for wallpaper categories based on `currentDay`
            // Show a notification that a new category is unlocked
            showUnlockNotification(context, "Category $currentDay unlocked!")
            // Move to the next day and update last open date
            prefs.setCurrentDay(currentDay + 1)
            prefs.setLastOpenDate(System.currentTimeMillis())
        }
    }

    private fun showUnlockNotification(context: Context, message: String) {
        context.let {
            val notificationManager =
                it.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channelId = "alarm_notification_channel"

            val intent = Intent(context, MainActivity::class.java).apply {
                putExtra("KEY_UPDATE_VALUE", "Reward")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            // Create notification channel for Android 8.0+
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    channelId,
                    "Alarm Notification",
                    NotificationManager.IMPORTANCE_HIGH
                )
                notificationManager.createNotificationChannel(channel)
            }
            // Build notification
            val notification = NotificationCompat.Builder(it, channelId)
                .setSmallIcon(R.drawable.ic_notification) // Add a small icon here
                .setContentTitle("Unlock Reward")
                .setContentText("Your New Categories is Ready To Unlock")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build()

            notificationManager.notify(1001, notification)
            Log.d("AlarmManagerCheck", "Alarm triggered and notification sent.")
        }
    }
}
