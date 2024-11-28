package livewallpaper.aod.screenlock.zipper.zip_custom

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import livewallpaper.aod.screenlock.zipper.R
import livewallpaper.aod.screenlock.zipper.utilities.Constants.getWallpapers

class MainViewModel : ViewModel() {

    private val _zippers = MutableLiveData<List<Int>>().apply { value = getZippers() }
    val zippers: LiveData<List<Int>> = _zippers

    private val _rowsView = MutableLiveData<List<Int>>().apply { value = getRowsView() }
    val rowsView: LiveData<List<Int>> = _rowsView

    private val _wallpapers = MutableLiveData<List<Int>>().apply { value = getWallpapers() }
    val wallpapers: LiveData<List<Int>> = _wallpapers

    private fun getZippers(): List<Int> {
        return listOf(R.drawable.zipper1, R.drawable.zipper2, R.drawable.zipper3,R.drawable.zipper4, R.drawable.zipper5, R.drawable.zipper6, R.drawable.zipper7, R.drawable.zipper8)
    }

    private fun getRowsView(): List<Int> {
        return listOf(R.drawable.row_view_1, R.drawable.row_view_2, R.drawable.row_view_3,R.drawable.row_view_4, R.drawable.row_view_5, R.drawable.row_view_6, R.drawable.row_view_7, R.drawable.row_view_7)
    }

}
