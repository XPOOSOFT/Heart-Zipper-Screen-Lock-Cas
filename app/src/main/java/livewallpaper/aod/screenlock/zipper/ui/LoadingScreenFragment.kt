package livewallpaper.aod.screenlock.zipper.ui

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.clap.whistle.phonefinder.utilities.DbHelper
import kotlinx.coroutines.delay
import livewallpaper.aod.screenlock.zipper.MyApplication.Companion.CAS_ID
import livewallpaper.aod.screenlock.zipper.MyApplication.Companion.adManager
import livewallpaper.aod.screenlock.zipper.MyApplication.Companion.appOpenManager
import livewallpaper.aod.screenlock.zipper.R
import livewallpaper.aod.screenlock.zipper.ads_cam.AdmobNative
import livewallpaper.aod.screenlock.zipper.ads_cam.InterstitialAdManager
import livewallpaper.aod.screenlock.zipper.ads_cam.NativeCallBack
import livewallpaper.aod.screenlock.zipper.ads_cam.NativeType
import livewallpaper.aod.screenlock.zipper.ads_cam.billing.BillingUtil
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
import livewallpaper.aod.screenlock.zipper.utilities.id_native_screen
import livewallpaper.aod.screenlock.zipper.utilities.inter_frequency_count
import livewallpaper.aod.screenlock.zipper.utilities.isFlowOne
import livewallpaper.aod.screenlock.zipper.utilities.isNetworkAvailable
import livewallpaper.aod.screenlock.zipper.utilities.isSplash
import livewallpaper.aod.screenlock.zipper.utilities.setLocaleMain
import livewallpaper.aod.screenlock.zipper.utilities.setupBackPressedCallback
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_app_open_screen
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_inter_loading_screen
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_native_loading_screen
import livewallpaper.aod.screenlock.zipper.utilities.val_on_bording_screen

class LoadingScreenFragment :
    BaseFragment<FragmentLoadingBinding>(FragmentLoadingBinding::inflate) {

    private var dbHelper: DbHelper? = null
    private var interstitialAdManager: InterstitialAdManager? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isSplash = true
        counter = 0
        inter_frequency_count = 0
        dbHelper = DbHelper(context ?: return)
        firebaseAnalytics("loading_fragment_open", "loading_fragment_open -->  Click")
        dbHelper?.getStringData(requireContext(), LANG_CODE, "en")?.let { setLocaleMain(it) }
        if (DataBasePref.LoadPref("firstTime", context ?: return) == 0) {
            DataBasePref.SavePref("firstTime", "1", context ?: return)
            DataBasePref.SavePref(ConstantValues.SpeedActivePref, "350", context ?: return)
            AppAdapter.SaveWallpaper(context ?: return, 7)
        }
        if (!val_ad_app_open_screen) {
            loadCASInterstitial(val_ad_inter_loading_screen)
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
                    appOpenManager?.showAdIfAvailable(activity?:return@launchWhenCreated)
                    moveToNext()
                } else {
                    showCASInterstitial(val_ad_inter_loading_screen) {
                        moveToNext()
                    }


                }

            }

        }
        setupBackPressedCallback {
            //Do Nothing
        }
        _binding?.next?.setOnClickListener {
            if(!isNetworkAvailable(context)){
                moveToNext()
                return@setOnClickListener
            }
            if (val_ad_app_open_screen) {
                appOpenManager?.showAdIfAvailable(activity?:return@setOnClickListener)
                moveToNext()
            } else {
                showCASInterstitial(val_ad_inter_loading_screen) {
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
        if   (!isAdsShow || !isNetworkAvailable(context)) {
            return
        }
        // Initialize the InterstitialAdManager
        interstitialAdManager = InterstitialAdManager(context ?: return, adManager?:return)
        // Load and show the ad
        interstitialAdManager?.loadAd(isAdsShow)
    }

    private fun showCASInterstitial(isAdsShow: Boolean, function: (() -> Unit)) {
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
        if(!isAdsShow){
            _binding?.adView?.visibility = View.INVISIBLE
            _binding?.nativeExitAd?.visibility = View.INVISIBLE
            return
        }
        AdmobNative().loadNativeAds(
            activity,
            _binding?.nativeExitAd!!,
            id_native_screen,
            if (isAdsShow)
                1 else 0,
            isAppPurchased = BillingUtil(activity ?: return).checkPurchased(activity ?: return),
            isInternetConnected = isNetworkAvailable(activity),
            nativeType = NativeType.LARGE,
            nativeCallBack = object : NativeCallBack {
                override fun onAdFailedToLoad(adError: String) {
                    _binding?.adView?.visibility = View.INVISIBLE
                    _binding?.nativeExitAd?.visibility = View.INVISIBLE
                }

                override fun onAdLoaded() {
                    _binding?.adView?.visibility = View.INVISIBLE
                }

                override fun onAdImpression() {
                    _binding?.adView?.visibility = View.INVISIBLE
                }
            }
        )
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