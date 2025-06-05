package livewallpaper.aod.screenlock.unlimited_wallpaper

import android.app.WallpaperManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import livewallpaper.aod.screenlock.zipper.databinding.FragmentFullscreenImage1Binding


class ImageDetailFragment : Fragment() {

    private var _binding: FragmentFullscreenImage1Binding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentFullscreenImage1Binding.inflate(layoutInflater)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val imageUrl = arguments?.getString("image_url")
        try {
            if (imageUrl != null) {
                // Show the progress bar and load the image
                Picasso.get().load(imageUrl).into(_binding?.fullscreenImageView, object : Callback {
                    override fun onSuccess() {
                        // Hide progress bar and show image
                        _binding?.loadingIndicator?.visibility = View.GONE
                        _binding?.fullscreenImageView?.visibility = View.VISIBLE
                        _binding?.applyWallpaperButton?.visibility = View.VISIBLE
                    }

                    override fun onError(e: Exception?) {
                        // Hide progress bar and show error
                        _binding?.loadingIndicator?.visibility = View.GONE
                        Toast.makeText(context, "Failed to load image", Toast.LENGTH_SHORT).show()
                    }
                })
            } else {
                Toast.makeText(context, "Image URL is missing", Toast.LENGTH_SHORT).show()
            }
        }catch (e :Exception){
            e.printStackTrace()
        }

        _binding?.applyWallpaperButton?.setOnClickListener {
            try {
                if (imageUrl != null) {
                    setAsWallpaper(imageUrl)
                }
            } catch (e: Exception) {
              e.printStackTrace()
            }
        }

        _binding?.backIcon?.setOnClickListener {
           findNavController().navigateUp()
        }
    }

    private fun setAsWallpaper(imageUrl: String) {
        Picasso.get().load(imageUrl).into(object : com.squareup.picasso.Target {
            override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                try {
                    val wallpaperManager = WallpaperManager.getInstance(context)
                    wallpaperManager.setBitmap(bitmap)
                    Toast.makeText(context, "Wallpaper applied!", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                }
            }

            override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                Toast.makeText(context, "Failed to load image", Toast.LENGTH_SHORT).show()
            }

            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                // No-op
            }
        })
    }
}

