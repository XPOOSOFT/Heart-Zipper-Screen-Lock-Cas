package livewallpaper.aod.screenlock.zipper.ads_cam

import android.app.Activity
import android.content.Context
import android.util.Log
import com.cleversolutions.ads.AdImpression
import com.cleversolutions.ads.AdLoadCallback
import com.cleversolutions.ads.AdPaidCallback
import com.cleversolutions.ads.AdType
import com.cleversolutions.ads.MediationManager

class RewardedAdManager(private val manager: MediationManager) {

        private val TAG = "RewardedAdManager"


    // Initialize and setup callbacks
    fun initializeRewardedAd(
    ) {
        // Set Ad load callback
        manager.onAdLoadEvent.add(object : AdLoadCallback {
            override fun onAdLoaded(type: AdType) {
                if (type == AdType.Rewarded) {
                    Log.d(TAG, "Rewarded Ad loaded and ready to show")
                }
            }

            override fun onAdFailedToLoad(type: AdType, error: String?) {
                if (type == AdType.Rewarded) {
                    Log.e(TAG, "Rewarded Ad received error: $error")
                }
            }
        })

        // Create Ad content callback
        val contentCallback = object : AdPaidCallback {
            override fun onShown(ad: AdImpression) {
                Log.d(TAG, "Rewarded Ad shown from ${ad.network}")
            }

            override fun onAdRevenuePaid(ad: AdImpression) {
                Log.d(TAG, "Rewarded Ad revenue paid from ${ad.network}")
            }

            override fun onShowFailed(message: String) {
                Log.e(TAG, "Rewarded Ad show failed: $message")
            }

            override fun onClicked() {
                Log.d(TAG, "Rewarded Ad received Click")
            }

            override fun onComplete() {
                Log.d(TAG, "Rewarded Ad complete")
//                AlertDialog.Builder(context).apply {
//                    setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
//                    setTitle("Reward Complete")
//                    setMessage("You have been rewarded!")
//                    setCancelable(false)
//                }.show()
            }

            override fun onClosed() {
                Log.d(TAG, "Rewarded Ad closed")
            }
        }

        // Setup load button if loading mode is manual
//        if (settings.loadingMode == LoadingManagerMode.Manual) {
//           manager.loadRewardedAd()
//        } else {
//            if (manager.isRewardedAdReady) {
//                Log.d(TAG, "Rewarded Ad already loaded")
//            } else {
//                Log.d(TAG, "Loading Rewarded Ad")
//            }
//        }

    }

    fun showRewardAds(context: Activity, function: (() -> Unit)) {
        // Setup show button
        if (manager.isRewardedAdReady) {
            manager.showRewardedAd(context, object : AdPaidCallback {
                override fun onShown(ad: AdImpression) {
                    Log.d(TAG, "Rewarded Ad shown from ${ad.network}")
                }

                override fun onAdRevenuePaid(ad: AdImpression) {
                    Log.d(TAG, "Rewarded Ad revenue paid from ${ad.network}")
                }

                override fun onShowFailed(message: String) {
                    Log.e(TAG, "Rewarded Ad show failed: $message")
                    function.invoke()
                }

                override fun onClicked() {
                    Log.d(TAG, "Rewarded Ad received Click")
                }

                override fun onComplete() {
                    Log.d(TAG, "Rewarded Ad complete")
                    function.invoke()
//                AlertDialog.Builder(context).apply {
//                    setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
//                    setTitle("Reward Complete")
//                    setMessage("You have been rewarded!")
//                    setCancelable(false)
//                }.show()
                }

                override fun onClosed() {
                    function.invoke()
                    Log.d(TAG, "Rewarded Ad closed")
                }
            })
        } else {
            function.invoke()
            Log.e(TAG, "Rewarded Ad not ready to show")
        }
    }
}
