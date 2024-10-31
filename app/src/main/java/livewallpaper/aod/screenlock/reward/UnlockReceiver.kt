package livewallpaper.aod.screenlock.reward

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import livewallpaper.aod.screenlock.zipper.MainActivity
import livewallpaper.aod.screenlock.zipper.R

class UnlockReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val prefs = RewardPreferences(context)

        // Check if the app was opened the previous day
        val lastOpenDate = prefs.getLastOpenDate()
        val currentTime = System.currentTimeMillis()
        val oneDayMillis = 24 * 60 * 60 * 1000L

        // If app wasn't opened the previous day, reset to day 1
        if (currentTime - lastOpenDate >= oneDayMillis * 2) {
            prefs.resetDayCounter()
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
        val intent = Intent(context, MainActivity::class.java) // Replace with the actual activity to open
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

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
