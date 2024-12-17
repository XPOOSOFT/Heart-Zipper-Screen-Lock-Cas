package livewallpaper.aod.screenlock.zipper.ads_cam

import android.app.Activity
import android.app.Application
import android.util.Log
import com.cleversolutions.ads.AdCallback
import com.cleversolutions.ads.AdError
import com.cleversolutions.ads.AdStatusHandler
import com.cleversolutions.ads.CASAppOpen
import com.cleversolutions.ads.LoadAdCallback
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_app_open_screen
import livewallpaper.aod.screenlock.zipper.utilities.val_app_open

class AppOpenManager(private val application: Application, private val casId: String) {

    private var appOpenAd: CASAppOpen? = null
    private var isVisibleAppOpenAd: Boolean = false
    private val tag = "AppOpenManager"

    init {
        // Initialize the App Open Ad
        appOpenAd = CASAppOpen.create(casId).apply {
            contentCallback = object : AdCallback {
                override fun onShown(ad: AdStatusHandler) {
                    Log.d(tag, "App Open Ad shown")
                }

                override fun onShowFailed(message: String) {
                    Log.e(tag, "App Open Ad show failed: $message")
                    isVisibleAppOpenAd = false
                }

                override fun onClicked() {
                    Log.d(tag, "App Open Ad clicked")
                }

                override fun onClosed() {
                    Log.d(tag, "App Open Ad closed")
                    isVisibleAppOpenAd = false
                }
            }
        }
        loadAd(val_app_open)
    }

    fun loadAd( isLoadingAppResources: Boolean) {
        appOpenAd?.loadAd(application, object : LoadAdCallback {
            override fun onAdLoaded() {
                Log.d(tag, "App Open Ad loaded")
                if (isLoadingAppResources) {
                    isVisibleAppOpenAd = true
//                    appOpenAd?.show(activity)
                }
            }

            override fun onAdFailedToLoad(error: AdError) {
                Log.e(tag, "App Open Ad failed to load: ${error.message}")
            }
        })
    }

    fun showAdIfAvailable(activity: Activity) {
        if (isVisibleAppOpenAd) {
            appOpenAd?.show(activity)
        } else {
//            loadAd(true)
            Log.d(tag, "App Open Ad is not ready to show")
        }
    }
}
