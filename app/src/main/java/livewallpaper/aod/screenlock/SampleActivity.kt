package livewallpaper.aod.screenlock

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import com.cleversolutions.ads.*
import com.cleversolutions.ads.android.CAS
import com.cleversolutions.ads.android.CASBannerView
import livewallpaper.aod.screenlock.zipper.MyApplication.Companion.TAG
import livewallpaper.aod.screenlock.zipper.MyApplication.Companion.adManager
import livewallpaper.aod.screenlock.zipper.R
import livewallpaper.aod.screenlock.zipper.utilities.clickWithThrottle


@SuppressLint("SetTextI18n")
class SampleActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_test)

        // Get current SDK version
        findViewById<TextView>(R.id.casVersionText).text = CAS.getSDKVersion()
        findViewById<AppCompatButton>(R.id.showAppOpen).clickWithThrottle {
            startActivity(Intent(this, SampleAppOpenAdActivity::class.java))
        }

        createBanner(adManager, findViewById<LinearLayout>(R.id.container))
        createInterstitial(adManager)
        createRewarded(adManager)

    }


    private fun createBanner(manager: MediationManager, container: LinearLayout) {
        val bannerView = CASBannerView(this, manager)
        // Set required Ad size
        bannerView.size = AdSize.getAdaptiveBannerInScreen(this)
        //bannerView.size = AdSize.BANNER
        //bannerView.size = AdSize.LEADERBOARD
        //bannerView.size = AdSize.MEDIUM_RECTANGLE
        // Set Ad content listener
        bannerView.adListener = object : AdViewListener {
            override fun onAdViewLoaded(view: CASBannerView) {
                Log.d(TAG, "Banner Ad loaded and ready to present")
            }

            override fun onAdViewFailed(view: CASBannerView, error: AdError) {
                Log.e(TAG, "Banner Ad received error: " + error.message)
            }

            override fun onAdViewPresented(view: CASBannerView, info: AdImpression) {
                Log.d(TAG, "Banner Ad presented from " + info.network)
            }

            override fun onAdViewClicked(view: CASBannerView) {
                Log.d(TAG, "Banner Ad received Click action")
            }
        }
        // Add view to container
        container.addView(bannerView)
        // Set controls
        findViewById<Button>(R.id.loadBannerBtn).setOnClickListener {
            bannerView.loadNextAd()
        }

        findViewById<Button>(R.id.showBannerBtn).setOnClickListener {
            bannerView.visibility = View.VISIBLE
        }

        findViewById<Button>(R.id.hideBannerBtn).setOnClickListener {
            bannerView.visibility = View.GONE
        }
    }

    private fun createInterstitial(manager: MediationManager) {
        setupAdLoadCallback(manager)
        setupAdContentCallback(manager)
        setupButtons(manager)
    }

    private fun createRewarded(manager: MediationManager) {

        // Set Ad load callback
        manager.onAdLoadEvent.add(object : AdLoadCallback {
            override fun onAdLoaded(type: AdType) {
                if (type == AdType.Rewarded) {
                    Log.d(TAG, "Rewarded Ad loaded and ready to show")
                    runOnUiThread {
                        Log.d(TAG, "Loaded")
                    }
                }
            }

            override fun onAdFailedToLoad(type: AdType, error: String?) {
                if (type == AdType.Rewarded) {
                    Log.d(TAG, "Rewarded Ad received error: $error")
                    runOnUiThread {
                        Log.d(TAG, "error")
                    }
                }
            }
        })

        // Create Ad content callback
        val contentCallback = object : AdPaidCallback {
            override fun onShown(ad: AdImpression) {
                Log.d(TAG, "Rewarded Ad shown from " + ad.network)
            }

            override fun onAdRevenuePaid(ad: AdImpression) {
                Log.d(TAG, "Rewarded Ad revenue paid from " + ad.network)
            }

            override fun onShowFailed(message: String) {
                Log.e(TAG, "Rewarded Ad show failed: $message")
                Log.d(TAG, "message")
            }

            override fun onClicked() {
                Log.d(TAG, "Rewarded Ad received Click")
            }

            override fun onComplete() {
                val dialog = AlertDialog.Builder(this@SampleActivity).apply {
                    setPositiveButton("ok") { dialog, _ -> dialog.dismiss() }
                    setTitle("Rewarded Ad complete")
                    setMessage("You have been rewarded")
                    setCancelable(false)
                    create()
                }
                dialog.show()
            }

            override fun onClosed() {
                Log.d(TAG, "Rewarded Ad received Close")
                Log.d(TAG, "Closed")
            }
        }

        // Any loading mode, except manual,
        // automatically controls the preparation of sdk for impressions.
        if (CAS.settings.loadingMode == LoadingManagerMode.Manual) {
            findViewById<Button>(R.id.loadRewardedBtn).setOnClickListener {
                Log.d(TAG, "Loading")
                manager.loadRewardedAd()
            }
        } else {
            if (manager.isRewardedAdReady)
                Log.d(TAG, "Loaded")
            else
                Log.d(TAG, "Loading")
            findViewById<Button>(R.id.loadRewardedBtn).visibility = View.GONE
        }

        findViewById<Button>(R.id.showRewardedBtn).setOnClickListener {
            if (manager.isRewardedAdReady)
                manager.showRewardedAd(this, contentCallback)
            else
                Log.e(TAG, "Rewarded Ad not ready to show")
        }
    }

    // Function to handle the Ad loading process
    private fun loadInterstitial(manager: MediationManager) {
        if (CAS.settings.loadingMode == LoadingManagerMode.Manual) {
            Log.d(TAG, "Loading Interstitial Ad")
            manager.loadInterstitial()
        } else {
            if (manager.isInterstitialReady) {
                Log.d(TAG, "Interstitial Ad already loaded")
            } else {
                Log.d(TAG, "Loading Interstitial Ad in non-manual mode")
            }
        }
    }

    // Function to show the interstitial ad
    private fun showInterstitial(manager: MediationManager, contentCallback: AdPaidCallback) {
        if (manager.isInterstitialReady) {
            Log.d(TAG, "Showing Interstitial Ad")
            manager.showInterstitial(this, contentCallback)
        } else {
            Log.e(TAG, "Interstitial Ad not ready to show")
        }
    }

    // Setup for Ad load callback
    private fun setupAdLoadCallback(manager: MediationManager) {
        manager.onAdLoadEvent.add(object : AdLoadCallback {
            override fun onAdLoaded(type: AdType) {
                if (type == AdType.Interstitial) {
                    Log.d(TAG, "Interstitial Ad loaded and ready to show")
                    runOnUiThread { Log.d(TAG, "Loaded") }
                }
            }

            override fun onAdFailedToLoad(type: AdType, error: String?) {
                if (type == AdType.Interstitial) {
                    Log.d(TAG, "Interstitial Ad received error: $error")
                    runOnUiThread { Log.d(TAG, "Error: $error") }
                }
            }
        })
    }

    // Setup for Ad content callback
    private fun setupAdContentCallback(manager: MediationManager): AdPaidCallback {
        return object : AdPaidCallback {
            override fun onShown(ad: AdImpression) {
                Log.d(TAG, "Interstitial Ad shown from ${ad.network}")
            }

            override fun onAdRevenuePaid(ad: AdImpression) {
                Log.d(TAG, "Interstitial Ad revenue paid from ${ad.network}")
            }

            override fun onShowFailed(message: String) {
                Log.e(TAG, "Interstitial Ad show failed: $message")
                Log.d(TAG, "Message: $message")
            }

            override fun onClicked() {
                Log.d(TAG, "Interstitial Ad received Click")
            }

            override fun onClosed() {
                Log.d(TAG, "Interstitial Ad closed")
            }
        }
    }

    // Setup for button listeners
    private fun setupButtons(manager: MediationManager) {
        findViewById<Button>(R.id.loadInterBtn).apply {
            visibility = if (CAS.settings.loadingMode == LoadingManagerMode.Manual) View.VISIBLE else View.GONE
            setOnClickListener { loadInterstitial(manager) }
        }

        findViewById<Button>(R.id.showInterBtn).setOnClickListener {
            val contentCallback = setupAdContentCallback(manager)
            showInterstitial(manager, contentCallback)
        }
    }

}
