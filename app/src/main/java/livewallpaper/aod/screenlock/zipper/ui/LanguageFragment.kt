package livewallpaper.aod.screenlock.zipper.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.clap.whistle.phonefinder.utilities.DbHelper
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import livewallpaper.aod.screenlock.zipper.MyApplication.Companion.adManager
import livewallpaper.aod.screenlock.zipper.R
import livewallpaper.aod.screenlock.zipper.adapter.LanguageGridAdapter
import livewallpaper.aod.screenlock.zipper.ads_cam.InterstitialAdManager
import livewallpaper.aod.screenlock.zipper.ads_manager.AdmobNative
import livewallpaper.aod.screenlock.zipper.ads_manager.AdsManager
import livewallpaper.aod.screenlock.zipper.ads_manager.billing.BillingUtil
import livewallpaper.aod.screenlock.zipper.ads_manager.billing.PurchasePrefs
import livewallpaper.aod.screenlock.zipper.ads_manager.interfaces.NativeCallBack
import livewallpaper.aod.screenlock.zipper.ads_manager.interfaces.NativeListener
import livewallpaper.aod.screenlock.zipper.ads_manager.interfaces.NativeType
import livewallpaper.aod.screenlock.zipper.ads_manager.showTwoInterAd
import livewallpaper.aod.screenlock.zipper.databinding.FragmentLanguageBinding
import livewallpaper.aod.screenlock.zipper.model.LanguageModel
import livewallpaper.aod.screenlock.zipper.utilities.BaseFragment
import livewallpaper.aod.screenlock.zipper.utilities.IS_FIRST
import livewallpaper.aod.screenlock.zipper.utilities.LANG_CODE
import livewallpaper.aod.screenlock.zipper.utilities.LANG_SCREEN
import livewallpaper.aod.screenlock.zipper.utilities.clickWithThrottle
import livewallpaper.aod.screenlock.zipper.utilities.firebaseAnalytics
import livewallpaper.aod.screenlock.zipper.utilities.id_inter_main_medium
import livewallpaper.aod.screenlock.zipper.utilities.id_native_screen
import livewallpaper.aod.screenlock.zipper.utilities.native_precashe_copunt_current
import livewallpaper.aod.screenlock.zipper.utilities.native_precashe_counter
import livewallpaper.aod.screenlock.zipper.utilities.setLocaleMain
import livewallpaper.aod.screenlock.zipper.utilities.setupBackPressedCallback
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_inter_language_screen
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_native_language_screen
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_native_language_screen_h
import livewallpaper.aod.screenlock.zipper.utilities.val_is_inapp_splash


class LanguageFragment : BaseFragment<FragmentLanguageBinding>(FragmentLanguageBinding::inflate) {
    private var positionSelected: String = "en"
    private var isLangScreen: Boolean = false
    private var isValue: Int = 0
    private var sharedPrefUtils: DbHelper? = null
    private var languageGridAdapter: LanguageGridAdapter? = null
    var list: ArrayList<LanguageModel>? = null

