package livewallpaper.aod.screenlock.unlimited_wallpaper

import com.google.gson.annotations.SerializedName

data class ImageData(
    @SerializedName("id") val id: Int,
    @SerializedName("pageURL") val pageURL: String,
    @SerializedName("type") val type: String,
    @SerializedName("tags") val tags: String = "flower, stamens, hypericum",
    @SerializedName("previewURL") val previewURL: String = "https://cdn.pixabay.com/photo/2022/12/26/13/50/flower-7679117_150.jpg",
    @SerializedName("previewWidth") val previewWidth: Int = 150,
    @SerializedName("previewHeight") val previewHeight: Int = 100,
    @SerializedName("webformatURL") val webformatURL: String = "https://pixabay.com/get/gb7515147084178c1050307fd9eac49b41975328f6f030920bcffb9e9bb50ae6980d3b520248fd28704dad8a50e4bfd8c7f5b2fa32d05a9847b17568b98a588a9_640.jpg",
    @SerializedName("webformatWidth") val webformatWidth: Int = 640,
    @SerializedName("webformatHeight") val webformatHeight: Int = 427,
    @SerializedName("largeImageURL") val largeImageURL: String = "https://pixabay.com/get/g31214432f6d30b83571f95fb8f76bb6dade45bf6d300f90ab6fde213f2ee2971ee7f843549785e064ae209059110ab08ed2a3c8df112a673531611f294164d93_1280.jpg",
    @SerializedName("imageWidth") val imageWidth: Int = 6000,
    @SerializedName("imageHeight") val imageHeight: Int = 4000,
    @SerializedName("imageSize") val imageSize: Int = 8137356,
    @SerializedName("views") val views: Int = 25282,
    @SerializedName("downloads") val downloads: Int = 19309,
    @SerializedName("collections") val collections: Int = 830,
    @SerializedName("likes") val likes: Int = 122,
    @SerializedName("comments") val comments: Int = 20,
    @SerializedName("user_id") val user_id: Int = 4379051,
    @SerializedName("user") val user: String = "Alfred_Grupstra",
    @SerializedName("userImageURL") val userImageURL: String = "https://cdn.pixabay.com/user/2024/08/25/22-05-08-334_250x250.jpg"
)


