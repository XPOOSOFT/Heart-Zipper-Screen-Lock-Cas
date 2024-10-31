//package livewallpaper.aod.screenlock.zipper.ui
//
//import android.app.Activity
//import android.app.Dialog
//import android.content.Context
//import android.content.Intent
//import android.content.res.Configuration
//import android.graphics.Color
//import android.graphics.drawable.ColorDrawable
//import android.net.Uri
//import android.os.Build
//import android.os.Bundle
//import android.provider.Settings
//import android.view.View
//import android.view.ViewGroup
//import android.widget.ImageView
//import androidx.appcompat.app.AppCompatActivity
//import com.bumptech.glide.Glide
//import kotlinx.coroutines.MainScope
//import kotlinx.coroutines.launch
//import livewallpaper.aod.screenlock.zipper.MainActivity
//import livewallpaper.aod.screenlock.zipper.R
//import livewallpaper.aod.screenlock.zipper.ads_manager.AdsManager
//import livewallpaper.aod.screenlock.zipper.ads_manager.showTwoInterAd
//import livewallpaper.aod.screenlock.zipper.databinding.ZipperThemePreviewActivityBinding
//import livewallpaper.aod.screenlock.zipper.service.LockScreenService
//import livewallpaper.aod.screenlock.zipper.utilities.ConstantValues.SelectRow
//import livewallpaper.aod.screenlock.zipper.utilities.ConstantValues.SelectRowTemp
//import livewallpaper.aod.screenlock.zipper.utilities.ConstantValues.SelectZipper
//import livewallpaper.aod.screenlock.zipper.utilities.ConstantValues.SelectZipperTemp
//import livewallpaper.aod.screenlock.zipper.utilities.ConstantValues.SelectedWallpaper
//import livewallpaper.aod.screenlock.zipper.utilities.ConstantValues.SelectedWallpaperTemp
//import livewallpaper.aod.screenlock.zipper.utilities.Constants.getBackground
//import livewallpaper.aod.screenlock.zipper.utilities.Constants.getRowsPreview
//import livewallpaper.aod.screenlock.zipper.utilities.Constants.getZippers
//import livewallpaper.aod.screenlock.zipper.utilities.DataBasePref
//import livewallpaper.aod.screenlock.zipper.utilities.Uscreen
//import livewallpaper.aod.screenlock.zipper.utilities.id_inter_main_medium
//import livewallpaper.aod.screenlock.zipper.utilities.val_inter_back_press
//import livewallpaper.aod.screenlock.zipper.utilities.val_inter_main_medium
//
//class ZipperThemePreviewActivity : AppCompatActivity() {
//
//    private var _binding: ZipperThemePreviewActivityBinding? = null
//    private var adsManager: AdsManager? = null
//    private var postion: Int = 0
//    val mainScope = MainScope()
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        _binding = ZipperThemePreviewActivityBinding.inflate(layoutInflater)
//        setContentView(_binding?.root)
//        postion = intent.getIntExtra("preview", 0)
//        adsManager = AdsManager.appAdsInit(this)
//        _binding?.mainbg?.setBackgroundResource(MainActivity.background)
//        Glide.with(this).load(
//            getBackground()?.get(DataBasePref.LoadPref(SelectedWallpaper, this))
//        )
//            .into(_binding?.zipperBg?:return)
//        Glide.with(this).load(getRowsPreview()?.get(DataBasePref.LoadPref(SelectRow, this))?:return)
//            .into(_binding?.rowBr?:return)
//        Glide.with(this).load(mainScope.launch {getZippers()[DataBasePref.LoadPref(SelectZipper, this@ZipperThemePreviewActivity)]})
//            .into(_binding?.clipBr?:return)
//        _binding?.run {
//            cancelBtn.setOnClickListener {
//                onBackPressed()
//            }
//            applyBtn.setOnClickListener {
//                exitApp()
//            }
//            previewBtn.setOnClickListener {
//                if (Settings.canDrawOverlays(
//                        this@ZipperThemePreviewActivity
//                    )
//                ) {
//                            LockScreenService.Start(this@ZipperThemePreviewActivity)
//                            return@setOnClickListener
//                } else {
//                    showCustomDialog()
//                }
//            }
//        }
//        loadIntertital()
//    }
//
//    private fun preferenceCheck() {
//        when (postion) {
//            0 -> {
//                DataBasePref.SavePref(
//                    SelectedWallpaper,
//                    SelectedWallpaperTemp,
//                    applicationContext
//                )
//            }
//
//            1 -> {
//                DataBasePref.SavePref(
//                    SelectZipper,
//                    SelectZipperTemp,
//                    applicationContext
//                )
//            }
//
//            2 -> {
//                DataBasePref.SavePref(
//                    SelectRow,
//                    SelectRowTemp,
//                    applicationContext
//                )
//            }
//        }
//    }
//
//    private fun exitApp() {
//        adsManager?.let {
//            showTwoInterAd(
//                ads = it,
//                activity = this,
//                remoteConfigNormal = val_inter_main_medium,
//                adIdNormal = id_inter_main_medium,
//                tagClass = "theme_preview_activity",
//                isBackPress = !val_inter_back_press,
//                layout = _binding?.adsLay?:return,
//            ) {
//                finish()
//            }
//        }
//    }
//
//    override fun onBackPressed() {
//        super.onBackPressed()
//        preferenceCheck()
////        adsManager?.let {
////            showTwoInterAd(
////                ads = it,
////                activity = this,
////                remoteConfigMedium = val_inter_main_medium,
////                remoteConfigNormal = val_inter_main_medium,
////                adIdMedium = id_inter_main_medium,
////                adIdNormal = id_inter_main_medium,
////                tagClass = "theme_preview_activity",
////                isBackPress = !val_inter_back_press,
////                layout = _binding?.adsLay?:return,
////            ) {
//                finish()
////            }
////        }
//    }
//
//    override fun attachBaseContext(newBase: Context?) {
//
//        val newOverride = Configuration(newBase?.resources?.configuration)
//        newOverride.fontScale = 1.0f
//        applyOverrideConfiguration(newOverride)
//        super.attachBaseContext(newBase)
//    }
//
//    private fun showCustomDialog() {
//        val dialog = Dialog(this)
//        dialog.requestWindowFeature(1)
//        dialog.setContentView(R.layout.permission_dialog)
//        dialog.show()
//        Glide.with(this)
//            .load(R.raw.dialog)
//            .into(dialog.findViewById<ImageView>(R.id.animationView))
//        dialog.window?.apply {
//            // Set the dialog to be full-screen
//            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
//            setBackgroundDrawable(ColorDrawable(Color.WHITE))  // Optional: Set background to transparent
//        }
//        dialog.findViewById<View>(R.id.buttonOk).setOnClickListener {
//            dialog.dismiss()
//            askPermission(this)
//        }
//        dialog.findViewById<View>(R.id.closeBtn).setOnClickListener {
//            dialog.dismiss()
//        }
//    }
//
//    private fun askPermission(activity: Activity) {
//        val intent = Intent(
//            "android.settings.action.MANAGE_OVERLAY_PERMISSION",
//            Uri.parse("package:" + activity.packageName)
//        )
//        activity.startActivity(intent)
//    }
//
//    private fun loadIntertital(){
////        loadTwoInterAds(
////            ads = adsManager ?: return,
////            activity = this,
////            remoteConfigMedium = val_inter_main_medium,
////            remoteConfigNormal = val_inter_main_medium,
////            adIdMedium = id_inter_main_medium,
////            adIdNormal = id_inter_main_medium,
////            tagClass = "zipper_theme_list_activity"
////        )
//
//        adsManager?.let {
//            showTwoInterAd(
//                ads = it,
//                activity = this@ZipperThemePreviewActivity,
//                remoteConfigNormal = val_inter_main_medium,
//                adIdNormal = id_inter_main_medium,
//                tagClass = "theme_preview_activity",
//                isBackPress = false,
//                layout = _binding?.adsLay?:return,
//            ) {
//            }
//        }
//    }
//
//}