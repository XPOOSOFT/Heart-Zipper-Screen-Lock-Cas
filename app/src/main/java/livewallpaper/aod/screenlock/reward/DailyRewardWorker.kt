package livewallpaper.aod.screenlock.reward

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import livewallpaper.aod.screenlock.lib.Layers.lockerLayer.locker
import livewallpaper.aod.screenlock.zipper.MainActivity
import livewallpaper.aod.screenlock.zipper.R

class DailyRewardWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val prefs = RewardPreferences(applicationContext)
        val lastOpenDate = prefs.getLastOpenDate()
        val currentTime = System.currentTimeMillis()
        val oneDayMillis = 24 * 60 * 60 * 1000L
        Log.e("work_manager", "main")
        // Check if it’s the first time or if the user missed a day
        if (prefs.isInitialLaunch()) {
            prefs.setInitialLaunchDone()
            prefs.resetDayCounter()
            prefs.setLastOpenDate(currentTime)
            unlockNextCategory(applicationContext)
            Log.e("work_manager", "1")
        } else if (currentTime - lastOpenDate >= oneDayMillis) {
            Log.e("work_manager", "2")
            // If enough time has passed since the last unlock, unlock the next category
            unlockNextCategory(applicationContext)
        }

        return Result.success()
    }

    fun unlockNextCategory(context: Context) {
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
//        val intent = Intent(context, MainActivity::class.java) // Replace with the actual activity to open
//        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val intent = Intent(context, MainActivity::class.java).apply {
            putExtra("KEY_UPDATE_VALUE", "Reward")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(context, "UnlockChannel")
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("New Category Unlocked!")
            .setContentText(message)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(1, notification)
    }


}
