package livewallpaper.aod.screenlock.zipper.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.clap.whistle.phonefinder.utilities.DbHelper
import livewallpaper.aod.screenlock.zipper.R
import livewallpaper.aod.screenlock.zipper.databinding.FragmentPrivacyScreenBinding
import livewallpaper.aod.screenlock.zipper.utilities.BaseFragment
import livewallpaper.aod.screenlock.zipper.utilities.LANG_SCREEN
import livewallpaper.aod.screenlock.zipper.utilities.clickWithThrottle
import livewallpaper.aod.screenlock.zipper.utilities.setupBackPressedCallback

class FragmentPrivacyScreen :
    BaseFragment<FragmentPrivacyScreenBinding>(FragmentPrivacyScreenBinding::inflate) {

    private var dbHelper: DbHelper? = null

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dbHelper = DbHelper(context ?: return)

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

}