package livewallpaper.aod.screenlock.zipper.utilities

import android.provider.Settings
import android.app.Activity
import android.app.ActivityManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.SystemClock
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.WindowInsetsController
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.tasks.Task
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import livewallpaper.aod.screenlock.zipper.R
import livewallpaper.aod.screenlock.zipper.model.LanguageModel
import livewallpaper.aod.screenlock.zipper.model.SoundModel
import livewallpaper.aod.screenlock.zipper.utilities.ConstantValues.SpeedActivePref
import livewallpaper.aod.screenlock.zipper.utilities.DataBasePref.LoadPref
import livewallpaper.aod.screenlock.reward.wallpaper.Wallpaper
import livewallpaper.aod.screenlock.zipper.model.MainMenu
import livewallpaper.aod.screenlock.zipper.service.LockScreenService
import livewallpaper.aod.screenlock.zipper.ui.MainAppFragment
import livewallpaper.aod.screenlock.zipper.utilities.ConstantValues.StyleSelect
import org.json.JSONArray
import java.util.Calendar
import java.util.Locale
import kotlin.random.Random

var isRating = true
var PurchaseScreen = 0
var isMainAdsShow = true
var counter = 0
var firstCounter = 0
var isSplash = true

var interLanguageScreen = 1
//var native_precashe_copunt_current = 0

var isFlowOne = true
var val_app_open = true
var val_on_bording_screen = true
var val_inter_main_medium = true

var val_ad_native_sound_screen = true
var val_ad_native_setting_screen = true
var val_ad_native_security_screen = true
var val_ad_native_enable_screen = true
var val_exit_dialog_native = true
var val_ad_native_main_menu_screen = true
var val_ad_native_password_screen = true
var val_ad_native_list_data_screen = true
var val_ad_native_intro_screen = true
var val_ad_native_language_screen = true
var val_ad_native_loading_screen = true
var val_ad_native_reward_screen = true

var type_ad_native_sound_screen = 2
var type_ad_native_setting_screen = 2
var type_ad_native_security_screen = 2
var type_ad_native_enable_screen = 2
var type_ad_native_password_screen = 2
var type_ad_native_list_data_screen = 2
var type_ad_native_reward_screen = 2

var val_ad_inter_reward_screen = true
var val_ad_inter_customize_screen = true

var val_banner_language_screen = true
var val_collapsable_banner = false
var val_banner_setting_screen = true

var val_ad_inter_loading_screen = true
var val_ad_inter_language_screen = true

var val_ad_inter_main_menu_screen_front = true
var val_ad_inter_language_screen_front = true
var val_ad_inter_sound_screen_front = true
var val_exit_dialog_inter_front = true
var val_ad_inter_setting_screen_front = true
var val_ad_inter_security_screen_front = true
var val_ad_inter_enable_screen_front = true
var val_ad_inter_password_screen_front = true
var val_ad_inter_list_data_screen_front = true
var val_ad_native_customize_screen = true

//New
var val_ad_app_open_screen = true
var val_is_inapp_splash = false
var val_is_inapp = false

var sessionOpenLanguageNew = 2
var sessionOnboarding = 1

var splash_bottom = 2
var language_bottom = 2
var language_bottom_second = 2
var home_native = 2
var on_bord_native = 2
var thankyou_bottom = 2

var custtom_main = 2
var wallpaper_fragment = 2
var theme_all = 2
var enable_first = 2
var apply_password = 2
var setting_fragment = 2
var sound_select = 2
var security_question = 2

//New Ids

var id_inter_watch_ads_Screen = ""
var val_ad_inter_watch_ads_screen = true
var val_ad_inter_wallpaper_server_screen = true
var val_ad_inter_in_app = true
var val_collapsable_banner_home = true

//end new
var banner_height = 160
var banner_type = 1
var appUpdateType = 0
var inter_frequency_count = 0
var id_frequency_counter = 10
var val_inapp_frequency = 10
var id_inter_counter = 0

