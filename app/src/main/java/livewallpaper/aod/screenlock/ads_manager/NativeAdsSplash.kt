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
import com.google.android.gms.ads.nativead.NativeAdOptions.ADCHOICES_TOP_RIGHT
import com.google.android.gms.ads.nativead.NativeAdView
import com.google.android.material.snackbar.Snackbar
import livewallpaper.aod.screenlock.ads_manager.billing.BillingUtil
import livewallpaper.aod.screenlock.ads_manager.interfaces.NativeListener
import livewallpaper.aod.screenlock.zipper.BuildConfig
import livewallpaper.aod.screenlock.zipper.R
import livewallpaper.aod.screenlock.zipper.utilities.id_ads_bg
import livewallpaper.aod.screenlock.zipper.utilities.id_ads_button
import livewallpaper.aod.screenlock.zipper.utilities.id_ads_splash_bg
import livewallpaper.aod.screenlock.zipper.utilities.id_ads_splash_button
import livewallpaper.aod.screenlock.zipper.utilities.id_ads_splash_text_color
import livewallpaper.aod.screenlock.zipper.utilities.id_ads_text_color
import androidx.core.graphics.toColorInt

object NativeAdsSplash {

    private const val NativeAdsSplashLog = "native_log_splash"
    private const val NativeAdsSplashID = "ca-app-pub-3940256099942544/2247696110"

    private var nativeAds: NativeAdsSplash? = null

    private var isNativeLoading: Boolean = false
    private var nativeId: String? = null
    var currentNativeAd: NativeAd? = null

    fun nativeAds(): NativeAdsSplash {
        if (nativeAds == null) {
            nativeAds = NativeAdsSplash
            Log.d("adsInit", "   NativeClass")
        }
        return nativeAds as NativeAdsSplash
    }


