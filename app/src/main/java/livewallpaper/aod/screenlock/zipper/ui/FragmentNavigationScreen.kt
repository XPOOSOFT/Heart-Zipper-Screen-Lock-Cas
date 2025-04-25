package livewallpaper.aod.screenlock.zipper.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.clap.whistle.phonefinder.utilities.DbHelper
import com.gold.zipper.goldzipper.lockscreen.royalgold.gold.gold_ads_manager.AdsManager
import com.gold.zipper.goldzipper.lockscreen.royalgold.gold.gold_ads_manager.FunctionClass.feedBackWithEmail
import com.gold.zipper.goldzipper.lockscreen.royalgold.gold.gold_ads_manager.billing.BillingUtil
import com.gold.zipper.goldzipper.lockscreen.royalgold.gold.gold_ads_manager.showTwoInterAd
import livewallpaper.aod.screenlock.zipper.R
import livewallpaper.aod.screenlock.zipper.databinding.NavigationLayoutBinding
import livewallpaper.aod.screenlock.zipper.service.LiveService
import livewallpaper.aod.screenlock.zipper.utilities.BaseFragment
import livewallpaper.aod.screenlock.zipper.utilities.CheckBoxUpdater
import livewallpaper.aod.screenlock.zipper.utilities.ConstantValues
import livewallpaper.aod.screenlock.zipper.utilities.Constants
import livewallpaper.aod.screenlock.zipper.utilities.Constants.isServiceRunning
import livewallpaper.aod.screenlock.zipper.utilities.DataBasePref
import livewallpaper.aod.screenlock.zipper.utilities.PurchaseScreen
import livewallpaper.aod.screenlock.zipper.utilities.clickWithThrottle
import livewallpaper.aod.screenlock.zipper.utilities.id_adaptive_banner
import livewallpaper.aod.screenlock.zipper.utilities.id_inter_main_medium
import livewallpaper.aod.screenlock.zipper.utilities.isFirstEnable
import livewallpaper.aod.screenlock.zipper.utilities.isSplash
import livewallpaper.aod.screenlock.zipper.utilities.moreApp
import livewallpaper.aod.screenlock.zipper.utilities.privacyPolicy
import livewallpaper.aod.screenlock.zipper.utilities.shareApp
import livewallpaper.aod.screenlock.zipper.utilities.showRatingDialog
import livewallpaper.aod.screenlock.zipper.utilities.showServiceDialog
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_inter_language_screen_front
import livewallpaper.aod.screenlock.zipper.utilities.val_ad_inter_security_screen_front
import livewallpaper.aod.screenlock.zipper.utilities.val_banner_setting_screen
import livewallpaper.aod.screenlock.zipper.utilities.val_inapp_frequency

