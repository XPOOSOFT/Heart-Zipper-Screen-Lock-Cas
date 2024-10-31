package livewallpaper.aod.screenlock.lib.Media

import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import livewallpaper.aod.screenlock.GlideBitmapFactory
import livewallpaper.aod.screenlock.lib.UgameLib.GameAdapters.GameAdapter
import livewallpaper.aod.screenlock.lib.UgameLib.Resource.Resources
import livewallpaper.aod.screenlock.zipper.R
import livewallpaper.aod.screenlock.zipper.service.LockScreenService
import livewallpaper.aod.screenlock.zipper.utilities.AppAdapter
import livewallpaper.aod.screenlock.zipper.utilities.ConstantValues.SelectRow
import livewallpaper.aod.screenlock.zipper.utilities.ConstantValues.SelectZipper
import livewallpaper.aod.screenlock.zipper.utilities.Constants.getRows
import livewallpaper.aod.screenlock.zipper.utilities.Constants.getWallpapers
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
                ChainLeft = GlideBitmapFactory.decodeResource(
                    LockScreenService.cc?.resources,
                    getRows()?.get(LoadPref) ?: return@let
                )
                ChainRight = GlideBitmapFactory.decodeResource(
                    LockScreenService.cc?.resources,
                    getRows()?.get(LoadPref) ?: return@let
                )
                ChainRight = rotate(ChainRight?:return, 180)
                zipper = GlideBitmapFactory.decodeResource(
                    LockScreenService.cc?.resources,
                    getZippersMedia()[LoadPref2]
                )
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
                wallpapers = getWallpapers()
                SelectedBg =
                    Resources.CreateBitmap(
                        wallpapers?.get(selectedWallpaperNumber) ?: return,
                        LockScreenService.cc
                    )
                val selectedWallpaperBgNumber =
                    AppAdapter.getSelectedWallpaperBgNumber(GameAdapter.ctx)
                wallpapers?.add(0, Integer.valueOf(R.drawable.transparent))
                SelectedBackBg = GlideBitmapFactory.decodeResource(
                    LockScreenService.cc?.resources,
                    wallpapers?.get(selectedWallpaperBgNumber + -1)?:return
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
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