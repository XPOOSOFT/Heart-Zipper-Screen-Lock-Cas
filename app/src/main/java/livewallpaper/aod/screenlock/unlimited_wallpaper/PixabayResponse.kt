package livewallpaper.aod.screenlock.unlimited_wallpaper

import com.google.gson.annotations.SerializedName

data class PixabayResponse(
    @SerializedName("total") val total: Int,
    @SerializedName("totalHits") val totalHits: Int,
    @SerializedName("hits") val hits: List<ImageData>
)

