package livewallpaper.aod.screenlock.zipper

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.os.Build
import android.webkit.WebView
import androidx.annotation.RequiresApi
import androidx.work.Configuration
import androidx.work.WorkManager
import com.cleversolutions.ads.Audience
import com.cleversolutions.ads.MediationManager
import com.cleversolutions.ads.android.CAS
import com.google.android.gms.ads.nativead.NativeAd
import livewallpaper.aod.screenlock.zipper.ads_cam.AppOpenManager

class MyApplication : Application(), Configuration.Provider {
    companion object {
        const val TAG = "CAS Sample"
        //        const val CAS_ID = "demo"
        const val CAS_ID = "com.heartzipperlock.lovezipper.romanticlockscreen.securelock.roselock"
        const val NativeAdsId = "ca-app-pub-3940256099942544/2247696110"
        var adManager: MediationManager? = null
        var preloadNativeAd: NativeAd? = null
        @SuppressLint("StaticFieldLeak")
        var appOpenManager: AppOpenManager? = null
    }

    override fun onCreate() {
        super.onCreate()

        // Set Ads Settings
        CAS.settings.debugMode = false
        CAS.settings.taggedAudience = Audience.NOT_CHILDREN
        // Set Manual loading mode to disable auto requests
        //CAS.settings.loadingMode = LoadingManagerMode.Manual
        appOpenManager = AppOpenManager(this, CAS_ID)
        // Register activity lifecycle callbacks


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val processName = Application.getProcessName()
            if (processName != packageName) {
                WebView.setDataDirectorySuffix(processName)
            }
        }
    }

    private fun getCurrentProcessName(context: Context): String {
        val pid = android.os.Process.myPid()
        val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (process in manager.runningAppProcesses) {
            if (process.pid == pid) {
                return process.processName ?: ""
            }
        }
        return ""
    }


    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.DEBUG)
            .build()
    }

}
