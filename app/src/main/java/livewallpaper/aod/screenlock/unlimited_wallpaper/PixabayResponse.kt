package livewallpaper.aod.screenlock.unlimited_wallpaper

data class PixabayResponse(
    val total: Int,
    val totalHits: Int,
    val hits: List<ImageData>
)

data class ImageData(
    val id: Int,
    val previewURL: String,
    val largeImageURL: String,
    val user: String
)
