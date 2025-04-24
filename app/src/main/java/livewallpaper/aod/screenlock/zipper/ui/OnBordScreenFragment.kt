package livewallpaper.aod.screenlock.zipper.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.clap.whistle.phonefinder.utilities.DbHelper
import com.gold.zipper.goldzipper.lockscreen.royalgold.gold.gold_ads_manager.AdmobNative
import com.gold.zipper.goldzipper.lockscreen.royalgold.gold.gold_ads_manager.AdsManager
import com.gold.zipper.goldzipper.lockscreen.royalgold.gold.gold_ads_manager.billing.BillingUtil
import com.gold.zipper.goldzipper.lockscreen.royalgold.gold.gold_ads_manager.interfaces.NativeCallBack
import com.gold.zipper.goldzipper.lockscreen.royalgold.gold.gold_ads_manager.showTwoInterAdStart
import com.google.android.gms.ads.nativead.NativeAdView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import livewallpaper.aod.screenlock.zipper.R
import livewallpaper.aod.screenlock.zipper.adapter.OnBordScreenAdapter
import livewallpaper.aod.screenlock.zipper.databinding.FragmentMainIntroBinding
import livewallpaper.aod.screenlock.zipper.utilities.BaseFragment
import livewallpaper.aod.screenlock.zipper.utilities.IS_FIRST
import livewallpaper.aod.screenlock.zipper.utilities.IS_INTRO
import livewallpaper.aod.screenlock.zipper.utilities.clickWithThrottle
import livewallpaper.aod.screenlock.zipper.utilities.firebaseAnalytics
import livewallpaper.aod.screenlock.zipper.utilities.id_inter_main_medium
import livewallpaper.aod.screenlock.zipper.utilities.id_native_screen
import livewallpaper.aod.screenlock.zipper.utilities.isNetworkAvailable
import livewallpaper.aod.screenlock.zipper.utilities.on_bord_native
import livewallpaper.aod.screenlock.zipper.utilities.sessionOpenLanguageNew
import livewallpaper.aod.screenlock.zipper.utilities.setupBackPressedCallback
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_inter_language_screen
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_inter_on_board
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_native_intro_screen
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_native_setting_screen
import livewallpaper.aod.screenlock.zipper.utilities.val_is_inapp_splash


class OnBordScreenFragment   :  BaseFragment<FragmentMainIntroBinding>(FragmentMainIntroBinding::inflate) {

    var currentpage = 0
    private var onBordScreenAdapter: OnBordScreenAdapter? = null
    private var sharedPrefUtils: DbHelper? = null
    private var adsManager: AdsManager? = null

    private val admobNative by lazy { AdmobNative() }

