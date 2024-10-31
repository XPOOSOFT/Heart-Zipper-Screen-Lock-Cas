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
import livewallpaper.aod.screenlock.zipper.MainActivity.Companion.background
import livewallpaper.aod.screenlock.zipper.R
import livewallpaper.aod.screenlock.zipper.adapter.SoundSelectLinearAdapter
import livewallpaper.aod.screenlock.zipper.ads_manager.AdsManager
import livewallpaper.aod.screenlock.zipper.databinding.FragmentSoundSelectionBinding
import livewallpaper.aod.screenlock.zipper.utilities.PurchaseScreen
import livewallpaper.aod.screenlock.zipper.utilities.ZIPPER_SOUND
import livewallpaper.aod.screenlock.zipper.utilities.clickWithThrottle
import livewallpaper.aod.screenlock.zipper.utilities.id_adaptive_banner
import livewallpaper.aod.screenlock.zipper.utilities.setupBackPressedCallback
import livewallpaper.aod.screenlock.zipper.utilities.soundData
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_native_sound_screen
import livewallpaper.aod.screenlock.zipper.utilities.val_inapp_frequency

class FragmentSoundSelection : Fragment() {

    private var sharedPrefUtils: DbHelper? = null
    private var adsManager: AdsManager? = null
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
            adsManager = AdsManager.appAdsInit(activity?:requireActivity())
            sharedPrefUtils = DbHelper(context?:requireContext())
            _binding?.topLay?.title?.text = getString(R.string.select_sound)
            _binding?.mainbg?.setBackgroundResource(background)
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

/*    private fun loadNative() {
        adsManager?.nativeAds()?.loadNativeAd(
            activity?:requireActivity(),
            val_ad_native_sound_screen,
            id_native_screen,
            object : NativeListener {
                override fun nativeAdLoaded(currentNativeAd: NativeAd?) {
                    _binding?.nativeExitAd?.visibility = View.VISIBLE
                    _binding?.adView?.visibility = View.GONE
                    val adView = layoutInflater.inflate(
                        R.layout.ad_unified_privacy,
                        null
                    ) as NativeAdView
                    adsManager?.nativeAds()?.nativeViewPolicy(context?:requireContext(),currentNativeAd ?: return, adView)
                    _binding?.nativeExitAd?.removeAllViews()
                    _binding?.nativeExitAd?.addView(adView)
                    super.nativeAdLoaded(currentNativeAd)
                }

                override fun nativeAdFailed(loadAdError: LoadAdError) {
                    _binding?.nativeExitAd?.visibility = View.GONE
                    _binding?.adView?.visibility = View.GONE
                    super.nativeAdFailed(loadAdError)
                }

                override fun nativeAdValidate(string: String) {
                    _binding?.nativeExitAd?.visibility = View.GONE
                    _binding?.adView?.visibility = View.GONE
                    super.nativeAdValidate(string)
                }
            })


    }*/
    private fun loadBanner(){
        adsManager?.adsBanners()?.loadBanner(
            activity = activity ?: return,
            view = _binding!!.nativeExitAd,
            addConfig = val_ad_native_sound_screen,
            bannerId = id_adaptive_banner
        ) {
            _binding!!.adView?.visibility=View.GONE

        }
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

}