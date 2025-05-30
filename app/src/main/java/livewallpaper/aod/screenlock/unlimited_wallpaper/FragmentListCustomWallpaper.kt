package livewallpaper.aod.screenlock.unlimited_wallpaper

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch
import livewallpaper.aod.screenlock.unlimited_wallpaper.RetrofitClient.apiService
import livewallpaper.aod.screenlock.zipper.R
import livewallpaper.aod.screenlock.zipper.databinding.ActivityMainBinding
import livewallpaper.aod.screenlock.zipper.utilities.apiKey
import livewallpaper.aod.screenlock.zipper.utilities.clickWithThrottle
import livewallpaper.aod.screenlock.zipper.utilities.firebaseAnalytics

class FragmentListCustomWallpaper : Fragment() {

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

        firebaseAnalytics(
            "custom_wallpaper_fragment_detail",
            "custom_wallpaper_fragment_detail -->  Click"
        )
        val textTile = arguments?.getString("title")
        if (textTile != null) {
            fetchImages(textTile)
        }
        _binding?.topLay?.title?.text = textTile

        _binding?.topLay?.backBtn?.clickWithThrottle {
            findNavController().navigateUp()
        }
    }

    private fun fetchImages(query: String) {
        try {
//            RetrofitClient.apiService.searchImages(apiKey, query).enqueue(object : Callback<PixabayResponse> {
//                    override fun onResponse(
//                        call: Call<PixabayResponse>,
//                        response: Response<PixabayResponse>
//                    ) {
//                        if (response.isSuccessful) {
//                            val images = response.body()?.hits ?: emptyList()
//                            _binding?.recyclerView?.adapter = ImageAdapter(images) { image ->
//                                findNavController().navigate(
//                                    R.id.ImageDetailFragment,
//                                    bundleOf("image_url" to image.largeImageURL)
//                                )
//                            }
//                        } else {
//                            Log.d( "onFailure:", response.message())
//                        }
//                    }
//                    override fun onFailure(call: Call<PixabayResponse>, t: Throwable) {
//                        Log.d( "onFailure:", "${t.message}")
//                    }
//                })
            lifecycleScope.launch {
                try {
                    val response = apiService.searchImages(apiKey, query)
                    if (response.isSuccessful) {
                        // Handle the response
                        val images = response.body()?.hits
                        _binding?.recyclerView?.adapter =
                            images?.let {
                                ImageAdapter(it) { image ->
                                    findNavController().navigate(
                                        R.id.ImageDetailFragment,
                                        bundleOf("image_url" to image.largeImageURL)
                                    )
                                }
                            }
                    } else {
                        // Handle error
                        Log.d("onFailure:", response.message())
                    }
                } catch (e: Exception) {
                    // Handle exception (e.g., network error)
                    Log.d("onFailure:", "$e")
                }
            }
        } catch (e: Exception) {
            Log.d("onFailure:", "${e.message}")
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.clear()
    }

}
