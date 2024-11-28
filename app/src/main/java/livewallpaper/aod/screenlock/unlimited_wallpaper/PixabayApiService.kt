package livewallpaper.aod.screenlock.unlimited_wallpaper

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface PixabayApiService {
    @GET("api/")
    fun searchImages(
        @Query("key") apiKey: String,
        @Query("q") query: String,
        @Query("image_type") imageType: String = "photo",
        @Query("pretty") pretty: Boolean = true
    ): Call<PixabayResponse>
}