    private var viewListener: ViewPager.OnPageChangeListener = object :
        ViewPager.OnPageChangeListener {
        override fun onPageScrolled(i: Int, v: Float, i1: Int) {
            currentpage = i
            Log.d("pager", "onPageScrolled: $i")
            if (i == 1) {
                view?.findViewById<TextView>(R.id.skipApp)?.visibility = View.INVISIBLE
                view?.findViewById<TextView>(R.id.nextApp)?.visibility = View.VISIBLE
            } else {
                view?.findViewById<TextView>(R.id.skipApp)?.visibility = View.VISIBLE
                view?.findViewById<TextView>(R.id.nextApp)?.visibility = View.INVISIBLE
            }
            _binding?.wormDotsIndicator?.attachTo(_binding?.mainSlideViewPager ?: return)
        }

        override fun onPageSelected(i: Int) {
            currentpage = i

        }

        override fun onPageScrollStateChanged(i: Int) {
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {
            firebaseAnalytics("intro_fragment_open", "intro_fragment_open -->  Click")
            onBordScreenAdapter = OnBordScreenAdapter(requireContext())
            sharedPrefUtils = DbHelper(context ?: return)
            adsManager = AdsManager.appAdsInit(activity ?: return)
            _binding?.run {
                skipApp.clickWithThrottle {
                    firebaseAnalytics(
                        "intro_fragment_move_to_next",
                        "intro_fragment_move_to_next -->  Click"
                    )
                    sharedPrefUtils?.saveData(requireContext(), IS_INTRO, true)
                    adsManager?.let {
                        showTwoInterAdStart(
                            ads = it,
                            activity = activity ?: requireActivity(),
                            remoteConfigNormal = val_ad_inter_on_board,
                            adIdNormal = id_inter_main_medium,
                            tagClass = "on_bord",
                            isBackPress = true,
                            layout = _binding?.adsLay ?: return@let
                        ) {
                            when (sessionOpenLanguageNew) {
                                0 -> {
                                    firebaseAnalytics(
                                        "loading_fragment_load_next_btn_main",
                                        "loading_fragment_load_next_btn_main -->  Click"
                                    )
                                    showTwoInterAdStart(
                                        ads = AdsManager.appAdsInit(
                                            activity ?: return@showTwoInterAdStart
                                        ),
                                        activity = activity ?: requireActivity(),
                                        remoteConfigNormal = val_ad_inter_language_screen,
                                        adIdNormal = id_inter_main_medium,
                                        tagClass = "language_first",
                                        isBackPress = true,
                                        layout = _binding?.adsLay ?: return@showTwoInterAdStart
                                    ) {
                                        if (BillingUtil(
                                                activity ?: return@showTwoInterAdStart
                                            ).checkPurchased(
                                                context ?: return@showTwoInterAdStart
                                            ) || !val_is_inapp_splash
                                        ) {
                                            findNavController().navigate(
                                                R.id.myMainMenuFragment,
                                                bundleOf("isSplash" to true)
                                            )
                                        } else {
                                            findNavController().navigate(
                                                R.id.FragmentBuyScreen,
                                                bundleOf("isSplash" to true)
                                            )
                                        }
                                    }

                                }

                                1 -> {
                                    if (!DbHelper(
                                            context ?: return@showTwoInterAdStart
                                        ).getBooleanData(
                                            context ?: return@showTwoInterAdStart, IS_FIRST, false
                                        )
                                    ) {
                                        showTwoInterAdStart(
                                            ads = AdsManager.appAdsInit(
                                                activity ?: return@showTwoInterAdStart
                                            ),
                                            activity = activity ?: requireActivity(),
                                            remoteConfigNormal = val_ad_inter_language_screen,
                                            adIdNormal = id_inter_main_medium,
                                            tagClass = "language_first",
                                            isBackPress = true,
                                            layout = _binding?.adsLay ?: return@showTwoInterAdStart
                                        ) {
                                            findNavController().navigate(
                                                R.id.LanguageFragment,
                                                bundleOf("isSplash" to true)
                                            )
                                        }
                                    } else {
                                        firebaseAnalytics(
                                            "loading_fragment_load_next_btn_main",
                                            "loading_fragment_load_next_btn_main -->  Click"
                                        )

                                    }
                                }

                                2 -> {
                                    firebaseAnalytics(
                                        "loading_fragment_load_next_btn_language",
                                        "loading_fragment_load_next_btn_language -->  Click"
                                    )
                                    showTwoInterAdStart(
                                        ads = AdsManager.appAdsInit(
                                            activity ?: return@showTwoInterAdStart
                                        ),
                                        activity = activity ?: requireActivity(),
                                        remoteConfigNormal = val_ad_inter_language_screen,
                                        adIdNormal = id_inter_main_medium,
                                        tagClass = "language_first",
                                        isBackPress = true,
                                        layout = _binding?.adsLay ?: return@showTwoInterAdStart
                                    ) {
                                        findNavController().navigate(
                                            R.id.LanguageFragment,
                                            bundleOf("isSplash" to true)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                nextApp.clickWithThrottle {
                    if(currentpage==3){
                        firebaseAnalytics(
                            "intro_fragment_move_to_next",
                            "intro_fragment_move_to_next -->  Click"
                        )
                        sharedPrefUtils?.saveData(requireContext(), IS_INTRO, true)
                        AdsManager.appAdsInit(activity?:return@clickWithThrottle)?.let {
                            showTwoInterAdStart(
                                ads = it,
                                activity = activity ?: requireActivity(),
                                remoteConfigNormal = val_ad_inter_on_board,
                                adIdNormal = id_inter_main_medium,
                                tagClass = "on_bord",
                                isBackPress = true,
                                layout = adsLay
                            ) {
                                when (sessionOpenLanguageNew) {
                                    0 -> {
                                        firebaseAnalytics(
                                            "loading_fragment_load_next_btn_main",
                                            "loading_fragment_load_next_btn_main -->  Click"
                                        )
                                        if (BillingUtil(
                                                activity ?: return@showTwoInterAdStart
                                            ).checkPurchased(
                                                context ?: return@showTwoInterAdStart
                                            ) || !val_is_inapp_splash
                                        ) {
                                            findNavController().navigate(
                                                R.id.myMainMenuFragment,
                                                bundleOf("isSplash" to true)
                                            )
                                        } else {
                                            findNavController().navigate(
                                                R.id.FragmentBuyScreen,
                                                bundleOf("isSplash" to true)
                                            )
                                        }
                                    }

                                    1 -> {
                                        if (sharedPrefUtils?.getBooleanData(
                                                context ?: return@showTwoInterAdStart,
                                                IS_FIRST,
                                                false
                                            ) == false
                                        ) {
                                            findNavController().navigate(
                                                R.id.LanguageFragment,
                                                bundleOf("isSplash" to true)
                                            )
                                        } else {
                                            firebaseAnalytics(
                                                "loading_fragment_load_next_btn_main",
                                                "loading_fragment_load_next_btn_main -->  Click"
                                            )
                                            if (BillingUtil(
                                                    activity ?: return@showTwoInterAdStart
                                                ).checkPurchased(
                                                    context ?: return@showTwoInterAdStart
                                                ) || !val_is_inapp_splash
                                            ) {
                                                findNavController().navigate(
                                                    R.id.myMainMenuFragment,
                                                    bundleOf("isSplash" to true)
                                                )
                                            } else {
                                                findNavController().navigate(
                                                    R.id.FragmentBuyScreen,
                                                    bundleOf("isSplash" to true)
                                                )
                                            }
                                        }
                                    }

                                    2 -> {
                                        firebaseAnalytics(
                                            "loading_fragment_load_next_btn_language",
                                            "loading_fragment_load_next_btn_language -->  Click"
                                        )
                                        findNavController().navigate(
                                            R.id.LanguageFragment, bundleOf("isSplash" to true)
                                        )
                                    }
                                }
                            }
                        }
                    }else{
                        mainSlideViewPager.setCurrentItem(getItem(1), true)
                    }


                }
            }
            _binding?.mainSlideViewPager?.adapter = onBordScreenAdapter
            _binding?.mainSlideViewPager?.addOnPageChangeListener(viewListener)
            loadNative()
            setupBackPressedCallback {
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun getItem(i: Int): Int {
        return 1
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.clear()
        _binding = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun loadNative() {
        admobNative.loadNativeAds(
            activity,
            _binding?.nativeExitAd!!,
            id_native_screen,
            if (val_ad_native_intro_screen)
                1 else 0,
            isAppPurchased = BillingUtil(activity ?: return).checkPurchased(activity ?: return),
            isInternetConnected = AdsManager.isNetworkAvailable(activity),
            nativeType = on_bord_native,
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

    override fun onResume() {
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}