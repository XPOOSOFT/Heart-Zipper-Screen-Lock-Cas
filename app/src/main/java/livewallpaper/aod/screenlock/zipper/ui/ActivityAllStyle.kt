package livewallpaper.aod.screenlock.zipper.ui

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.clap.whistle.phonefinder.utilities.DbHelper
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import livewallpaper.aod.screenlock.zipper.R
import livewallpaper.aod.screenlock.zipper.adapter.ZippersRecyclerAdapter
import livewallpaper.aod.screenlock.zipper.ads_manager.AdsManager
import livewallpaper.aod.screenlock.zipper.ads_manager.interfaces.NativeListener
import livewallpaper.aod.screenlock.zipper.databinding.FragmentAllStylesBinding
import livewallpaper.aod.screenlock.zipper.service.LockScreenService
import livewallpaper.aod.screenlock.zipper.utilities.ConstantValues
import livewallpaper.aod.screenlock.zipper.utilities.ConstantValues.StyleSelect
import livewallpaper.aod.screenlock.zipper.utilities.Constants
import livewallpaper.aod.screenlock.zipper.utilities.DataBasePref
import livewallpaper.aod.screenlock.zipper.utilities.PurchaseScreen
import livewallpaper.aod.screenlock.zipper.utilities.Uscreen
import livewallpaper.aod.screenlock.zipper.utilities.clickWithThrottle
import livewallpaper.aod.screenlock.zipper.utilities.id_adaptive_banner
import livewallpaper.aod.screenlock.zipper.utilities.id_native_screen
import livewallpaper.aod.screenlock.zipper.utilities.isFirstEnable
import livewallpaper.aod.screenlock.zipper.utilities.setupBackPressedCallback
import livewallpaper.aod.screenlock.zipper.utilities.type_ad_native_list_data_screen
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_native_list_data_screen
import livewallpaper.aod.screenlock.zipper.utilities.val_exit_dialog_native
import livewallpaper.aod.screenlock.zipper.utilities.val_inapp_frequency

class ActivityAllStyle : Fragment() {

