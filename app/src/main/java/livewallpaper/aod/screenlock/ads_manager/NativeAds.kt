package livewallpaper.aod.screenlock.ads_manager


import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.VideoOptions
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView
import com.google.android.material.snackbar.Snackbar
import livewallpaper.aod.screenlock.zipper.BuildConfig
import livewallpaper.aod.screenlock.zipper.R
import livewallpaper.aod.screenlock.ads_manager.billing.BillingUtil
import livewallpaper.aod.screenlock.ads_manager.interfaces.NativeListener
import livewallpaper.aod.screenlock.zipper.utilities.getRandomColor
import livewallpaper.aod.screenlock.zipper.utilities.id_ads_bg
import livewallpaper.aod.screenlock.zipper.utilities.id_ads_button
import livewallpaper.aod.screenlock.zipper.utilities.id_native_screen

/**
 * Created by
 * @Author: Javed Khan ,
 */

object NativeAds {

    private const val NativeAdsLogs = "native_log_internal"
    const val NativeAdsId = "ca-app-pub-3940256099942544/2247696110"
    private var nativeAds: NativeAds? = null
    private var isNativeLoading: Boolean = false
    private var nativeId: String? = null
    var currentNativeAd: NativeAd? = null


    fun nativeAds(): NativeAds {
        if (nativeAds == null) {
            nativeAds = NativeAds
            Log.d("adsInit", "   NativeClass")
        }
        return nativeAds as NativeAds
    }

