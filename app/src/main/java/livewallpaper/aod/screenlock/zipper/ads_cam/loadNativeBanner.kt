package livewallpaper.aod.screenlock.zipper.ads_cam

import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup
import com.cleversolutions.ads.AdError
import com.cleversolutions.ads.AdImpression
import com.cleversolutions.ads.AdSize
import com.cleversolutions.ads.AdViewListener
import com.cleversolutions.ads.android.CAS.manager
import com.cleversolutions.ads.android.CASBannerView
import livewallpaper.aod.screenlock.zipper.utilities.isNetworkAvailable

fun ViewGroup.loadNativeBanner(
    context: Context?,
    isAdsShow: Boolean,
    adSize: AdSize? = context?.let { AdSize.getAdaptiveBannerInScreen(it) },
    onAdLoaded: (() -> Unit)? = null,
    onAdFailed: ((AdError) -> Unit)? = null,
    onAdPresented: ((AdImpression) -> Unit)? = null,
    onAdClicked: (() -> Unit)? = null
) {
//    if   (!isAdsShow || !isNetworkAvailable(context)) {
//        this.visibility = View.INVISIBLE
//        onAdFailed?.invoke()
//        return
//    }

    val bannerView = CASBannerView(context ?: return, manager).apply {
        size = adSize?:return
        adListener = object : AdViewListener {
            override fun onAdViewLoaded(view: CASBannerView) {
                this@loadNativeBanner.visibility = View.VISIBLE
                onAdLoaded?.invoke()
                Log.d("AdBanner", "Banner Ad loaded and ready to present")
            }

            override fun onAdViewFailed(view: CASBannerView, error: AdError) {
                this@loadNativeBanner.visibility = View.INVISIBLE
                onAdFailed?.invoke(error)
                Log.e("AdBanner", "Banner Ad received error: ${error.message}")
            }

            override fun onAdViewPresented(view: CASBannerView, info: AdImpression) {
                onAdPresented?.invoke(info)
                Log.d("AdBanner", "Banner Ad presented from ${info.network}")
            }

            override fun onAdViewClicked(view: CASBannerView) {
                onAdClicked?.invoke()
                Log.d("AdBanner", "Banner Ad received Click action")
            }
        }
    }

    // Add the banner to the container
    this.addView(bannerView)
}