class FragmentNavigationScreen :
BaseFragment<NavigationLayoutBinding>(NavigationLayoutBinding::inflate) {
    private var sharedPrefUtils: DbHelper? = null
    private var isFirst = false
    private var isActivated = false
    private var adsManager: AdsManager? = null

    @SuppressLint("SetTextI18n", "UseSwitchCompatOrMaterialCode")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(++PurchaseScreen == val_inapp_frequency && !BillingUtil(activity?:return).checkPurchased(activity?:return)){
            PurchaseScreen =0
            findNavController().navigate(R.id.FragmentBuyScreen, bundleOf("isSplash" to false))
            return
        }
        sharedPrefUtils = DbHelper(context ?: return)
        adsManager = AdsManager.appAdsInit(activity ?: return)
        _binding?.versionText?.text=getString(R.string.version)+"${context?.packageManager?.getPackageInfo(context?.packageName!!, 0)?.versionName}"
        _binding?.customSwitch?.setOnCheckedChangeListener { compoundButton, z ->
            if (compoundButton.isPressed) {
                if (!isFirst) {
                    _binding?.customSwitch?.isChecked = false
                    findNavController().navigate(R.id.EnableFirstActivity)
                    return@setOnCheckedChangeListener
                }
                if (checkPermissionOverlay(activity ?: return@setOnCheckedChangeListener)) {
                    isActivated = CheckBoxUpdater.UC(
                        _binding?.customSwitch ?: return@setOnCheckedChangeListener,
                        isActivated,
                        ConstantValues.ActivePref,
                        context ?: return@setOnCheckedChangeListener,
                        true,
                        null
                    )
                    if (isActivated) {
                        if (!Constants.isMainServiceRunning(
                                context ?: return@setOnCheckedChangeListener
                            )
                        ) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                ContextCompat.startForegroundService(
                                    requireContext(),
                                    Intent(requireContext(), LiveService::class.java)
                                )
                            } else {
                                requireContext().startService(
                                    Intent(
                                        context,
                                        LiveService::class.java
                                    )
                                )
                            }
                        }
                    } else {
                        _binding?.customSwitch?.isChecked = true
                        showServiceDialog(
                            onPositiveNoClick = {
                                _binding?.customSwitch?.isChecked = true
                            },
                            onPositiveYesClick = {
                                _binding?.customSwitch?.isChecked = false
                                if (isServiceRunning()) {
                                    Constants.stopServiceCall(
                                        activity ?: return@showServiceDialog
                                    )
                                }
                            })

                    }
                } else {
                    _binding?.customSwitch?.isChecked = false
                }
            }
        }
        _binding?.navigationMain?.clickWithThrottle { }
        _binding?.languageView?.clickWithThrottle {
            adsManager?.let {
                showTwoInterAd(
                    ads = it,
                    activity = activity ?: return@clickWithThrottle,
                    remoteConfigNormal = val_ad_inter_language_screen_front,
                    adIdNormal = id_inter_main_medium,
                    tagClass = "main_menu",
                    isBackPress = false,
                    layout = _binding?.adsLay ?: return@clickWithThrottle,
                ) {
                    findNavController().navigate(R.id.LanguageFragment)
                }
            }
        }
        _binding?.rateUsView?.clickWithThrottle {
            showRatingDialog(activity, onPositiveButtonClick = { it, _dialog ->
            })
        }
        _binding?.shareAppView?.clickWithThrottle {
            requireContext().shareApp()
        }
        _binding?.privacyView?.clickWithThrottle {
            requireContext().privacyPolicy()
        }
        _binding?.customSView?.clickWithThrottle {
            feedBackWithEmail(
                context =activity?:return@clickWithThrottle,
                title = "Feed Back",
                message = "User Send Feed Back",
                emailId = "fireitinc.dev@gmail.com"
            )
        }
        _binding?.feedBackView?.clickWithThrottle {
            findNavController().navigate(R.id.FragmentFeedBack)
        }
        _binding?.moreAppView?.clickWithThrottle {
            requireContext().moreApp()
        }
        _binding?.exitAppView?.clickWithThrottle {
            findNavController().navigate(R.id.FragmentExitScreen)
        }
        _binding?.securityQView?.clickWithThrottle {
            adsManager?.let {
                showTwoInterAd(
                    ads = it,
                    activity = activity ?: return@clickWithThrottle,
                    remoteConfigNormal = val_ad_inter_security_screen_front,
                    adIdNormal = id_inter_main_medium,
                    tagClass = "setting",
                    isBackPress = false,
                    layout = _binding?.adsLay ?: return@clickWithThrottle
                ){
                    findNavController().navigate(R.id.SecurityQuestionFragment)
                }
            }

        }
        _binding?.backIcon?.clickWithThrottle {
            findNavController().popBackStack()
        }
        loadBanner()
    }

    override fun onResume() {
        super.onResume()
        isFirst = sharedPrefUtils?.chkBroadCast(isFirstEnable) == true
        if (isFirst) {
            if (DataBasePref.LoadPrefString(ConstantValues.ActivePref, context ?: return).equals("1")) {
                _binding?.customSwitch?.isChecked = true
            } else {
                _binding?.customSwitch?.isChecked = false
            }
        } else {
            _binding?.customSwitch?.isChecked = false
        }
    }

    private fun checkPermissionOverlay(activity: Activity): Boolean {
        return try {
            if (Settings.canDrawOverlays(activity)) {
                return true
            }
            showCustomDialog()
            false
        } catch (unused: Exception) {
            true
        }
    }

    private fun showCustomDialog() {
        val dialog = Dialog(context ?: return)
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
            dialog.dismiss()
            askPermission(activity ?: return@clickWithThrottle)
        }

        dialog.findViewById<View>(R.id.closeBtn).setOnClickListener {
            dialog.dismiss()
        }
    }

    private fun askPermission(activity: Activity) {
        isSplash =false
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:${context?.packageName}")
        )
        startActivityForResult(intent, 100)
    }

    private fun loadBanner(){
        adsManager?.adsBanners()?.loadBanner(
            activity = activity ?: return,
            view = _binding!!.adsView,
            addConfig = val_banner_setting_screen,
            bannerId = id_adaptive_banner
        ) {

        }
    }

}