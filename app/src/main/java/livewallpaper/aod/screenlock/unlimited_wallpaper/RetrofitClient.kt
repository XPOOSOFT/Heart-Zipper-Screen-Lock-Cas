package livewallpaper.aod.screenlock.unlimited_wallpaper

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type

object RetrofitClient {
    private const val BASE_URL = "https://pixabay.com/"
    val type: Type = object : TypeToken<Response<PixabayResponse>>() {}.type

    val retrofit = Retrofit.Builder()
        .baseUrl("https://pixabay.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService = retrofit.create(PixabayApiService::class.java)

}