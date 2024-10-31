package livewallpaper.aod.screenlock.zipper.ui

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.core.app.ActivityCompat.recreate
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.clap.whistle.phonefinder.utilities.DbHelper
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import livewallpaper.aod.screenlock.zipper.MainActivity.Companion.background
import livewallpaper.aod.screenlock.zipper.R
import livewallpaper.aod.screenlock.zipper.adapter.AppLanguageAdapter
import livewallpaper.aod.screenlock.zipper.ads_manager.AdsManager
import livewallpaper.aod.screenlock.zipper.ads_manager.interfaces.NativeListener
import livewallpaper.aod.screenlock.zipper.databinding.FragmentLanguageBinding
import livewallpaper.aod.screenlock.zipper.model.LanguageModel
import livewallpaper.aod.screenlock.zipper.utilities.BaseFragment
import livewallpaper.aod.screenlock.zipper.utilities.IS_FIRST
import livewallpaper.aod.screenlock.zipper.utilities.LANG_CODE
import livewallpaper.aod.screenlock.zipper.utilities.LANG_SCREEN
import livewallpaper.aod.screenlock.zipper.utilities.clickWithThrottle
import livewallpaper.aod.screenlock.zipper.utilities.firebaseAnalytics
import livewallpaper.aod.screenlock.zipper.utilities.id_native_screen
import livewallpaper.aod.screenlock.zipper.utilities.languageData
import livewallpaper.aod.screenlock.zipper.utilities.setLocaleMain
import livewallpaper.aod.screenlock.zipper.utilities.setupBackPressedCallback
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_native_language_screen


class LanguageFragment : BaseFragment<FragmentLanguageBinding>(FragmentLanguageBinding::inflate) {

    private var positionSelected: String = "en"
    private var isLangScreen: Boolean = false
    private var adsManager: AdsManager? = null
    private var sharedPrefUtils: DbHelper? = null
    private var adapter: AppLanguageAdapter? = null
    var list: ArrayList<LanguageModel> = ArrayList()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adsManager = AdsManager.appAdsInit(activity ?: return)
        _binding?.mainbg?.setBackgroundResource(background)
        firebaseAnalytics("language_fragment_open", "language_fragment_open -->  Click")
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
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
                recreate(requireActivity())
            } else {
                firebaseAnalytics(
                    "language_fragment_forward_btn_from",
                    "language_fragment_forward_btn_from -->  Click"
                )
                sharedPrefUtils?.saveData(requireContext(), LANG_CODE, positionSelected) ?: "en"
                setLocaleMain(positionSelected)
                sharedPrefUtils?.saveData(requireContext(), IS_FIRST, true)
//                if (PurchasePrefs(context).getBoolean("inApp") || !val_is_inapp_splash) {
                findNavController().navigate(R.id.myMainMenuFragment, bundleOf("is_splash" to true))
//                } else {
//                    findNavController().navigate(R.id.FragmentBuyScreen, bundleOf("isSplash" to true))
//                }
            }
        }
        sharedPrefUtils = DbHelper(context ?: return)
        positionSelected = sharedPrefUtils?.getStringData(requireContext(), LANG_CODE, "en") ?: "en"
        adapter = AppLanguageAdapter(clickItem = {
            positionSelected = it.country_code
            _binding?.forwardBtn?.visibility = View.VISIBLE
        }, languageData = languageData())
        if(!isLangScreen){
            adapter?.selectLanguage(positionSelected)
        }

        _binding?.title?.text = getString(R.string.set_app_language)
        _binding?.conversationDetail?.adapter = adapter
        _binding?.backBtn?.clickWithThrottle {
            if (!isLangScreen) {
                firebaseAnalytics(
                    "language_fragment_back_press",
                    "language_fragment_back_press -->  Click"
                )
                findNavController().popBackStack()
            } else {
                findNavController().navigate(R.id.myMainMenuFragment, bundleOf("is_splash" to true))
            }
        }
        setupBackPressedCallback {
            if (!isLangScreen) {
                firebaseAnalytics(
                    "language_fragment_back_press",
                    "language_fragment_back_press -->  Click"
                )
                findNavController().popBackStack()
            } else {
                findNavController().navigate(R.id.myMainMenuFragment, bundleOf("is_splash" to true))
            }
        }
        loadNative()
        /*  if (!isLangScreen) {
              loadNative()
          }else{
              binding?.nativeExitAd?.visibility=View.GONE
              binding?.adView?.visibility=View.GONE
          }*/
    }

    private fun loadNative() {
        adsManager?.nativeAds()?.loadNativeAd(
            activity ?: return,
            true,
            id_native_screen,
            object : NativeListener {
                override fun nativeAdLoaded(currentNativeAd: NativeAd?) {
                    if (isAdded && isVisible && !isDetached) {
                        _binding?.nativeExitAd?.visibility = View.VISIBLE
                        _binding?.adView?.visibility = View.GONE
                        val adView = layoutInflater.inflate(
                            R.layout.ad_unified_privacy,
                            null
                        ) as NativeAdView
                        adsManager?.nativeAds()
                            ?.nativeViewPolicy(context ?: return, currentNativeAd ?: return, adView)
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
        /*        adsManager?.adsBanners()?.loadBanner(
                    activity = activity ?: return,
                    view = binding!!.adsView,
                    addConfig = val_banner_language_screen,
                    bannerId = id_adaptive_banner
                ) {

                }*/
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.clear()
    }
}