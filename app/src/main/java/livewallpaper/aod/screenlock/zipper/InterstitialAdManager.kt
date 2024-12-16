package livewallpaper.aod.screenlock.zipper

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

class InterstitialAdManager(
    private val context: Context,
    private val manager: MediationManager
) {

    companion object {
        private const val TAG = "InterstitialAdManager"
    }

    init {
        setupAdLoadCallback()
    }

    // Function to load the interstitial ad
    fun loadAd() {
        if (CAS.settings.loadingMode == LoadingManagerMode.Manual) {
            Log.d(TAG, "Loading Interstitial Ad")
            manager.loadInterstitial()
        } else {
            Log.d(TAG, if (manager.isInterstitialReady) "Ad already loaded" else "Loading Ad in non-manual mode")
        }
    }

    // Function to show the interstitial ad
    fun showAd(function: (()->Unit)) {
        if (manager.isInterstitialReady) {
            Log.d(TAG, "Showing Interstitial Ad")
            manager.showInterstitial(context as Activity, setupAdContentCallback{
                function.invoke()
            })
        } else {
            Log.e(TAG, "Interstitial Ad not ready to show")
        }
    }

    // Private function to set up Ad load callback
    private fun setupAdLoadCallback() {
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
                function.invoke()
                Log.d(TAG, "Ad shown from ${ad.network}")
            }

            override fun onAdRevenuePaid(ad: AdImpression) {
                function.invoke()
                Log.d(TAG, "Ad revenue paid from ${ad.network}")
            }

            override fun onShowFailed(message: String) {
                function.invoke()
                Log.e(TAG, "Failed to show Ad: $message")
            }

            override fun onClicked() {
                Log.d(TAG, "Ad clicked")
            }

            override fun onClosed() {
                Log.d(TAG, "Ad closed")
            }
        }
    }
}
