package livewallpaper.aod.screenlock.reward

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.constraintlayout.utils.widget.ImageFilterView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.clap.whistle.phonefinder.utilities.DbHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import livewallpaper.aod.screenlock.zipper.R
import livewallpaper.aod.screenlock.zipper.ads_manager.AdsManager
import livewallpaper.aod.screenlock.zipper.ads_manager.AdsManager.isNetworkAvailable
import livewallpaper.aod.screenlock.zipper.ads_manager.showTwoInterAd
import livewallpaper.aod.screenlock.zipper.databinding.FragmentWallpaperBinding
import livewallpaper.aod.screenlock.zipper.utilities.clickWithThrottle
import livewallpaper.aod.screenlock.zipper.utilities.firebaseAnalytics
import livewallpaper.aod.screenlock.zipper.utilities.getRemainingTimeUntilMidnight
import livewallpaper.aod.screenlock.zipper.utilities.getRewardTitle
import livewallpaper.aod.screenlock.zipper.utilities.id_adaptive_banner
import livewallpaper.aod.screenlock.zipper.utilities.id_inter_main_medium
import livewallpaper.aod.screenlock.zipper.utilities.setupBackPressedCallback
import livewallpaper.aod.screenlock.zipper.utilities.showToast
import livewallpaper.aod.screenlock.zipper.utilities.startCountdownTimer
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_inter_reward_screen
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_inter_setting_screen_front
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_native_list_data_screen
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_native_reward_screen

class WallpaperFragment : Fragment() {

    private var adsmanager: AdsManager ? = null
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
       adsmanager =AdsManager.appAdsInit(activity ?: requireActivity())
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
        if (sharedPrefUtils?.getBooleanData(activity ?: return, "IS_UNLOCK", false) == false) {
            loadCategories1()
            return
        }
        val prefs = RewardPreferences(context)
        val currentDay = prefs.getCurrentUnlockDay(context)
        if (currentDay <= RewardConstants.TOTAL_DAYS) {
//            prefs.setCurrentDay(currentDay + 1)
//            prefs.setLastOpenDate(System.currentTimeMillis())
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
                showTwoInterAd(
                    ads = adsmanager?:return@CategoryAdapter,
                    activity = activity ?: requireActivity(),
                    remoteConfigNormal = val_ad_inter_reward_screen,
                    adIdNormal = id_inter_main_medium,
                    tagClass = "fragment_reward",
                    isBackPress = false,
                    layout = _binding?.adsLay ?: return@CategoryAdapter
                ) {
                    findNavController().navigate(
                        R.id.ImageListFragment,
                        bundleOf("title" to Tilte_)
                    )
                }
//                showToast(context?:requireContext(),"Un Lock - $Tilte_")
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
                locked = false
            )
        }

        categories.clear()
        categories.addAll(allCategories)
        categoryAdapter.updateCategories(categories)
    }

    private fun loadBanner() {
        AdsManager.appAdsInit(activity ?: return).adsBanners().loadBanner(
            activity = activity ?: return,
            view = _binding?.nativeExitAd!!,
            addConfig = val_ad_native_reward_screen,
            bannerId = id_adaptive_banner
        ) {
            _binding?.adView?.visibility = View.GONE
        }
    }
}
