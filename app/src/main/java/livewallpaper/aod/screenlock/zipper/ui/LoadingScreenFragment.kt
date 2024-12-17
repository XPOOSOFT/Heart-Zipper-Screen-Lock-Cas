package livewallpaper.aod.screenlock.zipper.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.clap.whistle.phonefinder.utilities.DbHelper
import com.cleversolutions.ads.AdError
import com.cleversolutions.ads.AdImpression
import com.cleversolutions.ads.AdSize
import com.cleversolutions.ads.AdViewListener
import com.cleversolutions.ads.android.CAS.manager
import com.cleversolutions.ads.android.CASBannerView
import com.hypersoft.admobads.adsconfig.interstitial.AdmobInterstitial
import com.hypersoft.admobads.adsconfig.interstitial.callbacks.InterstitialOnShowCallBack
import kotlinx.coroutines.delay
import livewallpaper.aod.screenlock.zipper.ads_cam.InterstitialAdManager
import livewallpaper.aod.screenlock.zipper.MyApplication.Companion.TAG
import livewallpaper.aod.screenlock.zipper.MyApplication.Companion.adManager
import livewallpaper.aod.screenlock.zipper.R
import livewallpaper.aod.screenlock.zipper.ads_cam.loadNativeBanner
import livewallpaper.aod.screenlock.zipper.ads_manager.AdsManager
import livewallpaper.aod.screenlock.zipper.ads_manager.AdsManager.showOpenAd
import livewallpaper.aod.screenlock.zipper.databinding.FragmentLoadingBinding
import livewallpaper.aod.screenlock.zipper.utilities.AppAdapter
import livewallpaper.aod.screenlock.zipper.utilities.BaseFragment
import livewallpaper.aod.screenlock.zipper.utilities.ConstantValues
import livewallpaper.aod.screenlock.zipper.utilities.DataBasePref
import livewallpaper.aod.screenlock.zipper.utilities.IS_FIRST
import livewallpaper.aod.screenlock.zipper.utilities.IS_INTRO
import livewallpaper.aod.screenlock.zipper.utilities.LANG_CODE
import livewallpaper.aod.screenlock.zipper.utilities.LANG_SCREEN
import livewallpaper.aod.screenlock.zipper.utilities.counter
import livewallpaper.aod.screenlock.zipper.utilities.firebaseAnalytics
import livewallpaper.aod.screenlock.zipper.utilities.inter_frequency_count
import livewallpaper.aod.screenlock.zipper.utilities.isFlowOne
import livewallpaper.aod.screenlock.zipper.utilities.isSplash
import livewallpaper.aod.screenlock.zipper.utilities.setLocaleMain
import livewallpaper.aod.screenlock.zipper.utilities.setupBackPressedCallback
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_app_open_screen
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_inter_loading_screen
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_native_loading_screen
import livewallpaper.aod.screenlock.zipper.utilities.val_on_bording_screen

