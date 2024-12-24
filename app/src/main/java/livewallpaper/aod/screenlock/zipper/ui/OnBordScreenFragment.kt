package livewallpaper.aod.screenlock.zipper.ui

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.clap.whistle.phonefinder.utilities.DbHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import livewallpaper.aod.screenlock.zipper.MyApplication.Companion.adManager
import livewallpaper.aod.screenlock.zipper.R
import livewallpaper.aod.screenlock.zipper.adapter.OnBordScreenAdapter
import livewallpaper.aod.screenlock.zipper.ads_cam.AdmobNative
import livewallpaper.aod.screenlock.zipper.ads_cam.InterstitialAdManager
import livewallpaper.aod.screenlock.zipper.ads_cam.NativeCallBack
import livewallpaper.aod.screenlock.zipper.ads_cam.NativeType
import livewallpaper.aod.screenlock.zipper.ads_cam.billing.BillingUtil
import livewallpaper.aod.screenlock.zipper.databinding.FragmentMainIntroBinding
import livewallpaper.aod.screenlock.zipper.utilities.BaseFragment
import livewallpaper.aod.screenlock.zipper.utilities.IS_INTRO
import livewallpaper.aod.screenlock.zipper.utilities.clickWithThrottle
import livewallpaper.aod.screenlock.zipper.utilities.firebaseAnalytics
import livewallpaper.aod.screenlock.zipper.utilities.id_native_screen
import livewallpaper.aod.screenlock.zipper.utilities.isNetworkAvailable
import livewallpaper.aod.screenlock.zipper.utilities.setupBackPressedCallback
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_native_intro_screen
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_native_setting_screen


class OnBordScreenFragment :
    BaseFragment<FragmentMainIntroBinding>(FragmentMainIntroBinding::inflate) {

    var currentpage = 0
    private var onBordScreenAdapter: OnBordScreenAdapter? = null
    private var sharedPrefUtils: DbHelper? = null
    private var interstitialAdManager: InterstitialAdManager? = null

    private var viewListener: OnPageChangeListener = object : OnPageChangeListener {
        override fun onPageScrolled(i: Int, v: Float, i1: Int) {
            currentpage = i

            _binding?.wormDotsIndicator?.attachTo(_binding?.mainSlideViewPager ?: return)
            if (currentpage == 3) {
                _binding?.skipApp?.visibility = View.INVISIBLE
                _binding?.nextApp?.text = getString(R.string.finish)
            } else {
                _binding?.nextApp?.text = getString(R.string.next)
            }
        }

        override fun onPageSelected(i: Int) {
            currentpage = i
        }

        override fun onPageScrollStateChanged(i: Int) {}
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseAnalytics("intro_fragment_open", "intro_fragment_open -->  Click")
        onBordScreenAdapter = OnBordScreenAdapter(requireContext())
        sharedPrefUtils = DbHelper(context ?: return)
//        ads = AdsManager.appAdsInit(activity ?: return)
        _binding?.run {
            mainSlideViewPager.adapter = onBordScreenAdapter
            mainSlideViewPager.addOnPageChangeListener(viewListener)
            skipApp.clickWithThrottle {
                firebaseAnalytics(
                    "intro_fragment_move_to_next",
                    "intro_fragment_move_to_next -->  Click"
                )
                sharedPrefUtils?.saveData(requireContext(), IS_INTRO, true)
                findNavController().navigate(R.id.FragmentPrivacyScreen)
//                findNavController().navigate(R.id.LanguageFragment, bundleOf(LANG_SCREEN to true))
            }
            nextApp.clickWithThrottle {
                if (currentpage == 3) {
                    firebaseAnalytics(
                        "intro_fragment_move_to_next",
                        "intro_fragment_move_to_next -->  Click"
                    )
                    sharedPrefUtils?.saveData(requireContext(), IS_INTRO, true)
//                    findNavController().navigate(
//                        R.id.LanguageFragment,
//                        bundleOf(LANG_SCREEN to true)
//                    )
                    findNavController().navigate(R.id.FragmentPrivacyScreen)
                } else {
                    mainSlideViewPager.setCurrentItem(getItem(+1), true)
                }
            }
        }
        loadNative()
        setupBackPressedCallback {
        }

    }

    private fun getItem(i: Int): Int {
        return _binding?.mainSlideViewPager?.currentItem!! + i
    }


    private fun loadNative() {
        AdmobNative().loadNativeAds(
            activity,
            _binding?.nativeExitAd!!,
            id_native_screen,
            if (val_ad_native_intro_screen)
                1 else 0,
            isAppPurchased = BillingUtil(activity ?: return).checkPurchased(activity ?: return),
            isInternetConnected = isNetworkAvailable(activity),
            nativeType = NativeType.LARGE,
            nativeCallBack = object : NativeCallBack {
                override fun onAdFailedToLoad(adError: String) {
                    _binding?.adView?.visibility = View.GONE
                    _binding?.skipApp?.visibility = View.VISIBLE
                    _binding?.nativeExitAd?.visibility = View.GONE
                }

                override fun onAdLoaded() {
                    _binding?.adView?.visibility = View.GONE
                    _binding?.skipApp?.visibility = View.VISIBLE
                }

                override fun onAdImpression() {
                    _binding?.adView?.visibility = View.GONE
                    _binding?.skipApp?.visibility = View.VISIBLE
                }
            }
        )
    }

    override fun onResume() {
        super.onResume()
        CoroutineScope(Dispatchers.Main).launch {
            delay(3000)
            _binding?.skipApp?.visibility = View.VISIBLE
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


    private fun loadCASInterstitial(isAdsShow: Boolean) {
        if   (!isAdsShow || !isNetworkAvailable(context)) {
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

}