package livewallpaper.aod.screenlock.zipper.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.clap.whistle.phonefinder.utilities.DbHelper
import livewallpaper.aod.screenlock.zipper.R
import livewallpaper.aod.screenlock.zipper.ads_manager.AdsManager
import livewallpaper.aod.screenlock.zipper.ads_manager.billing.PurchasePrefs
import livewallpaper.aod.screenlock.zipper.ads_manager.loadTwoInterAds
import livewallpaper.aod.screenlock.zipper.databinding.FragmentPrivacyScreenBinding
import livewallpaper.aod.screenlock.zipper.utilities.BaseFragment
import livewallpaper.aod.screenlock.zipper.utilities.IS_FIRST
import livewallpaper.aod.screenlock.zipper.utilities.IS_INTRO
import livewallpaper.aod.screenlock.zipper.utilities.LANG_SCREEN
import livewallpaper.aod.screenlock.zipper.utilities.clickWithThrottle
import livewallpaper.aod.screenlock.zipper.utilities.firebaseAnalytics
import livewallpaper.aod.screenlock.zipper.utilities.id_inter_main_medium
import livewallpaper.aod.screenlock.zipper.utilities.setupBackPressedCallback
import livewallpaper.aod.screenlock.zipper.utilities.val_is_inapp_splash
import livewallpaper.aod.screenlock.zipper.utilities.val_on_bording_screen

class FragmentPrivacyScreen :
    BaseFragment<FragmentPrivacyScreenBinding>(FragmentPrivacyScreenBinding::inflate) {

    private var dbHelper: DbHelper? = null
    private var adsManager: AdsManager? = null

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dbHelper = DbHelper(context ?: return)
        adsManager = AdsManager.appAdsInit(activity ?: return)

        loadTwoInterAds(
            ads = adsManager ?: return,
            activity = activity ?: return,
            remoteConfigNormal = true,
            adIdNormal = id_inter_main_medium,
            tagClass = "main_app_fragment_pre_cashe"
        )

        setupBackPressedCallback {
            findNavController().navigate(R.id.LanguageFragment, bundleOf(LANG_SCREEN to true))
        }

        _binding?.startBtn?.clickWithThrottle {
            findNavController().navigate(R.id.LanguageFragment, bundleOf(LANG_SCREEN to true))
        }

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