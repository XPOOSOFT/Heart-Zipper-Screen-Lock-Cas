package livewallpaper.aod.screenlock.zipper

import android.app.Application
import android.util.Log
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()


        MobileAds.initialize(this)
        val testDeviceIds =
            listOf("5cc64ff7-09c8-413c-8af9-8c8601affe0c", "3161FEFA3635A833C251D5BF71F528F3")
        val configuration = RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build()
        MobileAds.setRequestConfiguration(configuration)
        Log.d("application_class", "onCreate")

    }
}