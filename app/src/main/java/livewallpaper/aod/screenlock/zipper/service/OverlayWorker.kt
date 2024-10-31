package livewallpaper.aod.screenlock.zipper.service

import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import livewallpaper.aod.screenlock.zipper.R

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