var id_inter_main_medium = if (isDebug()) "ca-app-pub-3940256099942544/1033173712" else ""
var id_native_screen = ""
var id_language_native_second = ""
var id_app_open_screen = ""
var id_adaptive_banner = ""
var id_inter_splash_Screen = ""
var id_collapsable_banner = ""
var id_splash_native = ""
var id_reward = ""
var apiKey = ""
var Wallpaper_Cat = ""

var id_ads_button = "#F3202F"
var id_ads_bg = "#232323"
var id_ads_text_color = "#232323"

var id_ads_splash_button = "#F3202F"
var id_ads_splash_bg = "#232323"
var id_ads_splash_text_color = "#232323"

const val NOTIFY_CHANNEL_ID = "AppNameBackgroundService"

const val IS_NOTIFICATION = "IS_NOTIFICATION"
const val isFirstEnable = "isFirstEnable"

const val IS_FIRST = "is_First"
const val IS_INTRO = "is_Intro"
const val LANG_CODE = "language_code"
const val LANG_SCREEN = "LANG_SCREEN"
const val ZIPPER_SOUND = "ZIPPER_SOUND"

const val SECURITY_QUESTION = "SECURITY_QUESTION"
const val SECURITY_ANS = "SECURITY_ANS"

const val AUDIO_PERMISSION = "android.permission.RECORD_AUDIO"

//const val PHONE_PERMISSION = "android.permission.READ_PHONE_STATE"
const val NOTIFICATION_PERMISSION = "android.permission.POST_NOTIFICATIONS"

var slideImages = arrayOf(
    R.drawable.image_2, R.drawable.image_4, R.drawable.image_3, R.drawable.image_1
)

fun showAdsDialog(
    context: Activity,
    onInApp: () -> Unit,
    onWatchAds: () -> Unit,
) {
    val dialogView = context.layoutInflater.inflate(R.layout.dialog_reward_ads, null)
    ratingService = AlertDialog.Builder(context).create()
    ratingService?.setView(dialogView)
    ratingService?.window?.setBackgroundDrawableResource(android.R.color.transparent)
    val inApp= dialogView.findViewById<View>(R.id.inApp)
    val watchAds = dialogView.findViewById<View>(R.id.enableLock)
    inApp.setOnClickListener {
        ratingService?.dismiss()
        onInApp()
    }

    watchAds.setOnClickListener {
        ratingService?.dismiss()
        onWatchAds()
    }

    ratingService?.show()

}

// Utility to parse JSON into a List of Wallpaper objects
fun parseWallpaperJson(jsonString: String): List<Wallpaper> {
    val jsonArray = JSONArray(jsonString)
    val wallpapers = mutableListOf<Wallpaper>()

    for (i in 0 until jsonArray.length()) {
        val jsonObject = jsonArray.getJSONObject(i)
        val name = jsonObject.getString("name")
        val previews = mutableListOf<String>()

        for (j in 1..8) { // Loop through preview1 to preview8
            val previewKey = "preview$j"
            if (jsonObject.has(previewKey)) {
                previews.add(jsonObject.getString(previewKey))
            }
        }

        wallpapers.add(Wallpaper(name, previews))
    }
    return wallpapers
}

fun getImagesFromTitle(jsonArrayString: String, title: String): List<String> {
    val jsonArray = JSONArray(jsonArrayString)
    val imagesList = mutableListOf<String>()

    for (i in 0 until jsonArray.length()) {
        val jsonObject = jsonArray.getJSONObject(i)

        // Check if the title matches
        if (jsonObject.getString("title") == title) {
            val imagesArray = jsonObject.getJSONArray("images")

            // Add each image URL to the list
            for (j in 0 until imagesArray.length()) {
                imagesList.add(imagesArray.getString(j))
            }
            break // Stop searching once the title is found
        }
    }
    return imagesList
}

