package livewallpaper.aod.screenlock.lib.Media

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import livewallpaper.aod.screenlock.GlideBitmapFactory
import livewallpaper.aod.screenlock.lib.UgameLib.GameAdapters.GameAdapter
import livewallpaper.aod.screenlock.lib.UgameLib.Resource.Resources
import livewallpaper.aod.screenlock.zipper.R
import livewallpaper.aod.screenlock.zipper.service.LockScreenService
import livewallpaper.aod.screenlock.zipper.utilities.AppAdapter
import livewallpaper.aod.screenlock.zipper.utilities.ConstantValues.SelectRow
import livewallpaper.aod.screenlock.zipper.utilities.ConstantValues.SelectZipper
import livewallpaper.aod.screenlock.zipper.utilities.Constants.getBackground
import livewallpaper.aod.screenlock.zipper.utilities.Constants.getRows
import livewallpaper.aod.screenlock.zipper.utilities.Constants.getWallpapers
import livewallpaper.aod.screenlock.zipper.utilities.Constants.getZippers
import livewallpaper.aod.screenlock.zipper.utilities.Constants.getZippersMedia
import livewallpaper.aod.screenlock.zipper.utilities.DataBasePref

object Media {
    private var selectedWallpaperNumber: Int = 0
    private var wallpapers: MutableList<Int>? = null
    private var LoadPref: Int = 0
    private var LoadPref2: Int = 0

    var ChainLeft: Bitmap? = null
    var ChainRight: Bitmap? = null
    var Initialed = false
    var SelectedBackBg: Bitmap? = null
    var SelectedBg: Bitmap? = null


    var list: MutableList<Bitmap?>? = ArrayList()
    var zipper: Bitmap? = null

    fun inicial() {
        CoroutineScope(Dispatchers.IO).let {
            try {
                if (Initialed) {
                    LoadBgData()
                    return@let
                }
                Initialed = true
                if (list == null) {
                    list = ArrayList()
                }
                LoadPref = LockScreenService.cc?.let { DataBasePref.LoadPref(SelectRow, it) }?:return
                LoadPref2 = LockScreenService.cc?.let { DataBasePref.LoadPref(SelectZipper, it) }?:return
                val options = BitmapFactory.Options().apply {
                    inJustDecodeBounds = true // Only decode the bounds, not the bitmap itself
                }

                LockScreenService.cc?.resources?.openRawResource(getRows()?.get(LoadPref) ?: return@let)?.use { inputStream ->
                    BitmapFactory.decodeStream(inputStream, null, options)
                }
                options.inJustDecodeBounds = false // Now decode the actual bitmap
                ChainLeft = LockScreenService.cc?.resources?.openRawResource(
                    getRows()?.get(LoadPref) ?: return@let)?.use { inputStream ->
                    BitmapFactory.decodeStream(inputStream, null, options)
                }
                zipper = LockScreenService.cc?.resources?.openRawResource(getZippers()[LoadPref] ?: return@let)?.use { inputStream ->
                    BitmapFactory.decodeStream(inputStream, null, options)
                }
                ChainRight = LockScreenService.cc?.resources?.openRawResource(
                    getRows()?.get(LoadPref) ?: return@let)?.use { inputStream ->
                    BitmapFactory.decodeStream(inputStream, null, options)
                }
                ChainRight = rotate(ChainRight?:return, 180)
                LoadBgData()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun rotate(bitmap: Bitmap?, i: Int): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(i.toFloat())
        val createScaledBitmap = Bitmap.createScaledBitmap(
            bitmap!!, bitmap.width, bitmap.height, true
        )
        return Bitmap.createBitmap(
            createScaledBitmap,
            0,
            0,
            createScaledBitmap.width,
            createScaledBitmap.height,
            matrix,
            true
        )
    }

        private fun LoadBgData() {
            try {
                selectedWallpaperNumber = AppAdapter.getSelectedWallpaperNumber(GameAdapter.ctx)
                wallpapers = getBackground()
                SelectedBg =
                    Resources.CreateBitmap(
                        wallpapers?.get(selectedWallpaperNumber) ?: return,
                        LockScreenService.cc
                    )
                wallpapers?.add(0, R.drawable.transparent) // No need for Integer.valueOf()
                val selectedBackgroundNumber = AppAdapter.getSelectedWallpaperBgNumber(GameAdapter.ctx) - 1
                val selectedBackgroundResource = wallpapers?.get(selectedBackgroundNumber) ?: return
                SelectedBackBg = GlideBitmapFactory.decodeResource(
                    LockScreenService.cc?.resources,
                    selectedBackgroundResource
                )?.let { resizeBitmap(it, LockScreenService.cc, 800, 600) } // Adjust size as needed

            } catch (e: Exception) {
                e.printStackTrace()
                Clear()
            }
        }

    private fun resizeBitmap(bitmap: Bitmap, context: Context?, targetWidth: Int, targetHeight: Int): Bitmap {
        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, true)
        return scaledBitmap
    }

    fun Clear() {
        if (list != null) {
            list?.clear()
        }
        list = null
        SelectedBg = null
        zipper = null
        ChainLeft = null
        ChainRight = null
        Initialed = false
        SelectedBackBg = null
    }

}