package livewallpaper.aod.screenlock.zipper.zip_custom

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.utils.widget.ImageFilterView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import livewallpaper.aod.screenlock.ads_manager.AdmobNative
import livewallpaper.aod.screenlock.ads_manager.AdsManager
import livewallpaper.aod.screenlock.ads_manager.billing.BillingUtil
import livewallpaper.aod.screenlock.ads_manager.interfaces.NativeCallBack
import com.google.android.gms.ads.nativead.NativeAdView
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import livewallpaper.aod.screenlock.zipper.R
import livewallpaper.aod.screenlock.zipper.databinding.CustomZipMainFragmentBinding
import livewallpaper.aod.screenlock.zipper.service.LockScreenService
import livewallpaper.aod.screenlock.zipper.utilities.ConstantValues
import livewallpaper.aod.screenlock.zipper.utilities.Constants.getRowsView
import livewallpaper.aod.screenlock.zipper.utilities.Constants.getWallpapers
import livewallpaper.aod.screenlock.zipper.utilities.Constants.getZippers
import livewallpaper.aod.screenlock.zipper.utilities.DataBasePref
import livewallpaper.aod.screenlock.zipper.utilities.PurchaseScreen
import livewallpaper.aod.screenlock.zipper.utilities.clickWithThrottle
import livewallpaper.aod.screenlock.zipper.utilities.custtom_main
import livewallpaper.aod.screenlock.zipper.utilities.id_native_screen
import livewallpaper.aod.screenlock.zipper.utilities.setupBackPressedCallback
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_native_customize_screen
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_native_list_data_screen
import livewallpaper.aod.screenlock.zipper.utilities.val_inapp_frequency
import livewallpaper.aod.screenlock.zipper.zip_custom.adapter.CustomAdapter

class CustomMainFragment : Fragment(R.layout.custom_zip_main_fragment) {