fun loadJSONFromAsset(context: Context, fileName: String): String? {
    return try {
        val inputStream = context.assets.open(fileName)
        val size = inputStream.available()
        val buffer = ByteArray(size)
        inputStream.read(buffer)
        inputStream.close()
        String(buffer, Charsets.UTF_8)
    } catch (ex: Exception) {
        ex.printStackTrace()
        null
    }
}

fun introHeadingNew(context: Context): ArrayList<String>
{
    val list = arrayListOf<String>()
    list.add(context.getString(R.string.wallpaper))
    list.add(context.getString(R.string.zipperStyle))
    list.add(context.getString(R.string.row_style))
    list.add(context.getString(R.string.screen_lock))
    return list
}


fun getMainMenu(context: Context): ArrayList<MainMenu>
{
    val list = arrayListOf<MainMenu>()
    list.add(MainMenu(context.getString(R.string.zipper),R.drawable.zip_icon,context.getString(R.string.zipperStyle_new_detail)))
    list.add(MainMenu(context.getString(R.string.row),R.drawable.row_icon,context.getString(R.string.row_style_new_detail)))
    list.add(MainMenu(context.getString(R.string.background),R.drawable.wallpaper_icon,context.getString(R.string.wallpaper_new_detail)))
    list.add(MainMenu(context.getString(R.string.preview),R.drawable.preview_icon,context.getString(R.string.preview_new_detail)))
    list.add(MainMenu(context.getString(R.string.customize),R.drawable.customize_icon,context.getString(R.string.customize_detail)))
    list.add(MainMenu(context.getString(R.string.wallpapers),R.drawable.wallpaper_icon,context.getString(R.string.k_wallpaper_new)))
    return list
}

fun getRemainingTimeUntilMidnight(): Long {
    val now = Calendar.getInstance()
    val midnight = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
        add(Calendar.DAY_OF_YEAR, 1) // Move to the next day
    }
    return midnight.timeInMillis - now.timeInMillis // Remaining time in milliseconds
}

fun startCountdownTimer(remainingTime: Long, findViewById: TextView)  {
    object : CountDownTimer(remainingTime, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            val hours = (millisUntilFinished / (1000 * 60 * 60)) % 24
            val minutes = (millisUntilFinished / (1000 * 60)) % 60
            val seconds = (millisUntilFinished / 1000) % 60

            // Update UI with the remaining time
            findViewById.text= "$hours:$minutes:$seconds"
        }

        override fun onFinish() {
            // Handle the countdown finishing (e.g., reset, trigger an event)
            // Optionally, restart the countdown for the next day
            findViewById.text= "It's midnight!"
            val newRemainingTime = getRemainingTimeUntilMidnight() // Recalculate for the next day
            startCountdownTimer(newRemainingTime, findViewById) // Start the new countdown
        }
    }.start()
}

fun introDetailTextNew(context: Context): ArrayList<String>
{
    val list = arrayListOf<String>()
    list.add(context.getString(R.string.wallpaper_new))
    list.add(context.getString(R.string.zipperStyle_new))
    list.add(context.getString(R.string.row_style_new))
    list.add(context.getString(R.string.preview_new))
    return list
}


