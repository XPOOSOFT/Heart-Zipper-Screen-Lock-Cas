package livewallpaper.aod.screenlock.reward.wallpaper
import android.app.WallpaperManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import livewallpaper.aod.screenlock.zipper.R
import livewallpaper.aod.screenlock.zipper.utilities.clickWithThrottle
import livewallpaper.aod.screenlock.zipper.utilities.setupBackPressedCallback

class FullScreenImageFragment : Fragment() {

    private lateinit var fullscreenImageView: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_fullscreen_image_1, container, false)
        fullscreenImageView = view.findViewById(R.id.fullscreenImageView)
        try {
            val applyWallpaperButton = view.findViewById<TextView>(R.id.applyWallpaperButton)
            val imageUrl = arguments?.getString("imageUrl") ?: return view
            Glide.with(this).load(imageUrl).into(fullscreenImageView)
            applyWallpaperButton.setOnClickListener {
                applyWallpaper(imageUrl)
            }
        } catch (e: Exception) {
           e.printStackTrace()
        }
        view.findViewById<ImageView>(R.id.backIcon).clickWithThrottle {
            findNavController().navigateUp()
        }
        setupBackPressedCallback {
            findNavController().navigateUp()
        }
        return view
    }

    private fun applyWallpaper(imageUrl: String) {
        Glide.with(this)
            .asBitmap()
            .load(imageUrl)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    val wallpaperManager = WallpaperManager.getInstance(requireContext())
                    wallpaperManager.setBitmap(resource)
                    Toast.makeText(requireContext(), "Wallpaper applied!", Toast.LENGTH_SHORT).show()
                }

                override fun onLoadCleared(placeholder: Drawable?) {}
            })
    }

}
