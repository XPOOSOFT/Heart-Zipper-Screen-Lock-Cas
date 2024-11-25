package livewallpaper.aod.screenlock.reward

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.clap.whistle.phonefinder.utilities.DbHelper
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import livewallpaper.aod.screenlock.zipper.R
import livewallpaper.aod.screenlock.zipper.ads_manager.AdsManager
import livewallpaper.aod.screenlock.zipper.ads_manager.AdsManager.isNetworkAvailable
import livewallpaper.aod.screenlock.zipper.ads_manager.interfaces.NativeListener
import livewallpaper.aod.screenlock.zipper.databinding.FragmentWallpaperBinding
import livewallpaper.aod.screenlock.zipper.utilities.clickWithThrottle
import livewallpaper.aod.screenlock.zipper.utilities.firebaseAnalytics
import livewallpaper.aod.screenlock.zipper.utilities.getRemainingTimeUntilMidnight
import livewallpaper.aod.screenlock.zipper.utilities.getRewardTitle
import livewallpaper.aod.screenlock.zipper.utilities.id_adaptive_banner
import livewallpaper.aod.screenlock.zipper.utilities.id_native_screen
import livewallpaper.aod.screenlock.zipper.utilities.setupBackPressedCallback
import livewallpaper.aod.screenlock.zipper.utilities.showToast
import livewallpaper.aod.screenlock.zipper.utilities.startCountdownTimer
import livewallpaper.aod.screenlock.zipper.utilities.type_ad_native_enable_screen
import livewallpaper.aod.screenlock.zipper.utilities.type_ad_native_reward_screen
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_native_enable_screen
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_native_reward_screen

class WallpaperFragment : Fragment() {

    private var adsmanager: AdsManager? = null
    private lateinit var categoryAdapter: CategoryAdapter
    private val categories = mutableListOf<WallpaperCategory>()
    private var currentUnlockDay = 0
    private var sharedPrefUtils: DbHelper? = null
    private var _binding: FragmentWallpaperBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentWallpaperBinding.inflate(layoutInflater)
        Log.d("calling", "onCreateView: load main fragment")
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAnalytics("reward_fragment_open", "reward_fragment_open -->  Click")
        adsmanager = AdsManager.appAdsInit(activity ?: requireActivity())
        sharedPrefUtils = DbHelper(requireContext())
        unlockNextCategory(requireContext(), view)
        setupBackPressedCallback {
            findNavController().navigateUp()
        }
        CoroutineScope(Dispatchers.Main).launch {
            startCountdownTimer(getRemainingTimeUntilMidnight(), view.findViewById(R.id.timeText))
        }
        _binding?.titleBack?.clickWithThrottle {
            findNavController().navigateUp()
        }
        loadBanner()
    }

    private fun unlockNextCategory(context: Context, view: View) {
        setupRecyclerView(view)
//        if (sharedPrefUtils?.getBooleanData(activity ?: return, "IS_UNLOCK", false) == false) {
//            loadCategories1()
//            return
//        }
        val prefs = RewardPreferences(context)
        val currentDay = prefs.getCurrentUnlockDay(context)
        if (currentDay <= RewardConstants.TOTAL_DAYS) {
            loadCategories()
        } else {
            loadCategories1()
        }

    }

    private fun setupRecyclerView(view: View) {
        categoryAdapter = CategoryAdapter(categories) { _Lock, Tilte_ ->
            if (!isNetworkAvailable(activity)) {
                showToast(context ?: requireContext(), getString(R.string.no_internet))
                return@CategoryAdapter
            }
            if (_Lock) {
                showToast(context ?: requireContext(), "Lock - $Tilte_")
            } else {
                findNavController().navigate(
                    R.id.ImageListFragment,
                    bundleOf("title" to Tilte_)
                )
            }
        }
        view.findViewById<RecyclerView>(R.id.recyclerView).adapter = categoryAdapter
    }

    private fun loadCategories() {
        currentUnlockDay =
            RewardPreferences(context ?: return).getCurrentUnlockDay(requireContext())

        // Assume 8 categories for simplicity
        val allCategories = (1..8).map {
            WallpaperCategory(
                name = getRewardTitle(context ?: return)[it - 1],
                nameDay = "Day $it",
                locked = it > currentUnlockDay
            )
        }

        categories.clear()
        categories.addAll(allCategories)
        categoryAdapter.updateCategories(categories)
    }

    private fun loadCategories1() {
        // Assume 8 categories for simplicity
        val allCategories = (1..8).map {
            WallpaperCategory(
                name = getRewardTitle(context ?: return)[it - 1],
                nameDay = "Day $it",
                locked = true
            )
        }

        categories.clear()
        categories.addAll(allCategories)
        categoryAdapter.updateCategories(categories)
    }

    private fun loadBanner() {

        when (type_ad_native_reward_screen) {
            0 -> {
                _binding?.nativeExitAd?.visibility = View.GONE
                _binding?.adView?.visibility=View.GONE
            }

            1 -> {
                AdsManager.appAdsInit(activity ?: return).adsBanners().loadBanner(
                    activity = activity ?: return,
                    view = _binding!!.nativeExitAd,
                    addConfig = val_ad_native_reward_screen,
                    bannerId = id_adaptive_banner
                ) {
                    _binding!!.adView?.visibility=View.GONE
                }
            }

            2 -> {
                AdsManager.appAdsInit(activity ?: return).nativeAds().loadNativeAdExit(
                    activity ?: return,
                    val_ad_native_reward_screen,
                    id_native_screen,
                    object : NativeListener {
                        override fun nativeAdLoaded(currentNativeAd: NativeAd?) {
                            if (isAdded && isVisible && !isDetached) {
                                _binding?.nativeExitAd?.visibility = View.VISIBLE
                                _binding?.adView?.visibility = View.GONE
                                val adView =layoutInflater.inflate(R.layout.ad_unified_media, null) as NativeAdView
                                AdsManager.appAdsInit(activity ?: return).nativeAds()
                                    .nativeViewMedia(context?:return,currentNativeAd ?: return, adView)
                                _binding?.nativeExitAd?.removeAllViews()
                                _binding?.nativeExitAd?.addView(adView)
                            }
                            super.nativeAdLoaded(currentNativeAd)
                        }

                        override fun nativeAdFailed(loadAdError: LoadAdError) {
                            if (isAdded && isVisible && !isDetached) {
                                _binding?.nativeExitAd?.visibility = View.INVISIBLE
                                _binding?.adView?.visibility = View.INVISIBLE
                            }
                            super.nativeAdFailed(loadAdError)
                        }

                        override fun nativeAdValidate(string: String) {
                            if (isAdded && isVisible && !isDetached) {
                                _binding?.nativeExitAd?.visibility = View.INVISIBLE
                                _binding?.adView?.visibility = View.INVISIBLE
                            }
                            super.nativeAdValidate(string)
                        }
                    })
            }
        }
    }
}