class LoadingScreenFragment :
    BaseFragment<FragmentLoadingBinding>(FragmentLoadingBinding::inflate) {

    private var adsManager: AdsManager? = null
    private var dbHelper: DbHelper? = null
    private val admobInterstitial by lazy { AdmobInterstitial() }
    private var interstitialAdManager: InterstitialAdManager? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isSplash = true
        counter = 0
        inter_frequency_count = 0
        loadCASInterstitial(val_ad_inter_loading_screen)
        adsManager = AdsManager.appAdsInit(activity ?: return)
        dbHelper = DbHelper(context ?: return)
        firebaseAnalytics("loading_fragment_open", "loading_fragment_open -->  Click")
        dbHelper?.getStringData(requireContext(), LANG_CODE, "en")?.let { setLocaleMain(it) }
        if (DataBasePref.LoadPref("firstTime", context ?: return) == 0) {
            DataBasePref.SavePref("firstTime", "1", context ?: return)
            DataBasePref.SavePref(ConstantValues.SpeedActivePref, "350", context ?: return)
            AppAdapter.SaveWallpaper(context ?: return, 7)
        }
        if (isFlowOne) {
            lifecycleScope.launchWhenCreated {
                loadBanner(val_ad_native_loading_screen)
                delay(3000)
                if (isAdded && isVisible && !isDetached) {
                    _binding?.next?.visibility = View.VISIBLE
                    _binding?.animationView?.visibility = View.INVISIBLE
                }
            }
        } else {
            _binding?.next?.visibility = View.INVISIBLE
            _binding?.nativeExitAd?.visibility = View.INVISIBLE
            _binding?.animationView?.visibility = View.VISIBLE
            lifecycleScope.launchWhenCreated {
                delay(3000)
                if (val_ad_app_open_screen) {

                } else {

                }

            }

        }
        setupBackPressedCallback {
            //Do Nothing
        }
        _binding?.next?.setOnClickListener {
            if (val_ad_app_open_screen) {

            } else {
                showCASInterstitial(val_ad_inter_loading_screen){
                    moveToNext()
                }
            }

        }
    }

    private fun moveToNext() {
        if (dbHelper?.getBooleanData(
                context ?: return, IS_INTRO, false
            ) == false && val_on_bording_screen
        ) {
            firebaseAnalytics(
                "loading_fragment_load_next_btn_intro",
                "loading_fragment_load_next_btn_intro -->  Click"
            )
            findNavController().navigate(R.id.IntoScreenFragment)
        } else if (dbHelper?.getBooleanData(
                context ?: return, IS_FIRST, false
            ) == false
        ) {
            firebaseAnalytics(
                "loading_fragment_load_next_btn_language",
                "loading_fragment_load_next_btn_language -->  Click"
            )
            findNavController().navigate(
                R.id.LanguageFragment, bundleOf(LANG_SCREEN to true)
            )
        } else {
            firebaseAnalytics(
                "loading_fragment_load_next_btn_main",
                "loading_fragment_load_next_btn_main -->  Click"
            )
            findNavController().navigate(R.id.myMainMenuFragment, bundleOf("is_splash" to true))
        }

    }

    private fun loadCASInterstitial(isAdsShow: Boolean) {
        if (!isAdsShow) {
            return
        }
        // Initialize the InterstitialAdManager
        interstitialAdManager = InterstitialAdManager(context ?: return, adManager)
        // Load and show the ad
        interstitialAdManager?.loadAd(isAdsShow)
    }

    private fun showCASInterstitial(isAdsShow: Boolean,function: (()->Unit)) {
        if (interstitialAdManager == null) {
            function.invoke()
            return
        }
        interstitialAdManager?.showAdSplash(isAdsShow) {
            function.invoke()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.clear()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onLowMemory() {
        super.onLowMemory()
        activity?.finish()
    }

    private fun loadBanner(isAdsShow: Boolean) {
        _binding?.nativeExitAd?.apply {
            if (!isAdsShow) {
                visibility = View.INVISIBLE
                _binding?.adView?.visibility = View.INVISIBLE
                return
            }

            loadNativeBanner(
                context = requireContext(),
                isAdsShow = true,
                adSize = AdSize.LEADERBOARD, // Customize as needed
                onAdLoaded = { toggleVisibility(_binding?.nativeExitAd, true) },
                onAdFailed = { toggleVisibility(_binding?.nativeExitAd, false) },
                onAdPresented = { Log.d(TAG, "Ad presented from network: ${it.network}") },
                onAdClicked = { Log.d(TAG, "Ad clicked!") }
            )
        }
    }

    private fun toggleVisibility(view: View?, isVisible: Boolean) {
        _binding?.adView?.visibility=View.INVISIBLE
        view?.visibility = if (isVisible) View.VISIBLE else View.INVISIBLE
    }

    override fun onPause() {
        super.onPause()
        isSplash = false
    }

    override fun onResume() {
        super.onResume()
        isSplash = true
    }

}