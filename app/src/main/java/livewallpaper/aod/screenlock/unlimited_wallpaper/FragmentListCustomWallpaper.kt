package livewallpaper.aod.screenlock.unlimited_wallpaper

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import livewallpaper.aod.screenlock.zipper.R
import livewallpaper.aod.screenlock.zipper.databinding.ActivityMainBinding
import livewallpaper.aod.screenlock.zipper.utilities.firebaseAnalytics
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import livewallpaper.aod.screenlock.zipper.utilities.apiKey

class FragmentListCustomWallpaper  : Fragment() {

    private var _binding: ActivityMainBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = ActivityMainBinding.inflate(layoutInflater)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAnalytics("custom_wallpaper_fragment_detail", "custom_wallpaper_fragment_detail -->  Click")
        val textTile =arguments?.getString("title")
        if (textTile != null) {
            fetchImages(textTile)
        }
    }

    private fun fetchImages(query: String) {
        RetrofitClient.apiService.searchImages(apiKey, query)
            .enqueue(object : Callback<PixabayResponse> {
                override fun onResponse(
                    call: Call<PixabayResponse>,
                    response: Response<PixabayResponse>
                ) {
                    if (response.isSuccessful) {
                        val images = response.body()?.hits ?: emptyList()
                        _binding?.recyclerView?.adapter = ImageAdapter(images) { image ->
                            findNavController().navigate(
                                R.id.ImageDetailFragment,
                                bundleOf("image_url" to image.largeImageURL)
                            )
                        }
                    } else {
                        Toast.makeText(context, "Failed to load data", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<PixabayResponse>, t: Throwable) {
                    Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

}
