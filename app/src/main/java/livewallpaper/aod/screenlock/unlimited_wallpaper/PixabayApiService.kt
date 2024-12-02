package livewallpaper.aod.screenlock.unlimited_wallpaper

import retrofit2.http.GET
import retrofit2.http.Query

interface PixabayApiService {
    @GET("api/")
    suspend fun searchImages(
        @Query("key") apiKey: String,
        @Query("q") query: String,
        @Query("image_type") imageType: String = "photo"
    ): retrofit2.Response<PixabayResponse>
}

