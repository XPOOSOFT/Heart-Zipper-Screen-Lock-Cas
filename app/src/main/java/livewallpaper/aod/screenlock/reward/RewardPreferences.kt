package livewallpaper.aod.screenlock.reward

import android.content.Context
import livewallpaper.aod.screenlock.reward.RewardConstants.UNLOCK_DAY

class RewardPreferences(context: Context) {
    private val prefs = context.getSharedPreferences(RewardConstants.PREFS_NAME, Context.MODE_PRIVATE)

    fun isInitialLaunch(): Boolean = prefs.getBoolean(RewardConstants.INITIAL_LAUNCH_KEY, true)

    fun getCurrentUnlockDay(context: Context): Int {
        val prefs = context.getSharedPreferences(RewardConstants.PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getInt(UNLOCK_DAY, 0)
    }

    fun incrementUnlockDay(context: Context) {
        val prefs = context.getSharedPreferences(RewardConstants.PREFS_NAME, Context.MODE_PRIVATE)
        val newDay = getCurrentUnlockDay(context) + 1
        prefs.edit().putInt(UNLOCK_DAY, newDay).apply()
    }

    fun setInitialLaunchDone() {
        prefs.edit().putBoolean(RewardConstants.INITIAL_LAUNCH_KEY, false).apply()
    }

    fun getCurrentDay(): Int = prefs.getInt(RewardConstants.CURRENT_DAY_KEY, 1)

    fun setCurrentDay(day: Int) {
        prefs.edit().putInt(RewardConstants.CURRENT_DAY_KEY, day).apply()
    }

    fun getLastOpenDate(): Long = prefs.getLong(RewardConstants.LAST_OPEN_DATE_KEY, 0)

    fun setLastOpenDate(date: Long) {
        prefs.edit().putLong(RewardConstants.LAST_OPEN_DATE_KEY, date).apply()
    }

    fun resetDayCounter() {
        setCurrentDay(1)
    }
}
