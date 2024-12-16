package livewallpaper.aod.screenlock.zipper.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.applovin.sdk.AppLovinSdkUtils.runOnUiThread
import com.clap.whistle.phonefinder.utilities.DbHelper
import com.cleversolutions.ads.AdError
import com.cleversolutions.ads.AdImpression
import com.cleversolutions.ads.AdLoadCallback
import com.cleversolutions.ads.AdPaidCallback
import com.cleversolutions.ads.AdSize
import com.cleversolutions.ads.AdType
import com.cleversolutions.ads.AdViewListener
import com.cleversolutions.ads.LoadingManagerMode
import com.cleversolutions.ads.MediationManager
import com.cleversolutions.ads.android.CAS
import com.cleversolutions.ads.android.CAS.manager
import com.cleversolutions.ads.android.CASBannerView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import com.hypersoft.admobads.adsconfig.interstitial.AdmobInterstitial
import com.hypersoft.admobads.adsconfig.interstitial.callbacks.InterstitialOnShowCallBack
import kotlinx.coroutines.delay
import livewallpaper.aod.screenlock.zipper.InterstitialAdManager
import livewallpaper.aod.screenlock.zipper.MyApplication.Companion.TAG
import livewallpaper.aod.screenlock.zipper.MyApplication.Companion.adManager
import livewallpaper.aod.screenlock.zipper.R
import livewallpaper.aod.screenlock.zipper.ads_manager.AdsManager
import livewallpaper.aod.screenlock.zipper.ads_manager.AdsManager.showOpenAd
import livewallpaper.aod.screenlock.zipper.ads_manager.interfaces.NativeListener
import livewallpaper.aod.screenlock.zipper.ads_manager.showNormalInterAdSingle
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
import livewallpaper.aod.screenlock.zipper.utilities.id_inter_main_medium
import livewallpaper.aod.screenlock.zipper.utilities.id_inter_splash_Screen
import livewallpaper.aod.screenlock.zipper.utilities.id_native_screen
import livewallpaper.aod.screenlock.zipper.utilities.id_splash_native
import livewallpaper.aod.screenlock.zipper.utilities.inter_frequency_count
import livewallpaper.aod.screenlock.zipper.utilities.isFlowOne
import livewallpaper.aod.screenlock.zipper.utilities.isSplash
import livewallpaper.aod.screenlock.zipper.utilities.is_val_ad_inter_loading_screen
import livewallpaper.aod.screenlock.zipper.utilities.setLocaleMain
import livewallpaper.aod.screenlock.zipper.utilities.setupBackPressedCallback
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_app_open_screen
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_inter_loading_screen
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_native_loading_screen
import livewallpaper.aod.screenlock.zipper.utilities.val_inter_main_medium
import livewallpaper.aod.screenlock.zipper.utilities.val_on_bording_screen