    private var zippersRecyclerAdapter: ZippersRecyclerAdapter? = null
    private var list: List<Int>? = null
    private var name: String? = null
    private var _binding: FragmentAllStylesBinding? = null
    private var adsManager: AdsManager? = null
    var isShowAdsNative = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentAllStylesBinding.inflate(layoutInflater)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(++PurchaseScreen ==val_inapp_frequency){
            PurchaseScreen =0
            findNavController().navigate(R.id.FragmentBuyScreen, bundleOf("isSplash" to false))
            return
        }
        adsManager = AdsManager.appAdsInit(activity ?: return)
        loadBanner()
        var name = ""
        arguments?.let {
            name = it.getString(StyleSelect).toString()
        }
        detData(name)
        _binding?.topLay?.backBtn?.clickWithThrottle {
//            isBackShow = val_ad_inter_list_data_screen_back
            findNavController().popBackStack()
        }
        setupBackPressedCallback {
//            isBackShow = val_ad_inter_list_data_screen_back
            findNavController().popBackStack()
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun detData(nameText: String) {
        when (nameText) {
            getString(R.string.wallpapers) -> {
                list = Constants.getBackground()
                name = ConstantValues.SelectedWallpaper
                _binding?.topLay?.title?.text = getString(R.string.wallpapers)
                zippersRecyclerAdapter =
                    ZippersRecyclerAdapter(
                        list ?: return,
                        name ?: return,
                        context ?: requireContext(),
                        true
                    )
                zippersRecyclerAdapter?.onClick = {
                    if (Settings.canDrawOverlays(
                            context ?: requireContext()
                        )
                    ) {
                        DataBasePref.SavePref(
                            ConstantValues.SelectedWallpaper,
                            it.toString() + "",
                            context ?: requireContext()
                        )
                        zippersRecyclerAdapter?.updateItem(it)
                        LockScreenService.Start(context ?: requireContext())
                    } else {
                        showCustomDialog()
                    }
                }
            }

            getString(R.string.zipperStyle) -> {
                list = Constants.getZippers()
                name = ConstantValues.SelectZipper
                _binding?.topLay?.title?.text = getString(R.string.zipperStyle)
                zippersRecyclerAdapter =
                    ZippersRecyclerAdapter(
                        list ?: return,
                        name ?: return,
                        context ?: requireContext(),
                        false
                    )
                zippersRecyclerAdapter?.onClick = {
                    if (Settings.canDrawOverlays(
                            context ?: requireContext()
                        )
                    ) {
                        DataBasePref.SavePref(
                            ConstantValues.SelectZipper,
                            it.toString() + "",
                            context ?: requireContext()
                        )
                        zippersRecyclerAdapter?.updateItem(it)
                        LockScreenService.Start(context ?: requireContext())
                    } else {
                        showCustomDialog()
                    }
                }
            }

            getString(R.string.row_style) -> {
                list = Constants.getRowsView()
                name = ConstantValues.SelectRow
                _binding?.topLay?.title?.text = getString(R.string.row_style)
                zippersRecyclerAdapter =
                    ZippersRecyclerAdapter(
                        list ?: return,
                        name ?: return,
                        context ?: requireContext(),
                        false
                    )
                zippersRecyclerAdapter?.onClick = {
                    if (Settings.canDrawOverlays(
                            context ?: requireContext()
                        )
                    ) {
                        DataBasePref.SavePref(
                            ConstantValues.SelectRow,
                            it.toString() + "",
                            context ?: requireContext()
                        )
                        zippersRecyclerAdapter?.updateItem(it)
                        LockScreenService.Start(context ?: requireContext())
                    } else {
                        showCustomDialog()
                    }
                }
            }
        }
        _binding?.recyclerView?.adapter = zippersRecyclerAdapter
    }

    private fun showPreview() {
        if (Settings.canDrawOverlays(
                context ?: return
            )
        ) {
            LockScreenService.Start(context ?: return)
            return
        } else {
            showCustomDialog()
        }
    }

    private fun loadBanner(){
        when (type_ad_native_list_data_screen) {
            0 -> {
                _binding?.nativeExitAd?.visibility = View.GONE
                _binding?.adView?.visibility=View.GONE
            }

            1 -> {
                adsManager?.adsBanners()?.loadBanner(
                    activity = activity ?: return,
                    view = _binding!!.nativeExitAd,
                    addConfig = val_ad_native_list_data_screen,
                    bannerId = id_adaptive_banner
                ) {
                    _binding!!.adView?.visibility=View.GONE
                }
            }

            2 -> {
                adsManager?.nativeAds()?.loadNativeAd(
                    activity ?: return,
                    val_ad_native_list_data_screen,
                    id_native_screen,
                    object : NativeListener {
                        override fun nativeAdLoaded(currentNativeAd: NativeAd?) {
                            if (isAdded && isVisible && !isDetached) {
                                _binding?.nativeExitAd?.visibility = View.VISIBLE
                                _binding?.adView?.visibility = View.GONE
                                val adView =layoutInflater.inflate(R.layout.ad_unified_media, null) as NativeAdView
                                adsManager?.nativeAds()?.nativeViewMedia(context?:return,currentNativeAd ?: return, adView)
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

    override fun onResume() {
        super.onResume()
        detData(name ?: return)
        if (isShowAdsNative) {
            _binding?.nativeExitAd?.visibility = View.VISIBLE
        }
    }

    private fun showCustomDialog() {
        val dialog = Dialog(activity ?: return)
        dialog.requestWindowFeature(1)
        dialog.setContentView(R.layout.permission_dialog)
        dialog.show()
        Glide.with(this)
            .load(R.raw.dialog)
            .into(dialog.findViewById<ImageView>(R.id.animationView))
        dialog.window?.apply {
            // Set the dialog to be full-screen
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            setBackgroundDrawable(ColorDrawable(Color.WHITE))  // Optional: Set background to transparent
        }
        dialog.findViewById<View>(R.id.closeBtn).setOnClickListener {
            if(isVisible && !isDetached && isAdded) {
                dialog.dismiss()
            }
        }
        dialog.findViewById<View>(R.id.buttonOk).setOnClickListener {
            if(isVisible && !isDetached && isAdded) {
                dialog.dismiss()
                askPermission()
            }
        }

    }

    private fun askPermission() {
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:${context?.packageName}")
        )
        startActivityForResult(intent, 100)
    }

    private fun isOverlayPermissionGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(activity?.applicationContext)
        } else {
            true
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100) {
            // Check if the user granted the overlay permission
            if (isOverlayPermissionGranted()) {
                DbHelper(context ?: requireContext()).setBroadCast(isFirstEnable, true)
                DataBasePref.SavePref(ConstantValues.ActivePref, "1", context ?: requireContext())
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    ContextCompat.startForegroundService(
                        context ?: requireContext(),
                        Intent(context ?: requireContext(), LockScreenService::class.java)
                    )
                } else {
                    context?.startService(
                        Intent(
                            context ?: requireContext(), LockScreenService::class.java
                        )
                    )
                }
//                showOpenAd(activity ?: requireActivity())
            } else {
            }
        }
    }

    override fun onPause() {
        super.onPause()
        if (isShowAdsNative) {
            _binding?.nativeExitAd?.visibility = View.INVISIBLE
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.clear()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}