    fun loadNativeAd(
        activity: Activity,
        addConfig: Boolean,
        nativeAdId: String,
        nativeListener: NativeListener
    ) {
        Log.d(
            NativeAdsLogs,
            "validate ${!BillingUtil(activity).checkPurchased(activity)}    $addConfig"
        )

        if (AdsManager.isNetworkAvailable(activity) && !BillingUtil(
                activity
            ).checkPurchased(activity) && addConfig
        ) {
            nativeId = nativeAdId
            val builder = AdLoader.Builder(
                activity,
                if (isDebug()) NativeAdsId else nativeId
                    ?: NativeAdsId
            )
            if (isNativeLoading) {
                Log.d(NativeAdsLogs, "Already loading Ad")
                loadedShoeNative(activity, nativeListener, builder, nativeAdId)
                return
            }
            if (currentNativeAd != null) {
                nativeListener.nativeAdLoaded(currentNativeAd)
                Log.d(NativeAdsLogs, "   Having loaded Ad")
                builder.forNativeAd { nativeAd ->
                    if (currentNativeAd != null) {
                        currentNativeAd?.destroy()
                    }
                    isNativeLoading = false
                    currentNativeAd = nativeAd
                    Log.d(NativeAdsLogs, "   loaded native Ad")
                    nativeListener.nativeAdLoaded(currentNativeAd)
                }

                return
            }
            isNativeLoading = true

            builder.forNativeAd { nativeAd ->
                if (currentNativeAd != null) {
                    currentNativeAd?.destroy()
                }
                isNativeLoading = false
                currentNativeAd = nativeAd
                Log.d(NativeAdsLogs, "   loaded native Ad")
                nativeListener.nativeAdLoaded(currentNativeAd)
            }

            val videoOptions = VideoOptions.Builder().setStartMuted(true).build()

            val adOptions = NativeAdOptions.Builder().setVideoOptions(videoOptions)
                .setAdChoicesPlacement(NativeAdOptions.ADCHOICES_TOP_RIGHT).build()
            builder.withNativeAdOptions(adOptions)

            val adLoader = builder.withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    FullScreenAds.logEventForAds(NativeAdsLogs, "failed", nativeAdId)

                    if (isDebug()) {
                        Snackbar.make(
                            activity.window.decorView.rootView,
                            "AD Error Native: ${loadAdError.message}",
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                    Log.d(NativeAdsLogs, "failed native Ad  ${loadAdError.message}")
                    isNativeLoading = false
                    nativeListener.nativeAdFailed(loadAdError)

                }

                override fun onAdImpression() {
                    currentNativeAd = null
                    isNativeLoading = false
                    Log.d(NativeAdsLogs, "onAdImpression native Ad")
//                        native_precashe_copunt_current++
                        loadNativeAd(
                            activity,
                            true,
                            nativeAdId,
                            object : NativeListener {
                                override fun nativeAdLoaded(currentNativeAd: NativeAd?) {
                                    NativeAds.currentNativeAd = currentNativeAd
                                    super.nativeAdLoaded(currentNativeAd)
                                }

                            })
                    super.onAdImpression()
                }

                override fun onAdClicked() {
                    Log.d(NativeAdsLogs, "onAdClicked native Ad")
                    FullScreenAds.logEventForAds(NativeAdsLogs, "clicked", nativeAdId)
                    isNativeLoading = false
                    nativeListener.nativeAdClicked()
                    super.onAdClicked()
                }

                override fun onAdLoaded() {
                    isNativeLoading = false
                    FullScreenAds.logEventForAds(NativeAdsLogs, "loaded", nativeAdId)

                    Log.d(NativeAdsLogs, "onAdLoaded native Ad")
                    super.onAdLoaded()
                }
            }).build()

            adLoader.loadAd(AdRequest.Builder().build())
        } else {
            nativeListener.nativeAdValidate("hideAll")
            if (isDebug()) {
                Log.d(NativeAdsLogs, "config : $addConfig")
                Snackbar.make(
                    activity.window.decorView.rootView,
                    activity.getString(R.string.check_ads),
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }
    }


    fun loadNativeAdLang(
        activity: Activity,
        addConfig: Boolean,
        nativeAdId: String,
        nativeListener: NativeListener
    ) {
        Log.d(
            NativeAdsLogs,
            "validate ${!BillingUtil(activity).checkPurchased(activity)}    $addConfig"
        )

        if (AdsManager.isNetworkAvailable(activity) && !BillingUtil(
                activity
            ).checkPurchased(activity) && addConfig
        ) {
            nativeId = nativeAdId
            val builder = AdLoader.Builder(
                activity,
                if (isDebug()) NativeAdsId else nativeId
                    ?: NativeAdsId
            )
            if (isNativeLoading) {
                Log.d(NativeAdsLogs, "Already loading Ad")
                loadedShoeNativeLang(activity, nativeListener, builder, nativeAdId)
                return
            }
            if (currentNativeAd != null) {
                nativeListener.nativeAdLoaded(currentNativeAd)
                Log.d(NativeAdsLogs, "   Having loaded Ad")
                builder.forNativeAd { nativeAd ->
                    if (currentNativeAd != null) {
                        currentNativeAd?.destroy()
                    }
                    isNativeLoading = false
                    currentNativeAd = nativeAd
                    Log.d(NativeAdsLogs, "   loaded native Ad")
                    nativeListener.nativeAdLoaded(currentNativeAd)
                }

                return
            }
            isNativeLoading = true

            builder.forNativeAd { nativeAd ->
                if (currentNativeAd != null) {
                    currentNativeAd?.destroy()
                }
                isNativeLoading = false
                currentNativeAd = nativeAd
                Log.d(NativeAdsLogs, "   loaded native Ad")
                nativeListener.nativeAdLoaded(currentNativeAd)
            }

            val videoOptions = VideoOptions.Builder().setStartMuted(true).build()

            val adOptions = NativeAdOptions.Builder().setVideoOptions(videoOptions)
                .setAdChoicesPlacement(NativeAdOptions.ADCHOICES_TOP_RIGHT).build()
            builder.withNativeAdOptions(adOptions)

            val adLoader = builder.withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    FullScreenAds.logEventForAds(NativeAdsLogs, "failed", nativeAdId)

                    if (isDebug()) {
                        Snackbar.make(
                            activity.window.decorView.rootView,
                            "AD Error Native: ${loadAdError.message}",
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                    Log.d(NativeAdsLogs, "failed native Ad  ${loadAdError.message}")
                    isNativeLoading = false
                    nativeListener.nativeAdFailed(loadAdError)

                }

                override fun onAdImpression() {
                    currentNativeAd = null
                    isNativeLoading = false
                    Log.d(NativeAdsLogs, "onAdImpression native Ad")
                    loadNativeAd(
                        activity,
                        true,
                        nativeAdId,
                        object : NativeListener {
                            override fun nativeAdLoaded(currentNativeAd: NativeAd?) {
                                NativeAds.currentNativeAd = currentNativeAd
                                super.nativeAdLoaded(currentNativeAd)
                            }

                        })
                    super.onAdImpression()
                }

                override fun onAdClicked() {
                    Log.d(NativeAdsLogs, "onAdClicked native Ad")
                    FullScreenAds.logEventForAds(NativeAdsLogs, "clicked", nativeAdId)
                    isNativeLoading = false
                    nativeListener.nativeAdClicked()
                    super.onAdClicked()
                }

                override fun onAdLoaded() {
                    isNativeLoading = false
                    FullScreenAds.logEventForAds(NativeAdsLogs, "loaded", nativeAdId)

                    Log.d(NativeAdsLogs, "onAdLoaded native Ad")
                    super.onAdLoaded()
                }
            }).build()

            adLoader.loadAd(AdRequest.Builder().build())
        } else {
            nativeListener.nativeAdValidate("hideAll")
            if (isDebug()) {
                Log.d(NativeAdsLogs, "config : $addConfig")
                Snackbar.make(
                    activity.window.decorView.rootView,
                    activity.getString(R.string.check_ads),
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }
    }
    private fun loadedShoeNativeLang(
        activity: Activity,
        nativeListener: NativeListener,
        builder: AdLoader.Builder,
        nativeAdId: String
    ) {
        val videoOptions = VideoOptions.Builder().setStartMuted(true).build()

        val adOptions = NativeAdOptions.Builder().setVideoOptions(videoOptions)
            .setAdChoicesPlacement(NativeAdOptions.ADCHOICES_TOP_RIGHT).build()
        builder.withNativeAdOptions(adOptions)

        val adLoader = builder.withAdListener(object : AdListener() {
            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                FullScreenAds.logEventForAds(NativeAdsLogs, "failed", nativeAdId)

                if (isDebug()) {
                    Snackbar.make(
                        activity.window.decorView.rootView,
                        "AD Error Native: ${loadAdError.message}",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
                Log.d(NativeAdsLogs, "failed native Ad  ${loadAdError.message}")
                isNativeLoading = false
                nativeListener.nativeAdFailed(loadAdError)

            }

            override fun onAdImpression() {
                currentNativeAd = null
                isNativeLoading = false
                Log.d(NativeAdsLogs, "onAdImpression native Ad")
//                loadNativeAd(
//                    activity,
//                    true,
//                    nativeAdId,
//                    object : NativeListener {
//                        override fun nativeAdLoaded(currentNativeAd: NativeAd?) {
//                            NativeAds.currentNativeAd = currentNativeAd
//                            super.nativeAdLoaded(currentNativeAd)
//                        }
//
//                    })
                super.onAdImpression()
            }

            override fun onAdClicked() {
                Log.d(NativeAdsLogs, "onAdClicked native Ad")
                FullScreenAds.logEventForAds(NativeAdsLogs, "clicked", nativeAdId)
                isNativeLoading = false
                nativeListener.nativeAdClicked()
                super.onAdClicked()
            }

            override fun onAdLoaded() {
                isNativeLoading = false
                FullScreenAds.logEventForAds(NativeAdsLogs, "loaded", nativeAdId)
                Log.d(NativeAdsLogs, "onAdLoaded native Ad")
                super.onAdLoaded()
            }
        }).build()

        adLoader.loadAd(AdRequest.Builder().build())
        return
    }

    private fun loadedShoeNative(
        activity: Activity,
        nativeListener: NativeListener,
        builder: AdLoader.Builder,
        nativeAdId: String
    ) {
        val videoOptions = VideoOptions.Builder().setStartMuted(true).build()

        val adOptions = NativeAdOptions.Builder().setVideoOptions(videoOptions)
            .setAdChoicesPlacement(NativeAdOptions.ADCHOICES_TOP_RIGHT).build()
        builder.withNativeAdOptions(adOptions)

        val adLoader = builder.withAdListener(object : AdListener() {
            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                FullScreenAds.logEventForAds(NativeAdsLogs, "failed", nativeAdId)

                if (isDebug()) {
                    Snackbar.make(
                        activity.window.decorView.rootView,
                        "AD Error Native: ${loadAdError.message}",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
                Log.d(NativeAdsLogs, "failed native Ad  ${loadAdError.message}")
                isNativeLoading = false
                nativeListener.nativeAdFailed(loadAdError)

            }

            override fun onAdImpression() {
                currentNativeAd = null
                isNativeLoading = false
                Log.d(NativeAdsLogs, "onAdImpression native Ad")
//                    native_precashe_copunt_current++
                    loadNativeAd(
                        activity,
                        true,
                        nativeAdId,
                        object : NativeListener {
                            override fun nativeAdLoaded(currentNativeAd: NativeAd?) {
                                NativeAds.currentNativeAd = currentNativeAd
                                super.nativeAdLoaded(currentNativeAd)
                            }

                        })
                super.onAdImpression()
            }

            override fun onAdClicked() {
                Log.d(NativeAdsLogs, "onAdClicked native Ad")
                FullScreenAds.logEventForAds(NativeAdsLogs, "clicked", nativeAdId)
                isNativeLoading = false
                nativeListener.nativeAdClicked()
                super.onAdClicked()
            }

            override fun onAdLoaded() {
                isNativeLoading = false
                FullScreenAds.logEventForAds(NativeAdsLogs, "loaded", nativeAdId)
                Log.d(NativeAdsLogs, "onAdLoaded native Ad")
                super.onAdLoaded()
            }
        }).build()

        adLoader.loadAd(AdRequest.Builder().build())
        return
    }


    fun isDebug(): Boolean {
        return BuildConfig.BUILD_TYPE == "debug"
    }

    @SuppressLint("ResourceType")
    fun nativeViewMedia(context : Context, nativeAd: NativeAd, adView: NativeAdView) {
        adView.callToActionView = adView.findViewById(R.id.custom_call_to_action)
        adView.iconView = adView.findViewById<ImageView>(R.id.custom_app_icon)!!
        adView.headlineView = adView.findViewById(R.id.custom_headline)
        adView.bodyView = adView.findViewById(R.id.custom_body)
        adView.mediaView = adView.findViewById(R.id.custom_media)

        (adView.headlineView as TextView).text = nativeAd.headline
        if (nativeAd.body == null) {
            adView.bodyView?.visibility = View.INVISIBLE
        } else {
            adView.bodyView?.visibility = View.VISIBLE
            (adView.bodyView as TextView).text = nativeAd.body
        }

        if (nativeAd.callToAction == null) {
            adView.callToActionView?.visibility = View.INVISIBLE
        } else {
            adView.callToActionView?.visibility = View.VISIBLE
            (adView.callToActionView as TextView).text = nativeAd.callToAction
        }

        if (nativeAd.icon == null) {
            adView.iconView?.visibility = View.INVISIBLE
        } else {
            (adView.iconView as ImageView).setImageDrawable(
                nativeAd.icon?.drawable
            )
            adView.iconView?.visibility = View.VISIBLE
        }


        adView.setNativeAd(nativeAd)

    }

}