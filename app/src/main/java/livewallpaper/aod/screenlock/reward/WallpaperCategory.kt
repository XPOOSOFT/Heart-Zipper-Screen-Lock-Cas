package livewallpaper.aod.screenlock.reward

data class WallpaperCategory(
    val name: String,
    var nameDay: Int,
    var locked: Boolean = false,
    val saveTime: Long
)