    fun loadNativeAd(
        activity: Activity,
        addConfig: Boolean,
        nativeAdId: String,
        nativeListener: NativeListener
    ) {
        Log.d(
            NativeAdsSplashLog,
            "validate ${!BillingUtil(activity).checkPurchased(activity)}    $addConfig"
        )

        if (AdsManager.isNetworkAvailable(activity) && !BillingUtil(activity).checkPurchased(
                activity
            ) && addConfig
        ) {
            nativeId = nativeAdId
            val builder = AdLoader.Builder(
                activity,
                if (isDebug()) NativeAdsSplashID else nativeId ?: NativeAdsSplashID
            )
            if (isNativeLoading) {
                Log.d(NativeAdsSplashLog, "Already loading Ad")
//                loadedShoeNative(activity, nativeListener, builder, nativeAdId)
                return
            }
            if (currentNativeAd != null) {
                nativeListener.nativeAdLoaded(currentNativeAd)
                Log.d(NativeAdsSplashLog, "   Having loaded Ad")
                builder.forNativeAd { nativeAd ->
                    if (currentNativeAd != null) {
                        currentNativeAd?.destroy()
                    }
                    isNativeLoading = false
                    currentNativeAd = nativeAd
                    Log.d(NativeAdsSplashLog, "111 loaded native Ad")
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
                Log.d(NativeAdsSplashLog, "222 loaded native Ad")
                nativeListener.nativeAdLoaded(currentNativeAd)
            }

            val videoOptions = VideoOptions.Builder().setStartMuted(true).build()

            val adOptions = NativeAdOptions.Builder().setVideoOptions(videoOptions)
                .setAdChoicesPlacement(ADCHOICES_TOP_RIGHT).build()
            builder.withNativeAdOptions(adOptions)

            val adLoader = builder.withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    FullScreenAds.logEventForAds(NativeAdsSplashLog, "failed", nativeAdId)
                    if (isDebug()) {
                        Snackbar.make(
                            activity.window.decorView.rootView,
                            "AD Error Native: ${loadAdError.message}",
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                    Log.d(NativeAdsSplashLog, "failed native Ad  ${loadAdError.message}")
                    isNativeLoading = false
                    nativeListener.nativeAdFailed(loadAdError)

                }

                override fun onAdImpression() {
                    currentNativeAd = null
                    isNativeLoading = false
                    Log.d(NativeAdsSplashLog, "onAdImpression native Ad")
                    super.onAdImpression()
                }

                override fun onAdClicked() {
                    Log.d(NativeAdsSplashLog, "onAdClicked native Ad")
                    FullScreenAds.logEventForAds(NativeAdsSplashLog, "clicked", nativeAdId)
                    isNativeLoading = false
                    nativeListener.nativeAdClicked()
                    super.onAdClicked()
                }

                override fun onAdLoaded() {
                    isNativeLoading = false
                    FullScreenAds.logEventForAds(NativeAdsSplashLog, "loaded", nativeAdId)

                    Log.d(NativeAdsSplashLog, "onAdLoaded native Ad")
                    super.onAdLoaded()
                }
            }).build()

            adLoader.loadAd(AdRequest.Builder().build())
        } else {
            nativeListener.nativeAdValidate("hideAll")
            if (isDebug()) {
                Log.d(NativeAdsSplashLog, "config : $addConfig")
                Snackbar.make(
                    activity.window.decorView.rootView,
                    activity.getString(R.string.check_ads),
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }
    }


    fun isDebug(): Boolean {
        return BuildConfig.BUILD_TYPE == "debug"
    }

    @SuppressLint("ResourceType")
    fun nativeViewMedia(context: Context, nativeAd: NativeAd, adView: NativeAdView) {
        adView.callToActionView = adView.findViewById(R.id.custom_call_to_action)
        adView.iconView = adView.findViewById<ImageView>(R.id.custom_app_icon)!!
        adView.headlineView = adView.findViewById(R.id.custom_headline)
        adView.bodyView = adView.findViewById(R.id.custom_body)
//        adView.advertiserView = adView.findViewById(R.id.custom_advertiser)
//        adView.starRatingView = adView.findViewById(R.id.custom_stars)
        adView.mediaView = adView.findViewById(R.id.custom_media)


        try {
            (adView.findViewById<TextView>(R.id.custom_call_to_action)!!).backgroundTintList =
                ColorStateList.valueOf(id_ads_button.toColorInt())
            (adView.findViewById<NativeAdView>(R.id.layoutMedia)!!).backgroundTintList =
                ColorStateList.valueOf(Color.parseColor(id_ads_bg))
            (adView.findViewById<TextView>(R.id.custom_headline)!!).setTextColor(
                ColorStateList.valueOf(
                    id_ads_text_color.toColorInt()
                )
            )
            (adView.findViewById<TextView>(R.id.custom_body)!!).setTextColor(
                ColorStateList.valueOf(
                    id_ads_text_color.toColorInt()
                )
            )

        } catch (e: Exception) {
            e.printStackTrace()
        }
        adView.mediaView?.mediaContent = (nativeAd.mediaContent ?: return)

        (adView.headlineView as TextView).text = nativeAd.headline

        /*      if (nativeAd.starRating == null) {
                  adView.starRatingView?.visibility = View.GONE
              } else {
                  (adView.starRatingView as RatingBar).rating = nativeAd.starRating?.toFloat() ?: 0f
                  adView.starRatingView?.visibility = View.GONE
              }*/

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

//        if (nativeAd.advertiser == null) {
//            adView.advertiserView?.visibility = View.INVISIBLE
//        } else {
//            (adView.advertiserView as TextView).text = nativeAd.advertiser
//            adView.advertiserView?.visibility = View.VISIBLE
//        }

        adView.setNativeAd(nativeAd)

    }

    @SuppressLint("ResourceType", "UseCompatLoadingForColorStateLists", "CutPasteId")
    fun nativeViewMediaSplash(context: Context, nativeAd: NativeAd, adView: NativeAdView) {
        adView.callToActionView = adView.findViewById(R.id.custom_call_to_action)
        adView.iconView = adView.findViewById<ImageView>(R.id.custom_app_icon)!!
        adView.headlineView = adView.findViewById(R.id.custom_headline)
        adView.bodyView = adView.findViewById(R.id.custom_body)
//        adView.advertiserView = adView.findViewById(R.id.custom_advertiser)
//        adView.starRatingView = adView.findViewById(R.id.custom_stars)
        adView.mediaView = adView.findViewById(R.id.custom_media)
        try {
            (adView.findViewById<TextView>(R.id.custom_call_to_action)!!).backgroundTintList =
                ColorStateList.valueOf(id_ads_splash_button.toColorInt())
            (adView.findViewById<NativeAdView>(R.id.layoutMedia)!!).backgroundTintList =
                ColorStateList.valueOf(id_ads_splash_bg.toColorInt())
            (adView.findViewById<TextView>(R.id.custom_headline)!!).setTextColor(
                ColorStateList.valueOf(
                    id_ads_splash_text_color.toColorInt()
                )
            )
            (adView.findViewById<TextView>(R.id.custom_body)!!).setTextColor(
                ColorStateList.valueOf(
                    id_ads_splash_text_color.toColorInt()
                )
            )

        } catch (e: Exception) {
            e.printStackTrace()
        }
        adView.mediaView?.mediaContent = (nativeAd.mediaContent ?: return)

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

//        if (nativeAd.advertiser == null) {
//            adView.advertiserView?.visibility = View.INVISIBLE
//        } else {
//            (adView.advertiserView as TextView).text = nativeAd.advertiser
//            adView.advertiserView?.visibility = View.VISIBLE
//        }

        adView.setNativeAd(nativeAd)

    }


}