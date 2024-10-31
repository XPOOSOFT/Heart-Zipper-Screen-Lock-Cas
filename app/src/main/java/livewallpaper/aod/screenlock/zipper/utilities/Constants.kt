package livewallpaper.aod.screenlock.zipper.utilities

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import livewallpaper.aod.screenlock.zipper.R
import livewallpaper.aod.screenlock.zipper.service.LiveService
import livewallpaper.aod.screenlock.zipper.service.LockScreenService

object Constants {
    const val Apppurchase = "apppurchase"
    const val No_of_attempts = "No_of_attempts"
    const val SHARED_PREFS = "pref"
    const val Tone_Selected = "Tone_Selected"
    const val User_Value = "userpass"

    @SuppressLint("SuspiciousIndentation")
    fun getZippersMedia(): List<Int> {
        val arrayList: ArrayList<Int> = ArrayList()
            arrayList.add(R.drawable.zipper1)
            arrayList.add(R.drawable.zipper2)
            arrayList.add(R.drawable.zipper3)
            arrayList.add(R.drawable.zipper4)
            arrayList.add(R.drawable.zipper5)
            arrayList.add(R.drawable.zipper6)
            arrayList.add(R.drawable.zipper7)
            arrayList.add(R.drawable.zipper8)
        return arrayList
    }
    @SuppressLint("SuspiciousIndentation")
    fun getZippers(): List<Int> {
        val arrayList: ArrayList<Int> = ArrayList()
        CoroutineScope(Dispatchers.IO).launch {
            arrayList.add(R.drawable.zipper1)
            arrayList.add(R.drawable.zipper2)
            arrayList.add(R.drawable.zipper3)
            arrayList.add(R.drawable.zipper4)
            arrayList.add(R.drawable.zipper5)
            arrayList.add(R.drawable.zipper6)
            arrayList.add(R.drawable.zipper7)
            arrayList.add(R.drawable.zipper8)
        }
        return arrayList
    }

    fun getBackground(): List<Int> {
        val arrayList: ArrayList<Int> = ArrayList()
        CoroutineScope(Dispatchers.IO).launch {
            arrayList.add(R.drawable.image1)
            arrayList.add(R.drawable.image2)
            arrayList.add(R.drawable.image3)
            arrayList.add(R.drawable.image4)
            arrayList.add(R.drawable.image5)
            arrayList.add(R.drawable.image6)
            arrayList.add(R.drawable.image7)
            arrayList.add(R.drawable.image8)
            arrayList.add(R.drawable.image9)
            arrayList.add(R.drawable.image10)
            arrayList.add(R.drawable.image11)
            arrayList.add(R.drawable.image12)
            arrayList.add(R.drawable.image13)
            arrayList.add(R.drawable.image14)
            arrayList.add(R.drawable.image15)
            arrayList.add(R.drawable.image16)
            arrayList.add(R.drawable.image17)
            arrayList.add(R.drawable.image18)
            arrayList.add(R.drawable.image19)
            arrayList.add(R.drawable.image20)
            arrayList.add(R.drawable.image21)
            arrayList.add(R.drawable.image22)
            arrayList.add(R.drawable.image23)
            arrayList.add(R.drawable.image24)
            arrayList.add(R.drawable.image25)
            arrayList.add(R.drawable.image26)
            arrayList.add(R.drawable.image27)
            arrayList.add(R.drawable.image28)
            arrayList.add(R.drawable.image29)
            arrayList.add(R.drawable.image30)
        }
        return arrayList
    }

