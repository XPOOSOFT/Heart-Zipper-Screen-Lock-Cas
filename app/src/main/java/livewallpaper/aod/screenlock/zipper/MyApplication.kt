package livewallpaper.aod.screenlock.zipper

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import com.cleversolutions.ads.AdType
import com.cleversolutions.ads.Audience
import com.cleversolutions.ads.ConsentFlow
import com.cleversolutions.ads.MediationManager
import com.cleversolutions.ads.android.CAS
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.ads.nativead.NativeAd
import livewallpaper.aod.screenlock.zipper.ads_cam.AppOpenManager

class MyApplication : Application() {
    companion object {
        const val TAG = "CAS Sample"
        const val CAS_ID = "demo"
        const val NativeAdsId = "ca-app-pub-3940256099942544/2247696110"
        lateinit var adManager: MediationManager
        var preloadNativeAd: NativeAd? = null
        lateinit var appOpenManager: AppOpenManager
    }
    override fun onCreate() {
        super.onCreate()

        // Set Ads Settings
        CAS.settings.debugMode = BuildConfig.DEBUG
        CAS.settings.taggedAudience = Audience.NOT_CHILDREN

        // Set Manual loading mode to disable auto requests
        //CAS.settings.loadingMode = LoadingManagerMode.Manual

        appOpenManager =AppOpenManager(this, CAS_ID)
        // Register activity lifecycle callbacks


//        // Initialize SDK
//        adManager = CAS.buildManager()
//            .withManagerId(CAS_ID)
//            .withTestAdMode(BuildConfig.DEBUG)
//            .withAdTypes(AdType.Banner, AdType.Interstitial, AdType.Rewarded, AdType.AppOpen, AdType.Native, AdType.Rewarded)
//            .withConsentFlow(
//                ConsentFlow(isEnabled = true)
//                    .withDismissListener {
//                        Log.d(TAG, "Consent flow dismissed")
//                    }
//            )
//            .withCompletionListener {
//                if (it.error == null) {
//                    Log.d(TAG, "Ad manager initialized")
//                    // Initialize App Open Manager
//                    appOpenManager = AppOpenManager(this, CAS_ID)
//
//                    // Register activity lifecycle callbacks
//                    registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
//                        override fun onActivityResumed(activity: Activity) {
//                            appOpenManager.showAdIfAvailable(activity)
//                        }
//
//                        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
//                        override fun onActivityStarted(activity: Activity) {}
//                        override fun onActivityPaused(activity: Activity) {}
//                        override fun onActivityStopped(activity: Activity) {}
//                        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
//                        override fun onActivityDestroyed(activity: Activity) {}
//                    })
//                } else {
//                    Log.d(TAG, "Ad manager initialization failed: " + it.error)
//                }
//            }
//            .build(this)
    }

    fun isDebug(): Boolean {
        return BuildConfig.BUILD_TYPE == "debug"
    }
}
