package livewallpaper.aod.screenlock.zipper.new_service

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.work.Worker
import androidx.work.WorkerParameters
import livewallpaper.aod.screenlock.zipper.service.LockScreenService

class OverlayWorker(private val context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    override fun doWork(): Result {
        // Start the overlay service
        val serviceIntent = Intent(context, LockScreenService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startService(serviceIntent)
        } else {
            context.startService(serviceIntent)
        }

        return Result.success()
    }

}