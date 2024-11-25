package livewallpaper.aod.screenlock.zipper.ui

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.clap.whistle.phonefinder.utilities.DbHelper
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import livewallpaper.aod.screenlock.zipper.MainActivity.Companion.background
import livewallpaper.aod.screenlock.zipper.R
import livewallpaper.aod.screenlock.zipper.adapter.OnBordScreenAdapter
import livewallpaper.aod.screenlock.zipper.ads_manager.AdsManager
import livewallpaper.aod.screenlock.zipper.ads_manager.interfaces.NativeListener
import livewallpaper.aod.screenlock.zipper.databinding.FragmentMainIntroBinding
import livewallpaper.aod.screenlock.zipper.utilities.BaseFragment
import livewallpaper.aod.screenlock.zipper.utilities.IS_INTRO
import livewallpaper.aod.screenlock.zipper.utilities.LANG_SCREEN
import livewallpaper.aod.screenlock.zipper.utilities.clickWithThrottle
import livewallpaper.aod.screenlock.zipper.utilities.firebaseAnalytics
import livewallpaper.aod.screenlock.zipper.utilities.id_native_screen
import livewallpaper.aod.screenlock.zipper.utilities.setupBackPressedCallback
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_native_intro_screen


class OnBordScreenFragment :
    BaseFragment<FragmentMainIntroBinding>(FragmentMainIntroBinding::inflate) {

    var currentpage = 0
    private var onBordScreenAdapter: OnBordScreenAdapter? = null
    private var sharedPrefUtils: DbHelper? = null
    private var ads: AdsManager? = null

    private var viewListener: OnPageChangeListener = object : OnPageChangeListener {
        override fun onPageScrolled(i: Int, v: Float, i1: Int) {
            currentpage = i

            _binding?.wormDotsIndicator?.attachTo(_binding?.mainSlideViewPager ?: return)
            if (currentpage == 3) {
                _binding?.skipApp?.visibility = View.INVISIBLE
                _binding?.nextApp?.text = getString(R.string.finish)
            } else {
                _binding?.skipApp?.visibility = View.VISIBLE
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
        _binding?.mainbg?.setBackgroundResource(background)
        sharedPrefUtils = DbHelper(context ?: return)
        ads = AdsManager.appAdsInit(activity ?: return)
        _binding?.run {
            mainSlideViewPager.adapter = onBordScreenAdapter
            mainSlideViewPager.addOnPageChangeListener(viewListener)
            skipApp.clickWithThrottle {
                firebaseAnalytics(
                    "intro_fragment_move_to_next",
                    "intro_fragment_move_to_next -->  Click"
                )
                sharedPrefUtils?.saveData(requireContext(), IS_INTRO, true)
                findNavController().navigate(R.id.LanguageFragment, bundleOf(LANG_SCREEN to true))
            }
            nextApp.clickWithThrottle {
                if (currentpage == 3) {
                    firebaseAnalytics(
                        "intro_fragment_move_to_next",
                        "intro_fragment_move_to_next -->  Click"
                    )
                    sharedPrefUtils?.saveData(requireContext(), IS_INTRO, true)
                    findNavController().navigate(
                        R.id.LanguageFragment,
                        bundleOf(LANG_SCREEN to true)
                    )
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
        ads?.nativeAds()?.loadNativeAd(
            activity ?: return,
            val_ad_native_intro_screen,
            id_native_screen,
            object : NativeListener {
                override fun nativeAdLoaded(currentNativeAd: NativeAd?) {
                    if (isAdded && isVisible && !isDetached) {
                        _binding?.nativeExitAd?.visibility = View.VISIBLE
                        _binding?.nextApp?.visibility = View.VISIBLE
                        _binding?.adView?.visibility = View.GONE
                        val adView =
                            layoutInflater.inflate(
                                R.layout.ad_unified_privacy,
                                null
                            ) as NativeAdView
                        ads?.nativeAds()
                            ?.nativeViewPolicy(context ?: return, currentNativeAd ?: return, adView)
                        _binding?.nativeExitAd?.removeAllViews()
                        _binding?.nativeExitAd?.addView(adView)
                    }
                    super.nativeAdLoaded(currentNativeAd)
                }

                override fun nativeAdFailed(loadAdError: LoadAdError) {
                    if (isAdded && isVisible && !isDetached) {
                        _binding?.nextApp?.visibility = View.VISIBLE
                        _binding?.nativeExitAd?.visibility = View.INVISIBLE
                        _binding?.adView?.visibility = View.INVISIBLE
                    }
                    super.nativeAdFailed(loadAdError)
                }

                override fun nativeAdValidate(string: String) {
                    if (isAdded && isVisible && !isDetached) {
                        _binding?.nextApp?.visibility = View.VISIBLE
                        _binding?.nativeExitAd?.visibility = View.INVISIBLE
                        _binding?.adView?.visibility = View.INVISIBLE
                    }
                    super.nativeAdValidate(string)
                }
            })

    }

    override fun onResume() {
        super.onResume()
        CoroutineScope(Dispatchers.Main).launch {
            delay(3000)
            _binding?.nextApp?.visibility = View.VISIBLE
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
}