package livewallpaper.aod.screenlock.zipper.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.clap.whistle.phonefinder.utilities.DbHelper
import com.cleversolutions.ads.AdSize
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import livewallpaper.aod.screenlock.zipper.MyApplication.Companion.TAG
import livewallpaper.aod.screenlock.zipper.R
import livewallpaper.aod.screenlock.zipper.ads_cam.loadNativeBanner
import livewallpaper.aod.screenlock.zipper.ads_manager.AdmobNative
import livewallpaper.aod.screenlock.zipper.ads_manager.AdsManager
import livewallpaper.aod.screenlock.zipper.ads_manager.billing.BillingUtil
import livewallpaper.aod.screenlock.zipper.ads_manager.interfaces.NativeCallBack
import livewallpaper.aod.screenlock.zipper.ads_manager.interfaces.NativeListener
import livewallpaper.aod.screenlock.zipper.ads_manager.interfaces.NativeType
import livewallpaper.aod.screenlock.zipper.ads_manager.loadTwoInterAds
import livewallpaper.aod.screenlock.zipper.ads_manager.showTwoInterAd
import livewallpaper.aod.screenlock.zipper.databinding.FragmentMainMenuActivityBinding
import livewallpaper.aod.screenlock.zipper.databinding.SecurityQuestionFragmentBinding
import livewallpaper.aod.screenlock.zipper.utilities.PurchaseScreen
import livewallpaper.aod.screenlock.zipper.utilities.SECURITY_ANS
import livewallpaper.aod.screenlock.zipper.utilities.SECURITY_QUESTION
import livewallpaper.aod.screenlock.zipper.utilities.containsLeadingTrailingSpaces
import livewallpaper.aod.screenlock.zipper.utilities.containsMultipleSpaces
import livewallpaper.aod.screenlock.zipper.utilities.id_adaptive_banner
import livewallpaper.aod.screenlock.zipper.utilities.id_inter_main_medium
import livewallpaper.aod.screenlock.zipper.utilities.id_native_screen
import livewallpaper.aod.screenlock.zipper.utilities.native_precashe_copunt_current
import livewallpaper.aod.screenlock.zipper.utilities.native_precashe_counter
import livewallpaper.aod.screenlock.zipper.utilities.setupBackPressedCallback
import livewallpaper.aod.screenlock.zipper.utilities.showToast
import livewallpaper.aod.screenlock.zipper.utilities.type_ad_native_security_screen
import livewallpaper.aod.screenlock.zipper.utilities.type_ad_native_setting_screen
import livewallpaper.aod.screenlock.zipper.utilities.type_ad_native_sound_screen
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_inter_security_screen_back
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_inter_security_screen_front
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_native_list_data_screen
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_native_security_screen
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_native_setting_screen
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_native_sound_screen
import livewallpaper.aod.screenlock.zipper.utilities.val_inapp_frequency
import livewallpaper.aod.screenlock.zipper.utilities.val_inter_back_press

class SecurityQuestionFragment : Fragment() {

    private var sharedPrefUtils: DbHelper? = null
    private var questionText: String? = ""
    private var adsManager: AdsManager? = null
    private var _binding: SecurityQuestionFragmentBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = SecurityQuestionFragmentBinding.inflate(layoutInflater)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {
            if(++PurchaseScreen == val_inapp_frequency){
                PurchaseScreen =0
                findNavController().navigate(R.id.FragmentBuyScreen, bundleOf("isSplash" to false))
                return
            }
            if(isVisible && isAdded && !isDetached){
            adsManager = AdsManager.appAdsInit(activity?:requireActivity())
            _binding?.topLay?.title?.text = getString(R.string.security_question)
            sharedPrefUtils = DbHelper(context?:return)
            _binding?.powerSpinnerView?.setOnSpinnerItemSelectedListener<String> { oldIndex, oldItem, newIndex, newText ->
                showToast(context?:return@setOnSpinnerItemSelectedListener, "$newIndex selected!")
                questionText = newText
            }
            _binding?.addQuestion?.setOnClickListener {
                if (questionText.equals("") ) {
                    questionText = getString(R.string.what_is_your_favourite_color)
                }

                if (_binding?.editTextText?.text?.isNotEmpty() == true && !containsMultipleSpaces(_binding?.editTextText?.text?.toString()?:return@setOnClickListener) && !containsLeadingTrailingSpaces(_binding?.editTextText?.text?.toString()?:return@setOnClickListener)) {
                    sharedPrefUtils?.saveData(
                        context?:return@setOnClickListener,
                        SECURITY_QUESTION,
                        questionText
                    )
                    sharedPrefUtils?.saveData(
                        context?:return@setOnClickListener,
                        SECURITY_ANS,
                        _binding?.editTextText?.text.toString()
                    )
                    showToast(
                        context?:return@setOnClickListener, getString(R.string.save_security_question)
                    )

                    findNavController().navigateUp()
                } else {
                    _binding?.editTextText?.error = getString(R.string.empty_field)
                    showToast(
                        context?:return@setOnClickListener, getString(R.string.empty_field)
                    )
                }
            }
            _binding?.topLay?.backBtn?.setOnClickListener {
                findNavController().navigateUp()
            }
                loadBanner()
            setupBackPressedCallback {
                findNavController().navigateUp()
            }
            }
        } catch (e: Exception) {
          e.printStackTrace()
        }
    }

    private fun loadBanner(){
        when (type_ad_native_security_screen) {
            0 -> {
                _binding?.nativeExitAd?.visibility = View.GONE
                _binding?.adView?.visibility=View.GONE
            }

            1 -> {
                loadBanner(val_ad_native_security_screen)
            }

            2 -> {

            }
        }
    }

    private fun loadBanner(isAdsShow: Boolean) {
        _binding?.nativeExitAd?.apply {
            if (!isAdsShow) {
                visibility = View.INVISIBLE
                _binding?.adView?.visibility = View.INVISIBLE
                return
            }
            loadNativeBanner(
                context = requireContext(),
                isAdsShow = true,
                adSize = AdSize.LEADERBOARD, // Customize as needed
                onAdLoaded = { toggleVisibility(_binding?.nativeExitAd, true) },
                onAdFailed = { toggleVisibility(_binding?.nativeExitAd, false) },
                onAdPresented = { Log.d(TAG, "Ad presented from network: ${it.network}") },
                onAdClicked = { Log.d(TAG, "Ad clicked!") }
            )
        }
    }

    private fun toggleVisibility(view: View?, isVisible: Boolean) {
        _binding?.adView?.visibility=View.INVISIBLE
        view?.visibility = if (isVisible) View.VISIBLE else View.INVISIBLE
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding?.powerSpinnerView?.clearSelectedItem()
        _binding = null
    }
    override fun onLowMemory() {
        super.onLowMemory()
        activity?.finish()
    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.clear()
    }

}