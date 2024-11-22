package livewallpaper.aod.screenlock.zipper.ui

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.clap.whistle.phonefinder.utilities.DbHelper
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import kotlinx.coroutines.delay
import livewallpaper.aod.screenlock.zipper.MainActivity.Companion.background
import livewallpaper.aod.screenlock.zipper.R
import livewallpaper.aod.screenlock.zipper.ads_manager.AdsManager
import livewallpaper.aod.screenlock.zipper.ads_manager.AdsManager.showOpenAd
import livewallpaper.aod.screenlock.zipper.ads_manager.interfaces.NativeListener
import livewallpaper.aod.screenlock.zipper.ads_manager.loadTwoInterAds
import livewallpaper.aod.screenlock.zipper.ads_manager.loadTwoInterAdsSplash
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isSplash = true
        counter = 0
        inter_frequency_count = 0
        adsManager = AdsManager.appAdsInit(activity ?: return)
        dbHelper = DbHelper(context ?: return)
        _binding?.mainbg?.setBackgroundResource(background)
        firebaseAnalytics("loading_fragment_open", "loading_fragment_open -->  Click")
        dbHelper?.getStringData(requireContext(), LANG_CODE, "en")?.let { setLocaleMain(it) }
        _binding?.mainbg?.setBackgroundResource(background)

        if (DataBasePref.LoadPref("firstTime", context ?: return) == 0) {
            DataBasePref.SavePref("firstTime", "1", context ?: return)
            DataBasePref.SavePref(ConstantValues.SpeedActivePref, "350", context ?: return)
            AppAdapter.SaveWallpaper(context ?: return, 7)
        }


        if (val_ad_app_open_screen) {
            loadTwoInterAds(
                ads = adsManager ?: return,
                activity = activity ?: return,
                remoteConfigNormal = true,
                adIdNormal = id_inter_main_medium,
                tagClass = "main_app_fragment"
            )
        } else {
            loadTwoInterAdsSplash(
                adsManager ?: return,
                activity ?: return,
                remoteConfigNormal = val_ad_inter_loading_screen,
                adIdNormal = id_inter_splash_Screen,
                "splash"
            )
        }

        if (isFlowOne) {
            lifecycleScope.launchWhenCreated {
                loadNative()
                delay(8000)
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
                delay(8000)
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
                    adsManager?.let {
                        showNormalInterAdSingle(
                            it,
                            activity ?: return@let,
                            remoteConfigNormal = val_ad_inter_loading_screen,
                            adIdNormal = id_inter_main_medium,
                            tagClass = "splash",
                            layout = _binding?.adsLay ?: return@launchWhenCreated
                        ) {
                            if (dbHelper?.getBooleanData(
                    context ?: return@showNormalInterAdSingle, IS_INTRO, false
                ) == false && val_on_bording_screen
            ) {
                firebaseAnalytics(
                    "loading_fragment_load_next_btn_intro",
                    "loading_fragment_load_next_btn_intro -->  Click"
                )
                findNavController().navigate(R.id.IntoScreenFragment)
            } else if (dbHelper?.getBooleanData(
                    context ?: return@showNormalInterAdSingle, IS_FIRST, false
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
                    }
                }

            }

        }
        setupBackPressedCallback {
            //Do Nothing
        }

        _binding?.next?.setOnClickListener {
            lifecycleScope.launchWhenCreated {
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
                    adsManager?.let {
                        showNormalInterAdSingle(
                            it,
                            activity ?: return@let,
                            remoteConfigNormal = val_ad_inter_loading_screen,
                            adIdNormal = id_inter_splash_Screen,
                            tagClass = "splash",
                            layout = _binding?.adsLay ?: return@let
                        ) {
                             if (dbHelper?.getBooleanData(
                    context ?: return@showNormalInterAdSingle, IS_INTRO, false
                ) == false && val_on_bording_screen
            ) {
                firebaseAnalytics(
                    "loading_fragment_load_next_btn_intro",
                    "loading_fragment_load_next_btn_intro -->  Click"
                )
                findNavController().navigate(R.id.IntoScreenFragment)
            } else if (dbHelper?.getBooleanData(
                    context ?: return@showNormalInterAdSingle, IS_FIRST, false
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
                    }
                }
            }

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

    private fun loadNative() {
        adsManager?.nativeAdsMain()?.loadNativeAd(activity ?: return,
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
                        adsManager?.nativeAds()?.loadNativeAd(
                            activity ?: return,
                            true,
                            id_native_screen,
                            object : NativeListener {
                            })
                    }
                    super.nativeAdLoaded(currentNativeAd)
                }

                override fun nativeAdFailed(loadAdError: LoadAdError) {
                    if (isAdded && isVisible && !isDetached) {
                        _binding?.nativeExitAd?.visibility = View.INVISIBLE
                        _binding?.adView?.visibility = View.INVISIBLE
                        _binding?.next?.visibility = View.VISIBLE
                        _binding?.animationView?.visibility = View.INVISIBLE
                    }
                    super.nativeAdFailed(loadAdError)
                }

                override fun nativeAdValidate(string: String) {
                    if (isAdded && isVisible && !isDetached) {
                        _binding?.nativeExitAd?.visibility = View.INVISIBLE
                        _binding?.adView?.visibility = View.INVISIBLE
                        _binding?.next?.visibility = View.VISIBLE
                        _binding?.animationView?.visibility = View.INVISIBLE
                    }
                    super.nativeAdValidate(string)
                }
            })

        loadTwoInterAds(
            ads = adsManager ?: return,
            activity = activity ?: return,
            remoteConfigNormal = val_inter_main_medium,
            adIdNormal = id_inter_main_medium,
            tagClass = "main_app_fragment"
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