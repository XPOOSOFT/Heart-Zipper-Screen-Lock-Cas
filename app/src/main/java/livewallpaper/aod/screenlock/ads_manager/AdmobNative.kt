package livewallpaper.aod.screenlock.ads_manager

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import livewallpaper.aod.screenlock.zipper.R
import livewallpaper.aod.screenlock.ads_manager.AdOpenApp.Companion.preloadNativeAd
import livewallpaper.aod.screenlock.ads_manager.AdsBanners.isDebug
import livewallpaper.aod.screenlock.ads_manager.NativeAds.NativeAdsId
import livewallpaper.aod.screenlock.ads_manager.ScreenUtils.isSupportFullScreen
import livewallpaper.aod.screenlock.ads_manager.interfaces.NativeCallBack
import livewallpaper.aod.screenlock.ads_manager.interfaces.NativeType
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.formats.NativeAdOptions
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import livewallpaper.aod.screenlock.zipper.utilities.convertDpToPixel

/**
 * @Author:Javed Khan
 * @Date: 14,March,2024.
 * @Accounts
 */
class AdmobNative {

    /**
     * 0 = Ads Off
     * 1 = Ads On
     */

    private var adMobNativeAd: NativeAd? = null

    /**
     * load native ad and show
     */
    fun loadNativeAds(
        activity: Activity?,
        adsPlaceHolder: FrameLayout,
        nativeId: String,
        adEnable: Int,
        isAppPurchased: Boolean,
        isInternetConnected: Boolean,
        nativeType: Int,
        nativeCallBack: NativeCallBack? = null
    ) {
        val handlerException = CoroutineExceptionHandler { coroutineContext, throwable ->
            Log.e("AdsInformation", "${throwable.message}")
            nativeCallBack?.onAdFailedToLoad("${throwable.message}")
        }

        if (isAppPurchased) {
            Log.e("AdsInformation", "onAdFailedToLoad -> Premium user")
            nativeCallBack?.onAdFailedToLoad("onAdFailedToLoad -> Premium user")
            return
        }

        if (adEnable == 0) {
            Log.e("AdsInformation", "onAdFailedToLoad -> Remote config is off")
            nativeCallBack?.onAdFailedToLoad("onAdFailedToLoad -> Remote config is off")
            return
        }

        if (isInternetConnected.not()) {
            Log.e("AdsInformation", "onAdFailedToLoad -> Internet is not connected")
            nativeCallBack?.onAdFailedToLoad("onAdFailedToLoad -> Internet is not connected")
            return
        }

        if (activity == null) {
            Log.e("AdsInformation", "onAdFailedToLoad -> Context is null")
            nativeCallBack?.onAdFailedToLoad("onAdFailedToLoad -> Context is null")
            return
        }

        if (activity.isFinishing || activity.isDestroyed) {
            Log.e("AdsInformation", "onAdFailedToLoad -> activity is finishing or destroyed")
            nativeCallBack?.onAdFailedToLoad("onAdFailedToLoad -> activity is finishing or destroyed")
            return
        }

        if (nativeId.trim().isEmpty()) {
            Log.e("AdsInformation", "onAdFailedToLoad -> Ad id is empty")
            nativeCallBack?.onAdFailedToLoad("onAdFailedToLoad -> Ad id is empty")
            return
        }

        try {
            adsPlaceHolder.visibility = View.VISIBLE
            // reuse of preloaded native ad
            // if miss first native then use it next
            if (preloadNativeAd != null) {
                adMobNativeAd = preloadNativeAd
                preloadNativeAd = null
                Log.i("AdsInformation", "admob native onPreloaded")
                nativeCallBack?.onPreloaded()
                displayNativeAd(activity, adsPlaceHolder, nativeType)
                return
            }
            if (adMobNativeAd == null) {
                CoroutineScope(Dispatchers.IO + handlerException).launch {
                    val builder: AdLoader.Builder =
                        AdLoader.Builder(activity, if (isDebug()) NativeAdsId else nativeId)
                    val adLoader =
                        builder.forNativeAd { unifiedNativeAd: NativeAd? ->
                            adMobNativeAd = unifiedNativeAd
                        }
                            .withAdListener(object : AdListener() {
                                override fun onAdImpression() {
                                    super.onAdImpression()
                                    Log.d("AdsInformation", "admob native onAdImpression")
                                    nativeCallBack?.onAdImpression()
                                    adMobNativeAd = null
                                }

                                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                                    Log.e(
                                        "AdsInformation",
                                        "admob native onAdFailedToLoad: ${loadAdError.message}"
                                    )
                                    nativeCallBack?.onAdFailedToLoad(loadAdError.message)
                                    adsPlaceHolder.visibility = View.GONE
                                    adMobNativeAd = null
                                    super.onAdFailedToLoad(loadAdError)
                                }

                                override fun onAdLoaded() {
                                    super.onAdLoaded()
                                    Log.i("AdsInformation", "admob native onAdLoaded")
                                    nativeCallBack?.onAdLoaded()
                                    displayNativeAd(activity, adsPlaceHolder, nativeType)

                                }

                            }).withNativeAdOptions(
                                com.google.android.gms.ads.nativead.NativeAdOptions.Builder()
                                    .setAdChoicesPlacement(
                                        NativeAdOptions.ADCHOICES_TOP_RIGHT
                                    ).build()
                            )
                            .build()
                    adLoader.loadAd(AdRequest.Builder().build())

                }
            } else {
                Log.e("AdsInformation", "Native is already onPreloaded")
                nativeCallBack?.onPreloaded()
                displayNativeAd(activity, adsPlaceHolder, nativeType)
            }
        } catch (ex: Exception) {
            Log.e("AdsInformation", "${ex.message}")
            nativeCallBack?.onAdFailedToLoad("${ex.message}")
        }
    }
    var adView: NativeAdView? = null
    private fun displayNativeAd(
        activity: Activity?,
        adMobNativeContainer: FrameLayout,
        nativeType: Int,
    ) {
        activity?.let { mActivity ->
            try {
                adMobNativeAd?.let { ad ->
                    val inflater = LayoutInflater.from(mActivity)

                    when (nativeType) {
                        1 -> {
                            adMobNativeContainer.minimumHeight = convertDpToPixel(40F, activity).toInt()
                            adView =inflater.inflate(
                                R.layout.layout_native_80,
                                null
                            ) as NativeAdView
                        }

                        2 -> {
                            adMobNativeContainer.minimumHeight = convertDpToPixel(80F, activity).toInt()
                            adView =inflater.inflate(
                                R.layout.layout_native_140,
                                null
                            ) as NativeAdView
                        }

                        3 -> {
                            adMobNativeContainer.minimumHeight = convertDpToPixel(120F, activity).toInt()
                            adView =inflater.inflate(
                                R.layout.layout_native_176,
                                null
                            ) as NativeAdView
                        }

                        4 -> {
                            adMobNativeContainer.minimumHeight = convertDpToPixel(190F, activity).toInt()
                            adView =inflater.inflate(
                                R.layout.native_layout_190,
                                null
                            ) as NativeAdView
                        }

                        5 -> {
                            adMobNativeContainer.minimumHeight = convertDpToPixel(220F, activity).toInt()
                            adView =inflater.inflate(
                                R.layout.native_layout_276,
                                null
                            ) as NativeAdView
                        }

                        6 -> {
                            adMobNativeContainer.minimumHeight = convertDpToPixel(220F, activity).toInt()
                            adView =inflater.inflate(
                                R.layout.layout_native_260,
                                null
                            ) as NativeAdView
                        }

                        7 -> {
                            adMobNativeContainer.minimumHeight = convertDpToPixel(220F, activity).toInt()
                            adView =inflater.inflate(
                                R.layout.ad_undified_media_heigh,
                                null
                            ) as NativeAdView
                        }

                        8 -> {
                            adView =inflater.inflate(
                                R.layout.ad_unified_media_full,
                                null
                            ) as NativeAdView
                        }
                    }
                    val viewGroup: ViewGroup? = adView?.parent as ViewGroup?
                    viewGroup?.removeView(adView)
                    adMobNativeContainer.removeAllViews()
                    adMobNativeContainer.addView(adView)

                    adView?.callToActionView = adView?.findViewById(R.id.custom_call_to_action)
                    adView?.iconView = adView?.findViewById<ImageView>(R.id.custom_app_icon)!!
                    adView?.headlineView = adView?.findViewById(R.id.custom_headline)
                    adView?.bodyView = adView?.findViewById(R.id.custom_body)
                    adView?.mediaView = adView?.findViewById(R.id.custom_media)

                    (adView?.headlineView as TextView).text = ad.headline
                    if (ad.body == null) {
                        adView?.bodyView?.visibility = View.INVISIBLE
                    } else {
                        adView?.bodyView?.visibility = View.VISIBLE
                        (adView?.bodyView as TextView).text = ad.body
                    }

                    if (ad.callToAction == null) {
                        adView?.callToActionView?.visibility = View.INVISIBLE
                    } else {
                        adView?.callToActionView?.visibility = View.VISIBLE
                        (adView?.callToActionView as TextView).text = ad.callToAction
                    }

                    if (ad.icon == null) {
                        adView?.iconView?.visibility = View.INVISIBLE
                    } else {
                        (adView?.iconView as ImageView).setImageDrawable(
                            ad.icon?.drawable
                        )
                        adView?.iconView?.visibility = View.VISIBLE
                    }


                    adView?.setNativeAd(ad)
/*                    // Set other ad assets.
                    adView?.headlineView = adView?.findViewById(R.id.custom_headline)
                    adView?.bodyView = adView?.findViewById(R.id.custom_body)
                    adView?.callToActionView = adView?.findViewById(R.id.custom_call_to_action)
                    adView?.iconView = adView?.findViewById(R.id.custom_app_icon)

                    //Headline
                    adView?.headlineView?.let { headline ->
                        (headline as TextView).text = ad.headline
                        headline.isSelected = true
                    }

                    //Body
                    adView?.bodyView?.let { bodyView ->
                        if (ad.body == null) {
                            bodyView.visibility = View.INVISIBLE
                        } else {
                            bodyView.visibility = View.VISIBLE
                            (bodyView as TextView).text = ad.body
                        }
                    }

                    //Call to Action
                    adView?.callToActionView?.let { ctaView ->
                        if (ad.callToAction == null) {
                            ctaView.visibility = View.INVISIBLE
                        } else {
                            ctaView.visibility = View.VISIBLE
                            (ctaView as Button).text = ad.callToAction
                        }
                    }

                    //Icon
                    adView?.iconView?.let { iconView ->
                        if (ad.icon == null) {
                            iconView.visibility = View.INVISIBLE
                        } else {
                            (iconView as ImageView).setImageDrawable(ad.icon?.drawable)
                            iconView.visibility = View.VISIBLE
                        }
                    }

                    adView?.advertiserView?.let { adverView ->
                        if (ad.advertiser == null) {
                            adverView.visibility = View.GONE
                        } else {
                            (adverView as TextView).text = ad.advertiser
                            adverView.visibility = View.GONE
                        }
                    }
//                    if (mActivity.isSupportFullScreen()) {
                        val mediaView: MediaView = adView?.findViewById(R.id.custom_media)?:return
                        adView?.mediaView = mediaView
//                    }
                    adView?.setNativeAd(ad)*/
                }
            } catch (ex: Exception) {
                Log.e("AdsInformation", "displayNativeAd: ${ex.message}")
            }
        }
    }
}