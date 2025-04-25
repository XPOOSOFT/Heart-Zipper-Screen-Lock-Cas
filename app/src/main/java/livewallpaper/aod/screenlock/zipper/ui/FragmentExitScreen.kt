package livewallpaper.aod.screenlock.zipper.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.navigation.fragment.findNavController
import livewallpaper.aod.screenlock.ads_manager.AdmobNative
import livewallpaper.aod.screenlock.ads_manager.AdsManager
import livewallpaper.aod.screenlock.ads_manager.billing.BillingUtil
import livewallpaper.aod.screenlock.ads_manager.interfaces.NativeCallBack
import com.google.android.gms.ads.nativead.NativeAdView
import livewallpaper.aod.screenlock.zipper.databinding.FragmentExitScreenBinding
import livewallpaper.aod.screenlock.zipper.utilities.BaseFragment
import livewallpaper.aod.screenlock.zipper.utilities.firebaseAnalytics
import livewallpaper.aod.screenlock.zipper.utilities.getNativeLayout
import livewallpaper.aod.screenlock.zipper.utilities.id_native_screen
import livewallpaper.aod.screenlock.zipper.utilities.setupBackPressedCallback
import livewallpaper.aod.screenlock.zipper.utilities.thankyou_bottom
import livewallpaper.aod.screenlock.zipper.utilities.val_exit_dialog_native

class FragmentExitScreen :
    BaseFragment<FragmentExitScreenBinding>(FragmentExitScreenBinding::inflate) {

    private var adsManager: AdsManager? = null

    private val admobNative by lazy { AdmobNative() }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseAnalytics("fragment_open_exit_screen", "fragment_open_exit_screen")
        adsManager = AdsManager.appAdsInit(activity ?: return)
        mCLickListener()
        loadNative()
        setupBackPressedCallback {
            findNavController().popBackStack()
        }
    }

    @SuppressLint("SuspiciousIndentation")
    private fun loadNative() {
        Log.d("AdsInformation", "Call Admob Native")
        val adView = activity?.layoutInflater?.inflate(
            getNativeLayout(
                thankyou_bottom, _binding?.nativeExitAd!!,
                activity?:return
            ),
            null
        ) as NativeAdView
//        if (native_precashe_copunt_current >= native_precashe_counter) {
        admobNative.loadNativeAds(
            activity,
            _binding?.nativeExitAd!!,
            id_native_screen,
            if (val_exit_dialog_native)
                1 else 0,
            isAppPurchased = BillingUtil(activity?:return).checkPurchased(activity?:return),
            isInternetConnected = AdsManager.isNetworkAvailable(activity),
            nativeType = thankyou_bottom,
            nativeCallBack = object : NativeCallBack {
                override fun onAdFailedToLoad(adError: String) {
                    _binding?.nativeExitAd?.visibility = View.GONE
                    _binding?.adView?.visibility = View.GONE}
                override fun onAdLoaded() {
                    _binding?.adView?.visibility = View.GONE}
                override fun onAdImpression() {
                    _binding?.adView?.visibility = View.GONE
                }
            }
        )
//        } else {
//            adsManager?.nativeAdsMain()?.loadNativeAd(
//                activity ?: return,
//                val_exit_dialog_native,
//                id_native_screen,
//                object : NativeListener {
//                    override fun nativeAdLoaded(currentNativeAd: NativeAd?) {
//                        if(isVisible && isAdded && !isDetached) {
//                            Log.d("native_log_i", "nativeAdLoaded: main ad")
//                            _binding?.nativeExitAd?.visibility = View.VISIBLE
//                            _binding?.adView?.visibility = View.GONE
//                            val adView =
//                                layoutInflater.inflate(
//                                    R.layout.ad_undified_media_heigh,
//                                    null
//                                ) as NativeAdView
//                            adsManager?.nativeAdsMain()
//                                ?.nativeViewMedia(
//                                    context ?: return,
//                                    currentNativeAd ?: return,
//                                    adView
//                                )
//                            _binding?.nativeExitAd?.removeAllViews()
//                            _binding?.nativeExitAd?.addView(adView)
//                        }
//                        super.nativeAdLoaded(currentNativeAd)
//                    }
//
//                    override fun nativeAdFailed(loadAdError: LoadAdError) {
//                        if (isVisible && isAdded && !isDetached) {
//                            _binding?.nativeExitAd?.visibility = View.INVISIBLE
//                            _binding?.adView?.visibility = View.INVISIBLE
//                        }
//                        super.nativeAdFailed(loadAdError)
//                    }
//
//                    override fun nativeAdValidate(string: String) {
//                        if (isVisible && isAdded && !isDetached) {
//                            _binding?.nativeExitAd?.visibility = View.INVISIBLE
//                            _binding?.adView?.visibility = View.INVISIBLE
//                        }
//                        super.nativeAdValidate(string)
//                    }
//                })
//        }
    }

    private fun mCLickListener() {

        _binding?.yesBtn?.setOnClickListener {
            findNavController().navigateUp()
        }
        _binding?.noBtn?.setOnClickListener {
            activity?.finish()
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.clear()
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}