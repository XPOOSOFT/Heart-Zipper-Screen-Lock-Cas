package livewallpaper.aod.screenlock.zipper.ui

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import livewallpaper.aod.screenlock.zipper.R
import livewallpaper.aod.screenlock.zipper.ads_manager.AdmobNative
import livewallpaper.aod.screenlock.zipper.ads_manager.AdsManager
import livewallpaper.aod.screenlock.zipper.ads_manager.billing.BillingUtil
import livewallpaper.aod.screenlock.zipper.ads_manager.interfaces.NativeCallBack
import livewallpaper.aod.screenlock.zipper.ads_manager.interfaces.NativeListener
import livewallpaper.aod.screenlock.zipper.ads_manager.interfaces.NativeType
import livewallpaper.aod.screenlock.zipper.databinding.FragmentExitScreenBinding
import livewallpaper.aod.screenlock.zipper.utilities.BaseFragment
import livewallpaper.aod.screenlock.zipper.utilities.id_native_screen
import livewallpaper.aod.screenlock.zipper.utilities.native_precashe_copunt_current
import livewallpaper.aod.screenlock.zipper.utilities.native_precashe_counter
import livewallpaper.aod.screenlock.zipper.utilities.setupBackPressedCallback
import livewallpaper.aod.screenlock.zipper.utilities.val_exit_dialog_native

class FragmentExitScreen : BaseFragment<FragmentExitScreenBinding>(FragmentExitScreenBinding::inflate) {

    private var adsManager: AdsManager? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {
            adsManager = AdsManager.appAdsInit(activity ?: return)
            mCLickListener()
            loadNative()
            setupBackPressedCallback {
                findNavController().popBackStack()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    private val admobNative by lazy { AdmobNative() }
    private fun loadNative() {
        if (native_precashe_copunt_current >= native_precashe_counter) {
            admobNative.loadNativeAds(
                activity,
                _binding?.nativeExitAd!!,
                id_native_screen,
                if (val_exit_dialog_native)
                    1 else 0,
                isAppPurchased = BillingUtil(activity?:return).checkPurchased(activity?:return),
                isInternetConnected = AdsManager.isNetworkAvailable(activity),
                nativeType = NativeType.LARGE,
                nativeCallBack = object : NativeCallBack {
                    override fun onAdFailedToLoad(adError: String) {
                        _binding?.adView?.visibility = View.GONE}
                    override fun onAdLoaded() {
                        _binding?.adView?.visibility = View.GONE}
                    override fun onAdImpression() {
                        _binding?.adView?.visibility = View.GONE}
                }
            )
        } else {
            adsManager?.nativeAds()?.loadNativeAd(
                activity ?: return,
                val_exit_dialog_native,
                id_native_screen,
                object : NativeListener {
                    override fun nativeAdLoaded(currentNativeAd: NativeAd?) {
                        if (isAdded && isVisible && !isDetached) {
                            _binding?.nativeExitAd?.visibility = View.VISIBLE
                            _binding?.adView?.visibility = View.GONE
                            val adView = layoutInflater.inflate(
                                R.layout.ad_unified_media,
                                null
                            ) as NativeAdView
                            adsManager?.nativeAds()?.nativeViewMedia(
                                context ?: return,
                                currentNativeAd ?: return,
                                adView
                            )
                            _binding?.nativeExitAd?.removeAllViews()
                            _binding?.nativeExitAd?.addView(adView)
                        }
                        super.nativeAdLoaded(currentNativeAd)
                    }

                    override fun nativeAdFailed(loadAdError: LoadAdError) {
                        if (isAdded && isVisible && !isDetached) {
                            _binding?.nativeExitAd?.visibility = View.INVISIBLE
                            _binding?.adView?.visibility = View.INVISIBLE
                        }
                        super.nativeAdFailed(loadAdError)
                    }

                    override fun nativeAdValidate(string: String) {
                        if (isAdded && isVisible && !isDetached) {
                            _binding?.nativeExitAd?.visibility = View.INVISIBLE
                            _binding?.adView?.visibility = View.INVISIBLE
                        }
                        super.nativeAdValidate(string)
                    }
                })
        }
    }

    private fun mCLickListener() {

        _binding?.yesBtn?.setOnClickListener {
            findNavController().navigateUp()
        }
        _binding?.noBtn?.setOnClickListener {
            activity?.finish()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
    override fun onLowMemory() {
        super.onLowMemory()
        activity?.finish()
    }
}