package livewallpaper.aod.screenlock.zipper.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.clap.whistle.phonefinder.utilities.DbHelper
import com.gold.zipper.goldzipper.lockscreen.royalgold.gold.gold_ads_manager.AdmobNative
import com.gold.zipper.goldzipper.lockscreen.royalgold.gold.gold_ads_manager.AdsManager
import com.gold.zipper.goldzipper.lockscreen.royalgold.gold.gold_ads_manager.billing.BillingUtil
import com.gold.zipper.goldzipper.lockscreen.royalgold.gold.gold_ads_manager.interfaces.NativeCallBack
import com.gold.zipper.goldzipper.lockscreen.royalgold.gold.gold_ads_manager.showTwoInterAdStart
import com.google.android.gms.ads.nativead.NativeAdView
import livewallpaper.aod.screenlock.zipper.R
import livewallpaper.aod.screenlock.zipper.adapter.LanguageGridAdapter
import livewallpaper.aod.screenlock.zipper.adapter.LanguageGridAdapter.AdViewHolder
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
import livewallpaper.aod.screenlock.zipper.utilities.isNetworkAvailable
import livewallpaper.aod.screenlock.zipper.utilities.language_bottom
import livewallpaper.aod.screenlock.zipper.utilities.setLocaleMain
import livewallpaper.aod.screenlock.zipper.utilities.setupBackPressedCallback
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_inter_language_screen
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_native_language_screen
import livewallpaper.aod.screenlock.zipper.utilities.val_is_inapp_splash


class LanguageFragment : BaseFragment<FragmentLanguageBinding>(FragmentLanguageBinding::inflate) {
    private var positionSelected: String = "en"
    private var isLangScreen: Boolean = false
    private var isValue: Int = 0
    private var sharedPrefUtils: DbHelper? = null
    private var languageGridAdapter: LanguageGridAdapter? = null
    var list: ArrayList<LanguageModel>? = null
    private var adsManager: AdsManager? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {
            firebaseAnalytics("language_fragment_open", "language_fragment_open -->  Click")
            requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
            sharedPrefUtils = DbHelper(context ?: return)
            positionSelected =
                sharedPrefUtils?.getStringData(requireContext(), LANG_CODE, "en") ?: "en"
            initializeData()
            adsManager = AdsManager.appAdsInit(activity ?: return)
            arguments?.let {
                isLangScreen = it.getBoolean("isSplash")
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
                    adsManager?.let {
                        showTwoInterAdStart(
                            ads = it,
                            activity = activity ?: requireActivity(),
                            remoteConfigNormal = val_ad_inter_language_screen,
                            adIdNormal = id_inter_main_medium,
                            tagClass = "language_first",
                            isBackPress = true,
                            layout = _binding?.adsLay ?: return@let
                        ) {
                            if (BillingUtil(activity?:return@showTwoInterAdStart ).checkPurchased(context?:return@showTwoInterAdStart) || !val_is_inapp_splash) {
                                findNavController().navigate(R.id.myMainMenuFragment,
                                    bundleOf("isSplash" to true))
                            } else {
                                findNavController().navigate(
                                    R.id.FragmentBuyScreen,
                                    bundleOf("isSplash" to true)
                                )
                            }
                        }
                    }
                }
            }

            languageGridAdapter =
                LanguageGridAdapter(list ?: return, adsManager ?: return, activity ?: return,
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
                    adsManager?.let {
                        showTwoInterAdStart(
                            ads = it,
                            activity = activity ?: requireActivity(),
                            remoteConfigNormal = val_ad_inter_language_screen,
                            adIdNormal = id_inter_main_medium,
                            tagClass = "language_first",
                            isBackPress = true,
                            layout = _binding?.adsLay ?: return@let
                        ) {
                            if (BillingUtil(activity?:return@showTwoInterAdStart ).checkPurchased(context?:return@showTwoInterAdStart) || !val_is_inapp_splash) {
                                findNavController().navigate(R.id.myMainMenuFragment,
                                    bundleOf("isSplash" to true))
                            } else {
                                findNavController().navigate(
                                    R.id.FragmentBuyScreen,
                                    bundleOf("isSplash" to true)
                                )
                            }
                        }
                    }
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
                    adsManager?.let {
                        showTwoInterAdStart(
                            ads = it,
                            activity = activity ?: requireActivity(),
                            remoteConfigNormal = val_ad_inter_language_screen,
                            adIdNormal = id_inter_main_medium,
                            tagClass = "language_first",
                            isBackPress = true,
                            layout = _binding?.adsLay ?: return@let
                        ) {
                            if (BillingUtil(activity?:return@showTwoInterAdStart ).checkPurchased(context?:return@showTwoInterAdStart) || !val_is_inapp_splash) {
                                findNavController().navigate(R.id.myMainMenuFragment,
                                    bundleOf("isSplash" to true))
                            } else {
                                findNavController().navigate(
                                    R.id.FragmentBuyScreen,
                                    bundleOf("isSplash" to true)
                                )
                            }
                        }
                    }
                }
            }
            loadNative()
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("adapter", "onBindViewHolder: ${e.message}")

        }
    }

    private val admobNative by lazy { AdmobNative() }

    private fun loadNative() {
        admobNative.loadNativeAds(
            activity,
            _binding?.nativeExitAd!!,
            id_native_screen,
            if (val_ad_native_language_screen)
                1 else 0,
            isAppPurchased = BillingUtil(activity ?: return).checkPurchased(activity ?: return),
            isInternetConnected = AdsManager.isNetworkAvailable(activity),
            nativeType = language_bottom,
            nativeCallBack = object : NativeCallBack {
                override fun onAdFailedToLoad(adError: String) {
                    _binding?.nativeExitAd?.visibility = View.GONE
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
            LanguageGridAdapter(list ?: return, adsManager ?: return, activity ?: return,
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

}
