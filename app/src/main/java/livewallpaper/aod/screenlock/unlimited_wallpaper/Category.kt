package livewallpaper.aod.screenlock.unlimited_wallpaper

data class Category(
    val name: String?,
    val thumbnil_image_url: String?,
    val qurey: String?,
    val sort_id: Int?
)

data class CategoriesResponse(
    val categories: List<Category>
)
