package livewallpaper.aod.screenlock.zipper.new_service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf

class ScreenReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_SCREEN_ON -> {
                Log.d("ScreenReceiver", "Screen ON")
                startWorker(context, "screen_on")
            }
            Intent.ACTION_SCREEN_OFF -> {
                Log.d("ScreenReceiver", "Screen OFF")
                startWorker(context, "screen_off")
            }
        }
    }

    private fun startWorker(context: Context, screenState: String) {
        val workRequest = OneTimeWorkRequestBuilder<ScreenWorker>()
            .setInputData(workDataOf("screen_state" to screenState))
            .build()
        WorkManager.getInstance(context).enqueue(workRequest)
    }
}