fun Fragment.languageData(): ArrayList<LanguageModel> {
    val list = arrayListOf<LanguageModel>()
    list.add(LanguageModel(getString(R.string.english), "en", R.drawable.uk, false))
    list.add(LanguageModel(getString(R.string.Arabic), "ar", R.drawable.arabic, false))
    list.add(LanguageModel( getString(R.string.bangladash), "bag", R.drawable.bangladash, false))
    list.add(LanguageModel(getString(R.string.brazil), "br", R.drawable.brazil, false))
    list.add(LanguageModel(getString(R.string.canada), "ca", R.drawable.canada, false))
    list.add(LanguageModel( getString(R.string.domican_republic), "rom", R.drawable.domican_republic, false))
    list.add(LanguageModel(getString(R.string.france), "fr", R.drawable.france, false))
    list.add(LanguageModel(getString(R.string.german), "de", R.drawable.germany, false))
    list.add(LanguageModel(getString(R.string.hindi), "hi", R.drawable.hindi, false))
    list.add(LanguageModel(getString(R.string.italian), "it", R.drawable.italian, false))
    list.add(LanguageModel(getString(R.string.japanese), "ja", R.drawable.japanese, false))
    list.add(LanguageModel( getString(R.string.keynia), "ky", R.drawable.keynia, false))
    list.add(LanguageModel(getString(R.string.korean), "ko", R.drawable.korean, false))
    list.add(LanguageModel( getString(R.string.mexicon), "mk", R.drawable.mexicon, false))
    list.add(LanguageModel(getString(R.string.netherland), "nl", R.drawable.netherland, false))
    list.add(LanguageModel( getString(R.string.portuguese), "pt", R.drawable.portuguese, false ))
    list.add(LanguageModel(getString(R.string.russian), "ru", R.drawable.russian, false))
    list.add(LanguageModel(getString(R.string.spanish), "es", R.drawable.spanish, false))
    list.add(LanguageModel( getString(R.string.africa), "af", R.drawable.africa, false))
    list.add(LanguageModel(getString(R.string.turkey), "tr", R.drawable.turkey, false))
    list.add(LanguageModel( getString(R.string.urdu), "ur", R.drawable.urdu, false))

    return list
}


/*@SuppressLint("InflateParams")
fun Fragment.showExitDialog() {
    val bottomSheetDialog = BottomSheetDialog(requireContext())
    val view = layoutInflater.inflate(R.layout.exit_bottom_dialog, null)
    bottomSheetDialog.setContentView(view)

    bottomSheetDialog.window?.setBackgroundDrawableResource(R.color.transparent)
//    view.setBackgroundResource(R.drawable.rect_white_exit_bottom)
    view.findViewById<ConstraintLayout>(R.id.main_lay)
        .setBackgroundResource(R.drawable.rect_white_exit_bottom)


    val btnExit = view.findViewById<TextView>(R.id.yes)
    val btnCancel = view.findViewById<TextView>(R.id.no)

    btnExit.setOnClickListener {
        // Perform exit action
        // For example, calling finish() for the activity
        requireActivity().finish()
    }

    btnCancel.setOnClickListener {
        bottomSheetDialog.dismiss()
    }

    bottomSheetDialog.show()
}*/

fun Fragment.setupBackPressedCallback(
    onBackPressedAction: () -> Unit,
) {
    requireActivity().onBackPressedDispatcher.addCallback(
        viewLifecycleOwner,
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                onBackPressedAction.invoke()
            }
        })
}


