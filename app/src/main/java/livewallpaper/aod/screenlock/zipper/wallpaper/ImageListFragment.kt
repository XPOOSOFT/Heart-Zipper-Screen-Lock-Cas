package livewallpaper.aod.screenlock.zipper.wallpaper
// ImageListFragment.kt
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.utils.widget.ImageFilterView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import livewallpaper.aod.screenlock.zipper.R
import livewallpaper.aod.screenlock.zipper.utilities.clickWithThrottle
import livewallpaper.aod.screenlock.zipper.utilities.getImagesFromTitle
import livewallpaper.aod.screenlock.zipper.utilities.loadJSONFromAsset
import livewallpaper.aod.screenlock.zipper.utilities.parseWallpaperJson
import livewallpaper.aod.screenlock.zipper.utilities.setupBackPressedCallback

class ImageListFragment : Fragment() {

    private lateinit var imageRecyclerView: RecyclerView
    private lateinit var imageAdapter: ImageAdapter
    private var images: List<String> = listOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_image_list, container, false)

        // Get title from arguments
        val title = arguments?.getString("title") ?: ""
        view.findViewById<TextView>(R.id.title).text = title
        view.findViewById<ImageFilterView>(R.id.titleBack).clickWithThrottle {
            findNavController().navigateUp()
        }
        setupBackPressedCallback {
            findNavController().navigateUp()
        }
        images = getImagesFromTitle(loadJSONFromAsset(context?:requireContext(),"categories.json")?: "" ,title)
        Log.d("list_images", "onCreateView: ${loadJSONFromAsset(context?:requireContext(),"categories.json")?: ""}")
        Log.d("list_images", "onCreateView: $images")
        imageRecyclerView = view.findViewById(R.id.imageRecyclerView)
        imageAdapter = ImageAdapter(images) { imageUrl ->
            openFullScreen(imageUrl)
        }
        imageRecyclerView.adapter = imageAdapter

        return view
    }

    private fun openFullScreen(imageUrl: String) {
        findNavController().navigate(R.id.FullScreenImageFragment, bundleOf("imageUrl" to imageUrl))
    }

    companion object {
        fun newInstance(title: String, imageUrls: List<String>): ImageListFragment {
            val fragment = ImageListFragment()
            fragment.images = imageUrls
            val args = Bundle().apply {
                putString("title", title)
            }
            fragment.arguments = args
            return fragment
        }
    }
}