    fun getWallpapers(): MutableList<Int>? {
        val arrayList: ArrayList<Int> = ArrayList()
        arrayList.add(R.drawable.image1)
        arrayList.add(R.drawable.image2)
        arrayList.add(R.drawable.image3)
        arrayList.add(R.drawable.image4)
        arrayList.add(R.drawable.image5)
        arrayList.add(R.drawable.image6)
        arrayList.add(R.drawable.image7)
        arrayList.add(R.drawable.image8)
        arrayList.add(R.drawable.image9)
        arrayList.add(R.drawable.image10)
        arrayList.add(R.drawable.image11)
        arrayList.add(R.drawable.image12)
        arrayList.add(R.drawable.image13)
        arrayList.add(R.drawable.image14)
        arrayList.add(R.drawable.image15)
        arrayList.add(R.drawable.image16)
        arrayList.add(R.drawable.image17)
        arrayList.add(R.drawable.image18)
        arrayList.add(R.drawable.image19)
        arrayList.add(R.drawable.image20)
        arrayList.add(R.drawable.image21)
        arrayList.add(R.drawable.image22)
        arrayList.add(R.drawable.image23)
        arrayList.add(R.drawable.image24)
        arrayList.add(R.drawable.image25)
        arrayList.add(R.drawable.image26)
        arrayList.add(R.drawable.image27)
        arrayList.add(R.drawable.image28)
        arrayList.add(R.drawable.image29)
        arrayList.add(R.drawable.image30)
        return arrayList
    }

    fun getRowsView(): List<Int>? {
        val arrayList: ArrayList<Int> = ArrayList()
        CoroutineScope(Dispatchers.IO).launch {
            arrayList.add(R.drawable.row_view_1)
            arrayList.add(R.drawable.row_view_2)
            arrayList.add(R.drawable.row_view_3)
            arrayList.add(R.drawable.row_view_4)
            arrayList.add(R.drawable.row_view_5)
            arrayList.add(R.drawable.row_view_6)
            arrayList.add(R.drawable.row_view_7)
        }
        return arrayList
    }

    fun getRows(): List<Int>? {
        val arrayList: ArrayList<Int> = ArrayList()
        arrayList.add(R.drawable.row1)
        arrayList.add(R.drawable.row2)
        arrayList.add(R.drawable.row3)
        arrayList.add(R.drawable.row4)
        arrayList.add(R.drawable.row5)
        arrayList.add(R.drawable.row6)
        arrayList.add(R.drawable.row7)
        return arrayList
    }

    fun getRowsPreview(): List<Int>? {
        val arrayList: ArrayList<Int> = ArrayList()
        CoroutineScope(Dispatchers.IO).launch {
            arrayList.add(R.drawable.row_preview_1)
            arrayList.add(R.drawable.row_preview_2)
            arrayList.add(R.drawable.row_preview_3)
            arrayList.add(R.drawable.row_preview_4)
            arrayList.add(R.drawable.row_preview_5)
            arrayList.add(R.drawable.row_preview_6)
            arrayList.add(R.drawable.row_preview_7)
        }
        return arrayList
    }

    private const val DELAY_OFF_MILLIS = 3000L


    @JvmStatic
    fun isMainServiceRunning(context: Context): Boolean {
        try {
            for (runningServiceInfo in (context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager).getRunningServices(
                Int.MAX_VALUE
            )) {
                if (LiveService::class.java.name == runningServiceInfo.service.className) {
                    return true
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    @JvmStatic

    fun Fragment.isServiceRunning(): Boolean {
        try {
            for (runningServiceInfo in (context?.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager).getRunningServices(
                Int.MAX_VALUE
            )) {
                if (LiveService::class.java.name == runningServiceInfo.service.className) {
                    return true
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    fun shareApp(context: Context) {
        val intent = Intent()
        intent.action = "android.intent.action.SEND"
        //        intent.putExtra("android.intent.extra.TEXT", context.getString(R.string.heycheckoutthisappat) + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID);
        intent.type = "text/plain"
        context.startActivity(intent)
    }


    fun stopServiceCall(
        context: Activity,
    ) {
        context.stopService(Intent(context, LiveService::class.java))
    }

    fun loadSound(position: Int): Int {

        Log.e("LockScreenService_position", "initialWindow: $position")
        when (position) {
            0 -> {
                return R.raw.unzip
            }

            1 -> {
                return R.raw.unzip1
            }

            2 -> {
                return R.raw.unzip2
            }

            3 -> {
                return R.raw.unzip3
            }

            4 -> {
                return R.raw.unzip5
            }

            5 -> {
                return R.raw.unzip6
            }

            6 -> {
                return R.raw.unzip7
            }
        }
        return R.raw.unzip
    }

}