    private var _binding: CustomZipMainFragmentBinding? = null
    private var ads: AdsManager? = null
    private var adapter: CustomAdapter? = null
    private var currentTab: String = "Zippers"
    private var zipperValue: String = "0"
    private var rowValue: String = "0"
    private var wallpaperValue: String = "0"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = CustomZipMainFragmentBinding.inflate(layoutInflater)
        Log.d("calling", "onCreateView: load main fragment")
        return _binding?.root
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        CoroutineScope(Dispatchers.Main).launch {
            if (++PurchaseScreen == val_inapp_frequency && !BillingUtil(
                    activity ?: return@launch
                ).checkPurchased(activity ?: return@launch)
            ) {
                PurchaseScreen = 0
                findNavController().navigate(R.id.FragmentBuyScreen, bundleOf("isSplash" to false))
                return@launch
            }
            ads = AdsManager.appAdsInit(activity ?: return@launch)

            zipperValue = DataBasePref.LoadPrefString(
                ConstantValues.SelectZipper,
                context ?: requireContext()
            ).toString()
            rowValue = DataBasePref.LoadPrefString(
                ConstantValues.SelectRow,
                context ?: requireContext()
            ).toString()
            wallpaperValue =
                DataBasePref.LoadPrefString(
                    ConstantValues.SelectedWallpaper,
                    context ?: requireContext()
                )
                    .toString()


            // Add tabs
            _binding?.tabLayout?.addTab(_binding?.tabLayout?.newTab()?.setText(getString(R.string.wallpaper))!!)
            _binding?.tabLayout?.addTab(_binding?.tabLayout?.newTab()?.setText(getString(R.string.zipper))!!)
            _binding?.tabLayout?.addTab(_binding?.tabLayout?.newTab()?.setText(getString(R.string.row))!!)

            // Initialize adapter and RecyclerView
            adapter = CustomAdapter(context ?: return@launch) {
                Log.d("currentTab", "onViewCreated: $currentTab")

                when (currentTab) {
                    getString(R.string.wallpaper) -> {
                        wallpaperValue = it.toString() + ""
                        adapter?.updateItem(it,1)
                    }
                    getString(R.string.zipper) -> {
                        zipperValue = it.toString() + ""
                        adapter?.updateItem(it,0)
                    }
                    getString(R.string.row) -> {
                        rowValue = it.toString() + ""
                        adapter?.updateItem(it,0)
                    }
                    else -> {
                        wallpaperValue = it.toString() + ""
                        adapter?.updateItem(it,1)
                    }
                }
            }
            _binding?.zipperRecyclerView?.adapter = adapter
            // Observe current items and update the RecyclerView

            when (currentTab) {
                getString(R.string.zipper) -> {
                    adapter?.updateItem(
                        DataBasePref.LoadPref(
                            ConstantValues.SelectZipper,
                            context ?: requireContext()
                        ).toInt(),0
                    )
                    getZippers().let { adapter?.submitList(it) }
                }

                getString(R.string.row) -> {

                    adapter?.updateItem(
                        DataBasePref.LoadPref(
                            ConstantValues.SelectRow,
                            context ?: requireContext()
                        ).toInt(),0
                    )
                    getRowsView().let { adapter?.submitList(it?:return@let) }
                }

                getString(R.string.wallpaper) -> {
                    adapter?.updateItem(
                        DataBasePref.LoadPref(
                            ConstantValues.SelectedWallpaper,
                            context ?: requireContext()
                        ).toInt(),1
                    )
                    getWallpapers().let { adapter?.submitList(it?:return@let) }
                }

                else -> {
                    adapter?.updateItem(
                        DataBasePref.LoadPref(
                            ConstantValues.SelectedWallpaper,
                            context ?: requireContext()
                        ).toInt(),1
                    )
                    getWallpapers().let { adapter?.submitList(it?:return@let) }
                }
            }

            // Handle tab selection to update data in the ViewModel
            _binding?.tabLayout?.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    currentTab = tab.text.toString()
                    when (currentTab) {
                        getString(R.string.zipper) -> {
                            adapter?.updateItem(
                                DataBasePref.LoadPref(
                                    ConstantValues.SelectZipper,
                                    context ?: requireContext()
                                ).toInt(),0
                            )
                            getZippers().let { adapter?.submitList(it) }
                        }

                        getString(R.string.row) -> {

                            adapter?.updateItem(
                                DataBasePref.LoadPref(
                                    ConstantValues.SelectRow,
                                    context ?: requireContext()
                                ).toInt(),0
                            )
                            getRowsView().let { adapter?.submitList(it?:return@let) }
                        }

                        getString(R.string.wallpaper) -> {
                            adapter?.updateItem(
                                DataBasePref.LoadPref(
                                    ConstantValues.SelectedWallpaper,
                                    context ?: requireContext()
                                ).toInt(),1
                            )
                            getWallpapers().let { adapter?.submitList(it?:return@let) }
                        }

                        else -> {
                            adapter?.updateItem(
                                DataBasePref.LoadPref(
                                    ConstantValues.SelectedWallpaper,
                                    context ?: requireContext()
                                ).toInt(),1
                            )
                            getWallpapers().let { adapter?.submitList(it?:return@let) }
                        }
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab) {}
                override fun onTabReselected(tab: TabLayout.Tab) {}
            })


            _binding?.topLay?.title?.text = getString(R.string.custom_setting)

            _binding?.previewButton?.setOnClickListener {
                if (Settings.canDrawOverlays(
                        context ?: requireContext()
                    )
                ) {
                    DataBasePref.SavePref(
                        ConstantValues.SelectedWallpaper,
                        wallpaperValue,
                        context ?: requireContext()
                    )
                    DataBasePref.SavePref(
                        ConstantValues.SelectZipper,
                        zipperValue,
                        context ?: requireContext()
                    )
                    DataBasePref.SavePref(
                        ConstantValues.SelectRow,
                        rowValue,
                        context ?: requireContext()
                    )
                    Log.d("values_theme", "onViewCreated: $wallpaperValue $rowValue $zipperValue")
                    LockScreenService.Start(context ?: requireContext())
                } else {
                    showCustomDialog()
                }
                // Handle preview button action, e.g., show a larger preview of selected wallpaper
            }
            view.findViewById<ImageFilterView>(R.id.backBtn)?.clickWithThrottle {
                findNavController().popBackStack()
            }
            setupBackPressedCallback {
                findNavController().popBackStack()
            }
            loadNative()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.clear()
    }

    private val admobNative by lazy { AdmobNative() }

    private fun loadNative() {
//        if (native_precashe_copunt_current >= native_precashe_counter) {
        admobNative.loadNativeAds(
            activity,
            _binding?.nativeExitAd!!,
            id_native_screen,
            if (val_ad_native_customize_screen)
                1 else 0,
            isAppPurchased = BillingUtil(activity?:return).checkPurchased(activity?:return),
            isInternetConnected = AdsManager.isNetworkAvailable(activity),
            nativeType = custtom_main,
            nativeCallBack = object : NativeCallBack {
                override fun onAdFailedToLoad(adError: String) {
                    _binding?.nativeExitAd?.visibility = View.GONE
                    _binding?.adView?.visibility = View.GONE}
                override fun onAdLoaded() {
                    _binding?.adView?.visibility = View.GONE}
                override fun onAdImpression() {
                    _binding?.adView?.visibility = View.GONE}
            }
        )
//        } else {
//        ads?.nativeAds()?.loadNativeAd(activity ?: return,
//            val_ad_native_customize_screen,
//            id_native_screen,
//            object : NativeListener {
//                override fun nativeAdLoaded(currentNativeAd: NativeAd?) {
//                    if (isAdded && isVisible && !isDetached) {
//                        _binding?.nativeExitAd?.visibility = View.VISIBLE
//                        _binding?.adView?.visibility = View.GONE
//                        val adView =
//                            layoutInflater.inflate(
//                                if (val_ad_native_customize_screen_h) R.layout.ad_unified_media else R.layout.ad_unified_privacy,
//                                null
//                            ) as NativeAdView
//                        ads?.nativeAds()
//                            ?.nativeViewPolicy(context ?: return, currentNativeAd ?: return, adView)
//                        _binding?.nativeExitAd?.removeAllViews()
//                        _binding?.nativeExitAd?.addView(adView)
//                    }
//                    super.nativeAdLoaded(currentNativeAd)
//                }
//
//                override fun nativeAdFailed(loadAdError: LoadAdError) {
//                    if (isAdded && isVisible && !isDetached) {
//                        _binding?.nativeExitAd?.visibility = View.GONE
//                        _binding?.adView?.visibility = View.GONE
//                    }
//                    super.nativeAdFailed(loadAdError)
//                }
//
//                override fun nativeAdValidate(string: String) {
//                    if (isAdded && isVisible && !isDetached) {
//                        _binding?.nativeExitAd?.visibility = View.GONE
//                        _binding?.adView?.visibility = View.GONE
//                    }
//                    super.nativeAdValidate(string)
//                }
//            })
//    }
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
        dialog.findViewById<View>(R.id.buttonOk).clickWithThrottle {
            if (isVisible && !isDetached && isAdded) {
                dialog.dismiss()
                askPermission()
            }
        }

        dialog.findViewById<View>(R.id.closeBtn).clickWithThrottle {
            if (isVisible && !isDetached && isAdded) {
                dialog.dismiss()
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

}