fun Context.shareApp() {
    val intentShare = Intent()
    intentShare.action = "android.intent.action.SEND"
    intentShare.putExtra(
        "android.intent.extra.TEXT", """
     Heart Zipper app Download at: 
     https://play.google.com/store/apps/details?id=$packageName
     """.trimIndent()
    )
    intentShare.type = "text/plain"
    try {
        startActivity(Intent.createChooser(intentShare, "Share via"))
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun Context.moreApp() {

    try {
        startActivity(
            Intent(
                "android.intent.action.VIEW",
                Uri.parse("https://play.google.com/store/apps/developer?id=Gold+Screen+Lock+%26+Zipper+Lock+Screen&hl=en")
            )
        )
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun Context.privacyPolicy() {
    try {
        startActivity(
            Intent(
                "android.intent.action.VIEW",
                Uri.parse("http://sites.google.com/view/goldscreenlockzipperlock/heartzipperlock")
            )
        )
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun Context.rateUs() {
    val i = Intent(Intent.ACTION_VIEW)
    i.data = Uri.parse("market://details?id=" + this.packageName)
    try {
        startActivity(i)
    } catch (e: ActivityNotFoundException) {
        e.printStackTrace()
    }
}

fun View.clickWithThrottle(throttleTime: Long = 100L, action: () -> Unit) {
    this.setOnClickListener(object : View.OnClickListener {
        private var lastClickTime: Long = 0

        override fun onClick(v: View) {
            if (SystemClock.elapsedRealtime() - lastClickTime < throttleTime) return
            else action()

            lastClickTime = SystemClock.elapsedRealtime()
        }
    })
}

val toast: Toast? = null

fun showToast(context: Context, msg: String) {
    toast?.cancel()
    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
}

fun Context.setLocale(languageCode: String) {
    val locale = Locale(languageCode)
    Locale.setDefault(locale)
    val resources: Resources = resources
    val configuration: Configuration = resources.configuration
    configuration.setLocale(locale)
    resources.updateConfiguration(configuration, resources.displayMetrics)
}

fun Fragment.setLocaleMain(languageCode: String) {
    val locale = Locale(languageCode)
    Locale.setDefault(locale)
    val resources: Resources = resources
    val configuration: Configuration = resources.configuration
    configuration.setLocale(locale)
    resources.updateConfiguration(configuration, resources.displayMetrics)
}

var ratingDialog: AlertDialog? = null
fun showRatingDialog(
    context: Activity?,
    onPositiveButtonClick: (Float, AlertDialog) -> Unit,
) {

    val dialogView = context?.layoutInflater?.inflate(R.layout.rating_dialog, null)
    ratingDialog = AlertDialog.Builder(context ?: return).create()
    ratingDialog?.setView(dialogView)
    ratingDialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
    val ratingBar = dialogView?.findViewById<RatingBar>(R.id.ratingBar)
    val rateUs = dialogView?.findViewById<TextView>(R.id.rate_us)
    val rateUsNo = dialogView?.findViewById<TextView>(R.id.rate_us_no)

    ratingBar?.setOnRatingBarChangeListener { it, fl, b ->
        if (b) {
            if (fl >= 1F && fl < 3F) {
                ratingDialog?.dismiss()
                showToast(context, context.getString(R.string.thanks_txt))
            } else if (fl in 3F..5F) {
                ratingDialog?.dismiss()
                context.rateUs()
            }

        }
    }
    rateUs?.setOnClickListener {
        ratingDialog?.dismiss()
    }
    rateUsNo?.setOnClickListener {
        ratingDialog?.dismiss()
    }
    ratingDialog?.show()

}

var ratingService: AlertDialog? = null
fun Fragment.showServiceDialog(
    onPositiveYesClick: () -> Unit,
    onPositiveNoClick: () -> Unit,
) {
    val dialogView = layoutInflater.inflate(R.layout.service_dialog, null)
    ratingService = AlertDialog.Builder(requireContext()).create()
    ratingService?.setView(dialogView)
    ratingService?.window?.setBackgroundDrawableResource(android.R.color.transparent)
    val no = dialogView.findViewById<Button>(R.id.cancl_btn)
    val yes = dialogView.findViewById<Button>(R.id.cnfrm_del_btn)
    yes.setOnClickListener {
        if (isVisible && isAdded && !isDetached) {
            ratingService?.dismiss()
        }
        onPositiveYesClick()
    }

    no.setOnClickListener {
        if (isVisible && isAdded && !isDetached) {
            ratingService?.dismiss()
        }
        onPositiveNoClick()
    }

    if (isVisible && isAdded && !isDetached) {
        ratingService?.show()
    }

}


fun showSpeedDialogNew(
    context: Activity,
    onPositiveYesClick: (Int) -> Unit,
    onPositiveNoClick: () -> Unit,
) {
    val dialogView = context.layoutInflater.inflate(R.layout.speed_chooser_layout, null)
    ratingService = AlertDialog.Builder(context).create()
    ratingService?.setView(dialogView)
    ratingService?.window?.setBackgroundDrawableResource(android.R.color.transparent)
    val no = dialogView?.findViewById<Button>(R.id.close)
    val yes = dialogView?.findViewById<Button>(R.id.done)
    val speed = dialogView?.findViewById<SeekBar>(R.id.seekBar)
    speed?.progress = LoadPref(SpeedActivePref, context)
    yes?.setOnClickListener {
        ratingService?.dismiss()
        onPositiveYesClick(speed?.progress ?: return@setOnClickListener)
    }
    no?.setOnClickListener {
        ratingService?.dismiss()
        onPositiveNoClick()
    }
    ratingService?.show()
}

fun showNotificationServiceDialog(
    context: Activity,
    onPositiveYesClick: () -> Unit,
    onPositiveNoClick: () -> Unit,
) {
    val dialogView = context.layoutInflater.inflate(R.layout.notification_service_dialog, null)
    ratingService = AlertDialog.Builder(context).create()
    ratingService?.setView(dialogView)
    ratingService?.window?.setBackgroundDrawableResource(android.R.color.transparent)
    val no = dialogView.findViewById<TextView>(R.id.cancl_btn)
    val yes = dialogView.findViewById<TextView>(R.id.cnfrm_del_btn)
    yes.setOnClickListener {
        ratingService?.dismiss()
        onPositiveYesClick()
    }

    no.setOnClickListener {
        ratingService?.dismiss()
        onPositiveNoClick()
    }

    ratingService?.show()

}


fun requestCameraPermissionNotification(context: Activity) {
    ActivityCompat.requestPermissions(
        context, arrayOf(NOTIFICATION_PERMISSION), 2
    )
}

fun Activity.setStatusBar() {
    val nightModeFlags: Int =
        this.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
    when (nightModeFlags) {
        Configuration.UI_MODE_NIGHT_YES -> {
            window.decorView.windowInsetsController?.setSystemBarsAppearance(
                0, WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )
        }

        Configuration.UI_MODE_NIGHT_NO -> window.decorView.windowInsetsController?.setSystemBarsAppearance(
            WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
            WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
        )

        Configuration.UI_MODE_NIGHT_UNDEFINED -> window.decorView.windowInsetsController?.setSystemBarsAppearance(
            WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
            WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
        )
    }
}

fun Activity.setDarkMode(isDarkMode: Boolean) {
    if (isDarkMode) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
    else AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
}

fun ImageView.loadImage(context: Context?, resourceId: Int) {
    Glide.with(context ?: return).load(resourceId).into(this)
}

fun ImageView.loadImagethumbnail(context: Context?, resourceId: Int) {
    Glide.with(context ?: return).load(resourceId).thumbnail().into(this)
}

/*fun MainAppFragment.autoServiceFunction(isStart: Boolean) {
    sharedPrefUtils?.setBroadCast(IS_NOTIFICATION, isStart)
    if (isStart) {
        ContextCompat.startForegroundService(
            context ?: return,
            Intent(
                context ?: return,
                MainService::class.java
            )
        )
    } else {
        context?.stopService(
            Intent(
                context ?: return,
                MainService::class.java
            )
        )
    }

}*/

/*fun FragmentDetailModule.autoServiceFunctionInternalModule(
    isStart: Boolean,
    active: String?,
) {
    sharedPrefUtils?.setBroadCast(active, isStart)
    if (isStart) {
        sharedPrefUtils?.setBroadCast(IS_NOTIFICATION, true)
        ContextCompat.startForegroundService(
            context ?: return,
            Intent(
                context ?: return,
                MainService::class.java
            )
        )
    }

}*/



fun firebaseAnalytics(Item_id: String, Item_name: String) {
    try {
        val firebaseAnalytics = Firebase.analytics

        val bundle = Bundle().apply {
            //        putString(FirebaseAnalytics.Param.ITEM_ID, Item_id)
            putString(FirebaseAnalytics.Param.ITEM_NAME, Item_name)
        }
        firebaseAnalytics.logEvent(Item_id, bundle)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun checkNotificationPermission(context: Activity?) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
        return
    }
    if (context?.let { ContextCompat.checkSelfPermission(it, NOTIFICATION_PERMISSION) } != 0) {
        requestCameraPermissionNotification(context ?: return)
    }

}

fun showCameraPhoneServiceDialog(
    context: Activity,
    onPositiveYesClick: () -> Unit,
    onPositiveNoClick: () -> Unit,
) {
    val dialogView = context.layoutInflater.inflate(R.layout.notification_service_dialog, null)
    ratingService = AlertDialog.Builder(context).create()
    ratingService?.setView(dialogView)
    ratingService?.window?.setBackgroundDrawableResource(android.R.color.transparent)
    val title = dialogView?.findViewById<TextView>(R.id.textView11)
    val titleDetail = dialogView?.findViewById<TextView>(R.id.textView12)
    val no = dialogView?.findViewById<Button>(R.id.cancl_btn)
    val yes = dialogView?.findViewById<Button>(R.id.cnfrm_del_btn)
    title?.setText(R.string.permission_needed)
    titleDetail?.setText(R.string.camera_permission)
    yes?.setOnClickListener {
        ratingService?.dismiss()
        onPositiveYesClick()
    }

    no?.setOnClickListener {
        ratingService?.dismiss()
        onPositiveNoClick()
    }

    ratingService?.show()

}

fun soundData(context: Activity): ArrayList<SoundModel> {
    val list = arrayListOf<SoundModel>()
    list.add(SoundModel(context.getString(R.string.sound), R.raw.unzip, false))
    list.add(SoundModel(context.getString(R.string.sound), R.raw.unzip1, false))
    list.add(SoundModel(context.getString(R.string.sound), R.raw.unzip2, false))
    list.add(SoundModel(context.getString(R.string.sound), R.raw.unzip3, false))
    list.add(SoundModel(context.getString(R.string.sound), R.raw.unzip5, false))
    list.add(SoundModel(context.getString(R.string.sound), R.raw.unzip6, false))
    list.add(SoundModel(context.getString(R.string.sound), R.raw.unzip7, false))
    return list
}

fun ClosedRange<Int>.random(): Int {
    return Random.nextInt(start, endInclusive + 1)
}

fun generateRandomNumberInRange(min: Int, max: Int): Int {
    return run {
        // Use the Kotlin extension function for generating random numbers
        (min..max).random()
    }
}


fun containsMultipleSpaces(str: String): Boolean {
    return str.contains("\\s{2,}".toRegex())
}

fun containsLeadingTrailingSpaces(str: String): Boolean {
    return str.startsWith(" ") || str.endsWith(" ") || str.endsWith("  ") || str.endsWith("   ") || str.endsWith(
        "    "
    ) || str.endsWith("     ") || str.endsWith("     ") || str.endsWith("     ")
}

fun Context.getAppVersion(): String {
    return try {
        val packageInfo = packageManager.getPackageInfo(packageName, 0)
        packageInfo.versionCode.toString()
    } catch (e: Exception) {
        e.printStackTrace()
        "Unknown"
    }
}

fun askRatings(context: Activity) {
    try {
        var isGMSAvailable = false
        isRating=false
        val result = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context)
        isGMSAvailable = ConnectionResult.SUCCESS == result
        if (isGMSAvailable) {
            val manager = ReviewManagerFactory.create(context)
            val request = manager.requestReviewFlow()
            request.addOnCompleteListener { task: Task<ReviewInfo?> ->
                try {
                    if (task.isSuccessful) {
                        // getting ReviewInfo object
                        val reviewInfo = task.result
                        val flow =
                            manager.launchReviewFlow(
                                context,
                                reviewInfo!!
                            )
                        flow.addOnCompleteListener { task2: Task<Void?>? -> }
                    } else {
                        // There was some problem, continue regardless of the result
                        // call old method for rating and user will land in Play Store App page
                        context.rateUs()
                    }
                } catch (ex: java.lang.Exception) {
                  ex.printStackTrace()
                }
            }
        } else {
            // if user has not installed Google play services in his/her device you land them to
            // specific store e.g. Huawei AppGallery or Samsung Galaxy Store
            context.rateUs()
        }
    } catch (e: Exception) {
       e.printStackTrace()
    }
}

fun getRandomColor(): String {
    // List of color codes as strings (you can add more)
    val colors = listOf(
        "#F48A1D",
        "#EACF2A",
        "#0AA350",
        "#44A0E3",
        "#6E340E",
        "#D80B8E",
        "#6B3499"
    )

    // Randomly select and return a color
    return colors.random()
}

fun isAppInForeground(context: Context): Boolean {
    val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    val appProcesses = activityManager.runningAppProcesses ?: return false
    for (appProcess in appProcesses) {
        if (appProcess.processName == context.packageName && appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
            return true
        }
    }
    return false
}

fun Int.toSimpleTimeFormat(): String {
    val minutes = this / 60
    val seconds = this % 60
    return "$minutes:${seconds.toString().padStart(2, '0')}"
}

fun getImageLanguage(position: String): Int {
    when (position) {
        "en" -> {
            return R.drawable.uk
        }

        "ar" -> {
            return R.drawable.arabic
        }

        "bag" -> {
            return R.drawable.bangladash
        }

        "br" -> {
            return R.drawable.brazil
        }

        "ca" -> {
            return R.drawable.canada
        }

        "rom" -> {
            return R.drawable.domican_republic
        }
        "fr" -> {
            return R.drawable.france
        }

        "de" -> {
            return R.drawable.germany
        }

        "hi" -> {
            return R.drawable.hindi
        }

        "it" -> {
            return R.drawable.italian
        }

        "ja" -> {
            return R.drawable.japanese
        }
        "ky" -> {
            return R.drawable.keynia
        }

        "ko" -> {
            return R.drawable.korean
        }

        "mk" -> {
            return R.drawable.mexicon
        }

        "nl" -> {
            return R.drawable.netherland
        }

        "pt" -> {
            return R.drawable.portuguese
        }

        "ru" -> {
            return R.drawable.russian
        }
        "es" -> {
            return R.drawable.spanish
        }

        "af" -> {
            return R.drawable.africa
        }

        "tr" -> {
            return R.drawable.turkey
        }

        "ur" -> {
            return R.drawable.urdu
        }

    }
    return R.drawable.uk
}

fun getNativeLayout(position: Int, layout: FrameLayout, context: Context): Int {
    Log.d("check_layout", "getNativeLayout: $position")
    when (position) {
        1 -> {
            layout.minimumHeight = convertDpToPixel(50f, context).toInt()
            return R.layout.layout_native_80
        }

        2 -> {
            layout.minimumHeight = convertDpToPixel(100F, context).toInt()
            return R.layout.layout_native_140
        }

        3 -> {
            layout.minimumHeight = convertDpToPixel(120F, context).toInt()
            return R.layout.layout_native_176
        }

        4 -> {
            layout.minimumHeight = convertDpToPixel(150F, context).toInt()
            return R.layout.native_layout_190
        }

        5 -> {
            layout.minimumHeight = convertDpToPixel(150F, context).toInt()
            return R.layout.native_layout_276
        }

        6 -> {
            layout.minimumHeight = convertDpToPixel(150F, context).toInt()
            return R.layout.layout_native_260
        }
    }
    layout.minimumHeight = convertDpToPixel(50f, context).toInt()
    return R.layout.layout_native_80
}

fun convertDpToPixel(valueDp: Float, context: Context): Float {
    val displayMetrics = context.resources.displayMetrics
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, valueDp,
        displayMetrics
    )
}