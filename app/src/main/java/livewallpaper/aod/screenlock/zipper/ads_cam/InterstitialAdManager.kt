package livewallpaper.aod.screenlock.zipper.ads_cam

import android.app.Activity
import android.content.Context
import android.util.Log
import com.cleversolutions.ads.AdImpression
import com.cleversolutions.ads.AdLoadCallback
import com.cleversolutions.ads.AdPaidCallback
import com.cleversolutions.ads.AdType
import com.cleversolutions.ads.LoadingManagerMode
import com.cleversolutions.ads.MediationManager
import com.cleversolutions.ads.android.CAS
import livewallpaper.aod.screenlock.zipper.utilities.counter
import livewallpaper.aod.screenlock.zipper.utilities.id_frequency_counter
import livewallpaper.aod.screenlock.zipper.utilities.id_inter_counter
import livewallpaper.aod.screenlock.zipper.utilities.inter_frequency_count
import livewallpaper.aod.screenlock.zipper.utilities.isNetworkAvailable
import livewallpaper.aod.screenlock.zipper.utilities.isSplash

class InterstitialAdManager(
    private val context: Context,
    private val manager: MediationManager
) {

        private  val TAG = "InterstitialAdManager"

    init {
        setupAdLoadCallback()
    }

    // Function to load the interstitial ad
    fun loadAd(isAdsShow: Boolean) {
//        if (id_inter_counter != counter) {
//            return
//        }
        if (CAS.settings.loadingMode == LoadingManagerMode.Manual && isAdsShow) {
            Log.d(TAG, "Loading Interstitial Ad")
            manager.loadInterstitial()
        } else {
            Log.d(TAG, if (manager.isInterstitialReady) "Ad already loaded" else "Loading Ad in non-manual mode")
        }
    }

    // Function to show the interstitial ad
    fun showAd(isShowAds: Boolean, function: () -> Unit) {
//        if (id_inter_counter != counter) {
//            Log.d(TAG, "Showing Interstitial if-counter")
//            counter++
//            function.invoke()
//            return
//        } else {
//            Log.d(TAG, "Showing Interstitial else- $counter")
//            counter = 0
//        }
        if (manager.isInterstitialReady && isShowAds) {
            Log.d(TAG, "Showing Interstitial Ad")
            manager.showInterstitial(context as Activity, setupAdContentCallback{
                function.invoke()
            })
        } else {
            function.invoke()
            Log.e(TAG, "Interstitial Ad not ready to show")
        }

    }

    // Function to show the interstitial ad
    fun showAdSplash(isShowAds: Boolean, function: () -> Unit) {
        if (manager.isInterstitialReady && isShowAds) {
            Log.d(TAG, "Showing Interstitial Ad")
            manager.showInterstitial(context as Activity, setupAdContentCallback{
            })
        } else {
            Log.e(TAG, "Interstitial Ad not ready to show")
        }
        function.invoke()
    }

    // Private function to set up Ad load callback
    private fun setupAdLoadCallback() {
        if (!isNetworkAvailable(context)) {
            return
        }
        if (inter_frequency_count >= id_frequency_counter) {
            return
        }
        manager.onAdLoadEvent.add(object : AdLoadCallback {
            override fun onAdLoaded(type: AdType) {
                if (type == AdType.Interstitial) {
                    Log.d(TAG, "Interstitial Ad loaded and ready to show")
                }
            }

            override fun onAdFailedToLoad(type: AdType, error: String?) {
                if (type == AdType.Interstitial) {
                    Log.e(TAG, "Failed to load Interstitial Ad: $error")
                }
            }
        })
    }

    // Private function to set up Ad content callback
    private fun setupAdContentCallback(function: (()->Unit)): AdPaidCallback {

        return object : AdPaidCallback {
            override fun onShown(ad: AdImpression) {
                isSplash=false
//                id_inter_counter++
//                function.invoke()
                Log.d(TAG, "Ad shown from ${ad.network}")
            }

            override fun onAdRevenuePaid(ad: AdImpression) {
                function.invoke()
                Log.d(TAG, "Ad revenue paid from ${ad.network}")
            }

            override fun onShowFailed(message: String) {
                isSplash=true
                function.invoke()
                Log.e(TAG, "Failed to show Ad: $message")
            }

            override fun onClicked() {
                Log.d(TAG, "Ad clicked")
            }

            override fun onClosed() {
                isSplash=true
                Log.d(TAG, "Ad closed")
            }
        }
    }
}
