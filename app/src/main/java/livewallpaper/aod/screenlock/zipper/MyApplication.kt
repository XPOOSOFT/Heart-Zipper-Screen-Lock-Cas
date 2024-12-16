package livewallpaper.aod.screenlock.zipper

import android.app.Application
import android.util.Log
import com.cleversolutions.ads.AdType
import com.cleversolutions.ads.Audience
import com.cleversolutions.ads.ConsentFlow
import com.cleversolutions.ads.MediationManager
import com.cleversolutions.ads.android.CAS
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration

class MyApplication : Application() {
    companion object {
        const val TAG = "CAS Sample"
        const val CAS_ID = "demo"

        lateinit var adManager: MediationManager
    }

    override fun onCreate() {
        super.onCreate()

        // Set Ads Settings
        CAS.settings.debugMode = BuildConfig.DEBUG
        CAS.settings.taggedAudience = Audience.NOT_CHILDREN

        // Set Manual loading mode to disable auto requests
        //CAS.settings.loadingMode = LoadingManagerMode.Manual

        // Initialize SDK
        adManager = CAS.buildManager()
            .withManagerId(CAS_ID)
            .withTestAdMode(BuildConfig.DEBUG)
            .withAdTypes(AdType.Banner, AdType.Interstitial, AdType.Rewarded, AdType.AppOpen, AdType.Native)
            .withConsentFlow(
                ConsentFlow(isEnabled = true)
                    .withDismissListener {
                        Log.d(TAG, "Consent flow dismissed")
                    }
            )
            .withCompletionListener {
                if (it.error == null) {
                    Log.d(TAG, "Ad manager initialized")
                } else {
                    Log.d(TAG, "Ad manager initialization failed: " + it.error)
                }
            }
            .build(this)
    }

}
