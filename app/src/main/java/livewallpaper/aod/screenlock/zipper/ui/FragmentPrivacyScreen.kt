package livewallpaper.aod.screenlock.zipper.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.clap.whistle.phonefinder.utilities.DbHelper
import livewallpaper.aod.screenlock.zipper.R
import livewallpaper.aod.screenlock.zipper.ads_manager.billing.PurchasePrefs
import livewallpaper.aod.screenlock.zipper.databinding.FragmentPrivacyScreenBinding
import livewallpaper.aod.screenlock.zipper.utilities.BaseFragment
import livewallpaper.aod.screenlock.zipper.utilities.IS_FIRST
import livewallpaper.aod.screenlock.zipper.utilities.IS_INTRO
import livewallpaper.aod.screenlock.zipper.utilities.LANG_SCREEN
import livewallpaper.aod.screenlock.zipper.utilities.clickWithThrottle
import livewallpaper.aod.screenlock.zipper.utilities.firebaseAnalytics
import livewallpaper.aod.screenlock.zipper.utilities.setupBackPressedCallback
import livewallpaper.aod.screenlock.zipper.utilities.val_is_inapp_splash
import livewallpaper.aod.screenlock.zipper.utilities.val_on_bording_screen

class FragmentPrivacyScreen :
    BaseFragment<FragmentPrivacyScreenBinding>(FragmentPrivacyScreenBinding::inflate) {

    private var dbHelper: DbHelper? = null
    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        dbHelper = DbHelper(context ?: return)

        setupBackPressedCallback {
            findNavController().navigateUp()
        }

        _binding?.startBtn?.clickWithThrottle {
            if (dbHelper?.getBooleanData(
                    context ?: return@clickWithThrottle, IS_INTRO, false
                ) == false && val_on_bording_screen
            ) {
                firebaseAnalytics(
                    "loading_fragment_load_next_btn_intro",
                    "loading_fragment_load_next_btn_intro -->  Click"
                )
                findNavController().navigate(R.id.IntoScreenFragment)
            } else if (dbHelper?.getBooleanData(
                    context ?: return@clickWithThrottle, IS_FIRST, false
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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
    override fun onLowMemory() {
        super.onLowMemory()
        activity?.finish()
    }
}