    //    private var adsManager: AdsManager? = null
    private var interstitialAdManager: InterstitialAdManager? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {
            firebaseAnalytics("language_fragment_open", "language_fragment_open -->  Click")
            requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
            sharedPrefUtils = DbHelper(context ?: return)
            positionSelected =
                sharedPrefUtils?.getStringData(requireContext(), LANG_CODE, "en") ?: "en"
            initializeData()
//            adsManager = AdsManager.appAdsInit(activity ?: return)
            arguments?.let {
                isLangScreen = it.getBoolean(LANG_SCREEN)
            }

            _binding?.forwardBtn?.clickWithThrottle {
                sharedPrefUtils?.saveData(requireContext(), IS_FIRST, true)
                if (!isLangScreen) {
                    firebaseAnalytics(
                        "language_fragment_forward_btn_from",
                        "language_fragment_forward_btn_from -->  Click"
                    )
                    sharedPrefUtils?.saveData(requireContext(), LANG_CODE, positionSelected) ?: "en"
                    setLocaleMain(positionSelected)
                    findNavController().popBackStack()
                } else {
                    firebaseAnalytics(
                        "language_fragment_forward_btn_from",
                        "language_fragment_forward_btn_from -->  Click"
                    )
                    sharedPrefUtils?.saveData(requireContext(), LANG_CODE, positionSelected) ?: "en"
                    setLocaleMain(positionSelected)
                    sharedPrefUtils?.saveData(requireContext(), IS_FIRST, true)

                    showCASInterstitial(val_ad_inter_language_screen) {
                        if (PurchasePrefs(context).getBoolean("inApp") || !val_is_inapp_splash) {
                            findNavController().navigate(R.id.myMainMenuFragment)
                        } else {
                            findNavController().navigate(
                                R.id.FragmentBuyScreen,
                                bundleOf("isSplash" to true)
                            )
                        }

                    }
                    /*      adsManager?.let {
                              showTwoInterAd(
                                  ads = it,
                                  activity = activity ?: requireActivity(),
                                  remoteConfigNormal = val_ad_inter_language_screen,
                                  adIdNormal = id_inter_main_medium,
                                  tagClass = "language_first",
                                  isBackPress = true,
                                  layout = _binding?.adsLay ?: return@let
                              ) {
                                  if (PurchasePrefs(context).getBoolean("inApp") || !val_is_inapp_splash) {
                                      findNavController().navigate(R.id.myMainMenuFragment)
                                  } else {
                                      findNavController().navigate(
                                          R.id.FragmentBuyScreen,
                                          bundleOf("isSplash" to true)
                                      )
                                  }
                              }
                          }*/
                }
            }

            languageGridAdapter =
                LanguageGridAdapter(list ?: return, activity ?: return,
                    clickItem = {
                        positionSelected = it.country_code
                        languageGridAdapter?.selectLanguage(positionSelected)
                        _binding?.forwardBtn?.visibility = View.VISIBLE
                    }
                )
            if (!isLangScreen) {
                languageGridAdapter?.selectLanguage(positionSelected)
            }
            _binding?.conversationDetail?.adapter = languageGridAdapter
            _binding?.backBtn?.clickWithThrottle {
                sharedPrefUtils?.saveData(requireContext(), IS_FIRST, true)
                if (!isLangScreen) {
                    firebaseAnalytics(
                        "language_fragment_forward_btn_from",
                        "language_fragment_forward_btn_from -->  Click"
                    )
                    sharedPrefUtils?.saveData(requireContext(), LANG_CODE, positionSelected) ?: "en"
                    setLocaleMain(positionSelected)
                    findNavController().popBackStack()
                } else {
                    firebaseAnalytics(
                        "language_fragment_forward_btn_from",
                        "language_fragment_forward_btn_from -->  Click"
                    )
                    sharedPrefUtils?.saveData(requireContext(), LANG_CODE, positionSelected) ?: "en"
                    setLocaleMain(positionSelected)
                    sharedPrefUtils?.saveData(requireContext(), IS_FIRST, true)
                    if (PurchasePrefs(context).getBoolean("inApp") || !val_is_inapp_splash) {
                        findNavController().navigate(R.id.myMainMenuFragment)
                    } else {
                        findNavController().navigate(
                            R.id.FragmentBuyScreen,
                            bundleOf("isSplash" to true)
                        )
                    }
                    /*    adsManager?.let {
                            showTwoInterAd(
                                ads = it,
                                activity = activity ?: requireActivity(),
                                remoteConfigNormal = val_ad_inter_language_screen,
                                adIdNormal = id_inter_main_medium,
                                tagClass = "language_first",
                                isBackPress = true,
                                layout = _binding?.adsLay ?: return@let
                            ) {
                                if (PurchasePrefs(context).getBoolean("inApp") || !val_is_inapp_splash) {
                                    findNavController().navigate(R.id.myMainMenuFragment)
                                } else {
                                    findNavController().navigate(
                                        R.id.FragmentBuyScreen,
                                        bundleOf("isSplash" to true)
                                    )
                                }
                            }
                        }*/
                }
            }
            setupBackPressedCallback {
                sharedPrefUtils?.saveData(requireContext(), IS_FIRST, true)
                if (!isLangScreen) {
                    firebaseAnalytics(
                        "language_fragment_forward_btn_from",
                        "language_fragment_forward_btn_from -->  Click"
                    )
                    sharedPrefUtils?.saveData(requireContext(), LANG_CODE, positionSelected) ?: "en"
                    setLocaleMain(positionSelected)
                    findNavController().popBackStack()
                } else {
                    firebaseAnalytics(
                        "language_fragment_forward_btn_from",
                        "language_fragment_forward_btn_from -->  Click"
                    )
                    sharedPrefUtils?.saveData(requireContext(), LANG_CODE, positionSelected) ?: "en"
                    setLocaleMain(positionSelected)
                    sharedPrefUtils?.saveData(requireContext(), IS_FIRST, true)
                    showCASInterstitial(val_ad_inter_language_screen){
                        if (PurchasePrefs(context).getBoolean("inApp") || !val_is_inapp_splash) {
                            findNavController().navigate(R.id.myMainMenuFragment)
                        } else {
                            findNavController().navigate(
                                R.id.FragmentBuyScreen,
                                bundleOf("isSplash" to true)
                            )
                        }
                    }
/*                    adsManager?.let {
                        showTwoInterAd(
                            ads = it,
                            activity = activity ?: requireActivity(),
                            remoteConfigNormal = val_ad_inter_language_screen,
                            adIdNormal = id_inter_main_medium,
                            tagClass = "language_first",
                            isBackPress = true,
                            layout = _binding?.adsLay ?: return@let
                        ) {
                            if (PurchasePrefs(context).getBoolean("inApp") || !val_is_inapp_splash) {
                                findNavController().navigate(R.id.myMainMenuFragment)
                            } else {
                                findNavController().navigate(
                                    R.id.FragmentBuyScreen,
                                    bundleOf("isSplash" to true)
                                )
                            }
                        }
                    }*/
                }
            }
            loadNative()
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("adapter", "onBindViewHolder: ${e.message}")

        }
    }

    private fun loadNative() {
   /*     if (native_precashe_copunt_current >= native_precashe_counter) {
            admobNative.loadNativeAds(
                activity,
                _binding?.nativeExitAd!!,
                id_native_screen,
                if (val_ad_native_language_screen)
                    1 else 0,
                isAppPurchased = BillingUtil(activity ?: return).checkPurchased(activity ?: return),
                isInternetConnected = AdsManager.isNetworkAvailable(activity),
                nativeType = if (val_ad_native_language_screen_h) NativeType.LARGE else NativeType.BANNER,
                nativeCallBack = object : NativeCallBack {
                    override fun onAdFailedToLoad(adError: String) {
                        _binding?.adView?.visibility = View.GONE
                    }

                    override fun onAdLoaded() {
                        _binding?.adView?.visibility = View.GONE
                    }

                    override fun onAdImpression() {
                        _binding?.adView?.visibility = View.GONE
                    }
                }
            )
        } else {
            AdsManager.appAdsInit(activity ?: return).nativeAds().loadNativeAd(activity ?: return,
                val_ad_native_language_screen,
                id_native_screen,
                object : NativeListener {
                    override fun nativeAdLoaded(currentNativeAd: NativeAd?) {
                        if (isAdded && isVisible && !isDetached) {
                            _binding?.nativeExitAd?.visibility = View.VISIBLE
                            _binding?.adView?.visibility = View.GONE
                            val adView =
                                layoutInflater.inflate(
                                    if (val_ad_native_language_screen_h) R.layout.ad_unified_media else R.layout.ad_unified_privacy,
                                    null
                                ) as NativeAdView
                            AdsManager.appAdsInit(activity ?: return).nativeAds()
                                .nativeViewPolicy(
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
                            _binding?.nativeExitAd?.visibility = View.GONE
                            _binding?.adView?.visibility = View.GONE
                        }
                        super.nativeAdFailed(loadAdError)
                    }

                    override fun nativeAdValidate(string: String) {
                        if (isAdded && isVisible && !isDetached) {
                            _binding?.nativeExitAd?.visibility = View.GONE
                            _binding?.adView?.visibility = View.GONE
                        }
                        super.nativeAdValidate(string)
                    }
                })
        }*/
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.clear()
    }

    private fun initializeData() {
        val list1 = arrayListOf<LanguageModel>()
        list1.add(LanguageModel(getString(R.string.english), "en", R.drawable.uk, false))
        list1.add(LanguageModel(getString(R.string.Arabic), "ar", R.drawable.arabic, false))
        list1.add(
            LanguageModel(
                getString(R.string.bangladash),
                "bag",
                R.drawable.bangladash,
                false
            )
        )
        list1.add(LanguageModel(getString(R.string.brazil), "br", R.drawable.brazil, false))
        list1.add(LanguageModel(getString(R.string.canada), "ca", R.drawable.canada, false))
        list1.add(
            LanguageModel(
                getString(R.string.domican_republic),
                "rom",
                R.drawable.domican_republic,
                false
            )
        )
        list1.add(LanguageModel(getString(R.string.france), "fr", R.drawable.france, false))
        list1.add(LanguageModel(getString(R.string.german), "de", R.drawable.germany, false))
        list1.add(LanguageModel(getString(R.string.hindi), "hi", R.drawable.hindi, false))
        list1.add(LanguageModel(getString(R.string.italian), "it", R.drawable.italian, false))
        list1.add(LanguageModel(getString(R.string.japanese), "ja", R.drawable.japanese, false))
        list1.add(LanguageModel(getString(R.string.keynia), "ky", R.drawable.keynia, false))
        list1.add(LanguageModel(getString(R.string.korean), "ko", R.drawable.korean, false))
        list1.add(LanguageModel(getString(R.string.mexicon), "mk", R.drawable.mexicon, false))
        list1.add(LanguageModel(getString(R.string.netherland), "nl", R.drawable.netherland, false))
        list1.add(LanguageModel(getString(R.string.portuguese), "pt", R.drawable.portuguese, false))
        list1.add(LanguageModel(getString(R.string.russian), "ru", R.drawable.russian, false))
        list1.add(LanguageModel(getString(R.string.spanish), "es", R.drawable.spanish, false))
        list1.add(LanguageModel(getString(R.string.africa), "af", R.drawable.africa, false))
        list1.add(LanguageModel(getString(R.string.turkey), "tr", R.drawable.turkey, false))
        list1.add(LanguageModel(getString(R.string.urdu), "ur", R.drawable.urdu, false))
        list = list1
        Log.d("adapter", "onBindViewHolder: Mian${list?.size}")
    }

    private fun insertAds() {
        val adPosition = 5
        val repeatAdPosition = 10
        var currentPos = adPosition
        while (currentPos < list?.size!!) {
            list?.add(currentPos, LanguageModel("Ad", "", 0, false))
            currentPos += repeatAdPosition
        }
        languageGridAdapter =
            LanguageGridAdapter(list ?: return,  activity ?: return,
                clickItem = {
                    positionSelected = it.country_code
                    languageGridAdapter?.selectLanguage(positionSelected)
                    _binding?.forwardBtn?.visibility = View.VISIBLE
                }
            )
        if (!isLangScreen) {
            languageGridAdapter?.selectLanguage(positionSelected)
        }
        _binding?.conversationDetail?.adapter = languageGridAdapter
        Log.d("adapter_insertAds", "onBindViewHolder: ${list?.size}")
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

    private fun showCASInterstitial(isAdsShow: Boolean, function: (() -> Unit)) {
        if (interstitialAdManager == null) {
            function.invoke()
            return
        }
        interstitialAdManager?.showAd(isAdsShow) {
            function.invoke()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}