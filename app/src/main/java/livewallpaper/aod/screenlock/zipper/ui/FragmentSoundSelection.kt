package livewallpaper.aod.screenlock.zipper.ui

import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.clap.whistle.phonefinder.utilities.DbHelper
import livewallpaper.aod.screenlock.zipper.R
import livewallpaper.aod.screenlock.zipper.adapter.SoundSelectLinearAdapter
import livewallpaper.aod.screenlock.zipper.ads_cam.AdmobNative
import livewallpaper.aod.screenlock.zipper.ads_cam.NativeCallBack
import livewallpaper.aod.screenlock.zipper.ads_cam.NativeType
import livewallpaper.aod.screenlock.zipper.ads_cam.billing.BillingUtil
import livewallpaper.aod.screenlock.zipper.ads_cam.loadNativeBanner
import livewallpaper.aod.screenlock.zipper.databinding.FragmentSoundSelectionBinding
import livewallpaper.aod.screenlock.zipper.utilities.PurchaseScreen
import livewallpaper.aod.screenlock.zipper.utilities.ZIPPER_SOUND
import livewallpaper.aod.screenlock.zipper.utilities.clickWithThrottle
import livewallpaper.aod.screenlock.zipper.utilities.id_native_screen
import livewallpaper.aod.screenlock.zipper.utilities.isNetworkAvailable
import livewallpaper.aod.screenlock.zipper.utilities.setupBackPressedCallback
import livewallpaper.aod.screenlock.zipper.utilities.soundData
import livewallpaper.aod.screenlock.zipper.utilities.type_ad_native_sound_screen
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_native_enable_screen
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_native_sound_screen
import livewallpaper.aod.screenlock.zipper.utilities.val_inapp_frequency

class FragmentSoundSelection : Fragment() {

    private var sharedPrefUtils: DbHelper? = null
    private var adapterLinear: SoundSelectLinearAdapter? = null
    private var _binding: FragmentSoundSelectionBinding? = null
    private var mediaPlayer: MediaPlayer? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentSoundSelectionBinding.inflate(layoutInflater)
        Log.d("calling", "onCreateView: load main fragment")
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(++PurchaseScreen ==val_inapp_frequency){
            PurchaseScreen =0
            findNavController().navigate(R.id.FragmentBuyScreen, bundleOf("isSplash" to false))
            return
        }
        try {
            sharedPrefUtils = DbHelper(context?:requireContext())
            _binding?.topLay?.title?.text = getString(R.string.select_sound)
            _binding?.run {
                topLay.backBtn.clickWithThrottle {
                    findNavController().navigateUp()
                }
                loadLayoutDirection()
            }
            loadBanner()
            setupBackPressedCallback {
                findNavController().navigateUp()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadLayoutDirection() {

        adapterLinear = SoundSelectLinearAdapter(clickItem = {
            playSound(it)
            sharedPrefUtils?.setTone(ZIPPER_SOUND, it)
        }, onClick = {
            playSound(it)
        }, soundData = soundData(activity?:requireActivity()))

        val managerLayout = LinearLayoutManager(context?:requireContext())
        managerLayout.orientation = LinearLayoutManager.VERTICAL
        _binding?.recycler?.layoutManager = managerLayout
        sharedPrefUtils?.getTone(context?:requireContext(), ZIPPER_SOUND)
            ?.let { adapterLinear?.selectLanguage(it) }
        _binding?.recycler?.adapter = adapterLinear
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.clear()
    }


    private fun loadBanner(){
        when (type_ad_native_sound_screen) {
            0 -> {
                _binding?.nativeExitAd?.visibility = View.GONE
                _binding?.adView?.visibility=View.GONE
            }

            1 -> {
                loadBanner(val_ad_native_sound_screen)
            }

            2 -> {

                AdmobNative().loadNativeAds(
                    activity,
                    _binding?.nativeExitAd!!,
                    id_native_screen,
                    if (val_ad_native_sound_screen)
                        1 else 0,
                    isAppPurchased = BillingUtil(activity ?: return).checkPurchased(activity ?: return),
                    isInternetConnected = isNetworkAvailable(activity),
                    nativeType = NativeType.LARGE,
                    nativeCallBack = object : NativeCallBack {
                        override fun onAdFailedToLoad(adError: String) {
                            _binding?.adView?.visibility = View.GONE
                            _binding?.nativeExitAd?.visibility = View.GONE
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
        }
    }

    private fun loadBanner(isAdsShow: Boolean) {
        _binding?.nativeExitAd?.apply {
            if   (!isAdsShow || !isNetworkAvailable(context)) {
                visibility = View.INVISIBLE
                _binding?.adView?.visibility = View.INVISIBLE
                return
            }

        }
    }

    private fun toggleVisibility(view: View?, isVisible: Boolean) {
        _binding?.adView?.visibility=View.INVISIBLE
        view?.visibility = if (isVisible) View.VISIBLE else View.INVISIBLE
    }

    private fun playSound(id: Int) {
        try {
            mediaPlayer = MediaPlayer.create(context?:requireContext(), soundData(activity?:requireActivity())[id].soundFile)
            // Set an event listener to release the MediaPlayer when playback is complete
            mediaPlayer?.setOnCompletionListener {
                mediaPlayer?.release()
                mediaPlayer = null
                adapterLinear?.onSoundComplete()
            }
            // Start playing the sound
            mediaPlayer?.start()
        } catch (e: Exception) {
            e.printStackTrace()
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