class LoadingScreenFragment :
    BaseFragment<FragmentLoadingBinding>(FragmentLoadingBinding::inflate) {

    private var adsManager: AdsManager? = null
    private var dbHelper: DbHelper? = null
    private val admobInterstitial by lazy { AdmobInterstitial() }
    private lateinit var interstitialAdManager: InterstitialAdManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isSplash = true
        counter = 0
        inter_frequency_count = 0
        loadCASInstertital()
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
                loadNative()
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
                    isSplash = true
                    showOpenAd(activity ?: return@launchWhenCreated) {

                    }
                    if (dbHelper?.getBooleanData(
                    context ?: return@launchWhenCreated, IS_INTRO, false
                ) == false && val_on_bording_screen
            ) {
                firebaseAnalytics(
                    "loading_fragment_load_next_btn_intro",
                    "loading_fragment_load_next_btn_intro -->  Click"
                )
                findNavController().navigate(R.id.IntoScreenFragment)
            } else if (dbHelper?.getBooleanData(
                    context ?: return@launchWhenCreated, IS_FIRST, false
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
                } else {
                    admobInterstitial.showInterstitialAd(
                        activity?:return@launchWhenCreated,
                        object : InterstitialOnShowCallBack {
                            override fun onAdDismissedFullScreenContent() {}
                            override fun onAdFailedToShowFullScreenContent() {
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
                            override fun onAdShowedFullScreenContent() {
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
                            override fun onAdImpression() {}
                        }
                    )
//                    adsManager?.let {
//                        showNormalInterAdSingle(
//                            it,
//                            activity ?: return@let,
//                            remoteConfigNormal = val_ad_inter_loading_screen,
//                            adIdNormal = id_inter_main_medium,
//                            tagClass = "splash_ld",
//                            layout = _binding?.adsLay ?: return@launchWhenCreated
//                        ) {
//
//                        }
//                    }
                }

            }

        }
        setupBackPressedCallback {
            //Do Nothing
        }

        _binding?.next?.setOnClickListener {
            interstitialAdManager.showAd {
                if (dbHelper?.getBooleanData(
                        context ?: return@showAd, IS_INTRO, false
                    ) == false && val_on_bording_screen
                ) {
                    firebaseAnalytics(
                        "loading_fragment_load_next_btn_intro",
                        "loading_fragment_load_next_btn_intro -->  Click"
                    )
                    findNavController().navigate(R.id.IntoScreenFragment)
                } else if (dbHelper?.getBooleanData(
                        context ?: return@showAd, IS_FIRST, false
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

 /*           lifecycleScope.launchWhenCreated {
                if (val_ad_app_open_screen) {
                    isSplash = true
                    showOpenAd(activity ?: return@launchWhenCreated) {
                    }
                     if (dbHelper?.getBooleanData(
                    context ?: return@launchWhenCreated, IS_INTRO, false
                ) == false && val_on_bording_screen
            ) {
                firebaseAnalytics(
                    "loading_fragment_load_next_btn_intro",
                    "loading_fragment_load_next_btn_intro -->  Click"
                )
                findNavController().navigate(R.id.IntoScreenFragment)
            } else if (dbHelper?.getBooleanData(
                    context ?: return@launchWhenCreated, IS_FIRST, false
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
                } else {
                    admobInterstitial.showInterstitialAd(
                        activity?:return@launchWhenCreated,
                        object : InterstitialOnShowCallBack {
                            override fun onAdDismissedFullScreenContent() {}
                            override fun onAdFailedToShowFullScreenContent() {
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
                            override fun onAdShowedFullScreenContent() {
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
                            override fun onAdImpression() {}
                        }
                    )
//                    adsManager?.let {
//                        showNormalInterAdSingle(
//                            it,
//                            activity ?: return@let,
//                            remoteConfigNormal = val_ad_inter_loading_screen && is_val_ad_inter_loading_screen,
//                            adIdNormal = id_inter_splash_Screen,
//                            tagClass = "splash",
//                            layout = _binding?.adsLay ?: return@let
//                        ) {
//                             if (dbHelper?.getBooleanData(
//                    context ?: return@showNormalInterAdSingle, IS_INTRO, false
//                ) == false && val_on_bording_screen
//            ) {
//                firebaseAnalytics(
//                    "loading_fragment_load_next_btn_intro",
//                    "loading_fragment_load_next_btn_intro -->  Click"
//                )
//                findNavController().navigate(R.id.IntoScreenFragment)
//            } else if (dbHelper?.getBooleanData(
//                    context ?: return@showNormalInterAdSingle, IS_FIRST, false
//                ) == false
//            ) {
//                firebaseAnalytics(
//                    "loading_fragment_load_next_btn_language",
//                    "loading_fragment_load_next_btn_language -->  Click"
//                )
//                findNavController().navigate(
//                    R.id.LanguageFragment, bundleOf(LANG_SCREEN to true)
//                )
//            } else {
//                firebaseAnalytics(
//                    "loading_fragment_load_next_btn_main",
//                    "loading_fragment_load_next_btn_main -->  Click"
//                )
//                    findNavController().navigate(R.id.myMainMenuFragment, bundleOf("is_splash" to true))
//            }
//                        }
//                    }
                }
            }*/

        }

    }

    private fun loadCASInstertital() {
        // Initialize the InterstitialAdManager
        interstitialAdManager = InterstitialAdManager(context?:return, adManager)
        // Load and show the ad
        interstitialAdManager.loadAd()
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

    private fun loadNative() {
            val bannerView = CASBannerView(context?:return, manager)
            // Set required Ad size
            bannerView.size = AdSize.getAdaptiveBannerInScreen(context?:return)
            //bannerView.size = AdSize.BANNER
//            bannerView.size = AdSize.LEADERBOARD
            bannerView.size = AdSize.MEDIUM_RECTANGLE
//            bannerView.size = AdSize.MEDIUM_RECTANGLE
            // Set Ad content listener
            bannerView.adListener = object : AdViewListener {
                override fun onAdViewLoaded(view: CASBannerView) {
                    _binding?.adView?.visibility=View.INVISIBLE
                    Log.d(TAG, "Banner Ad loaded and ready to present")
                }

                override fun onAdViewFailed(view: CASBannerView, error: AdError) {
                    _binding?.adView?.visibility=View.INVISIBLE
                    _binding?.nativeExitAd?.visibility=View.INVISIBLE
                    Log.e(TAG, "Banner Ad received error: " + error.message)
                }

                override fun onAdViewPresented(view: CASBannerView, info: AdImpression) {
                    _binding?.adView?.visibility=View.INVISIBLE
                    Log.d(TAG, "Banner Ad presented from " + info.network)
                }

                override fun onAdViewClicked(view: CASBannerView) {
                    _binding?.adView?.visibility=View.INVISIBLE
                    Log.d(TAG, "Banner Ad received Click action")
                }
            }
            // Add view to container
        _binding?.nativeExitAd?.addView(bannerView)
            // Set controls
/*        adsManager?.nativeAdsMain()?.loadNativeAd(activity ?: return,
            val_ad_native_loading_screen,
            id_splash_native,
            object : NativeListener {
                override fun nativeAdLoaded(currentNativeAd: NativeAd?) {
                    if (isAdded && isVisible && !isDetached) {
                        _binding?.nativeExitAd?.visibility = View.VISIBLE
                        _binding?.adView?.visibility = View.GONE
                        val adView =
                            layoutInflater.inflate(R.layout.ad_unified_media, null) as NativeAdView
                        adsManager?.nativeAdsMain()
                            ?.nativeViewMediaSplash(
                                context ?: return,
                                currentNativeAd ?: return,
                                adView
                            )
                        _binding?.nativeExitAd?.removeAllViews()
                        _binding?.nativeExitAd?.addView(adView)
                        _binding?.next?.visibility = View.VISIBLE
                        _binding?.animationView?.visibility = View.INVISIBLE

                    }
                    super.nativeAdLoaded(currentNativeAd)
                }

                override fun nativeAdFailed(loadAdError: LoadAdError) {
                    if (isAdded && isVisible && !isDetached) {
                        _binding?.nativeExitAd?.visibility = View.INVISIBLE
                        _binding?.adView?.visibility = View.INVISIBLE
                        _binding?.next?.visibility = View.VISIBLE
                        _binding?.animationView?.visibility = View.INVISIBLE
                        adsManager?.nativeAds()?.loadNativeAd(
                            activity ?: return,
                            true,
                            id_native_screen,
                            object : NativeListener {
                            })
                    }
                    super.nativeAdFailed(loadAdError)
                }

                override fun nativeAdValidate(string: String) {
                    if (isAdded && isVisible && !isDetached) {
                        _binding?.nativeExitAd?.visibility = View.INVISIBLE
                        _binding?.adView?.visibility = View.INVISIBLE
                        _binding?.next?.visibility = View.VISIBLE
                        _binding?.animationView?.visibility = View.INVISIBLE
                        adsManager?.nativeAds()?.loadNativeAd(
                            activity ?: return,
                            true,
                            id_native_screen,
                            object : NativeListener {
                            })
                    }
                    super.nativeAdValidate(string)
                }
            })

        adsManager?.nativeAds()?.loadNativeAd(
            activity ?: return,
            true,
            id_native_screen,
            object : NativeListener {
            })*/
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