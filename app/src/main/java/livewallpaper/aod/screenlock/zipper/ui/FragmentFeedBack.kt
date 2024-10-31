package livewallpaper.aod.screenlock.zipper.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import livewallpaper.aod.screenlock.zipper.databinding.FragmentFeedBackBinding
import livewallpaper.aod.screenlock.zipper.utilities.BaseFragment
import livewallpaper.aod.screenlock.zipper.utilities.clickWithThrottle
import livewallpaper.aod.screenlock.zipper.utilities.setupBackPressedCallback
import livewallpaper.aod.screenlock.zipper.utilities.showToast

class FragmentFeedBack : BaseFragment<FragmentFeedBackBinding>(FragmentFeedBackBinding::inflate) {

    private val PERMISSION_REQUEST_CODE = 101
    private val PICK_IMAGE_REQUEST_CODE = 102
    private var imageUri: Uri? = null
    private fun checkStoragePermission(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(
                activity ?: return false, Manifest.permission.READ_MEDIA_IMAGES
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            return ContextCompat.checkSelfPermission(
                activity ?: return false, Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestStoragePermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                activity ?: return,
                arrayOf(Manifest.permission.READ_MEDIA_IMAGES),
                PERMISSION_REQUEST_CODE
            )
        } else {
            ActivityCompat.requestPermissions(
                activity ?: return,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                PERMISSION_REQUEST_CODE
            )
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding?.sendFeedBack?.setOnClickListener {
            if (imageUri != null && _binding?.editTextText?.text?.toString()?.isEmpty() == false) {
                shareContent(
                    title = "Bugs Find Alert",
                    text = "Bugs Find By User.Need to Fixed" + "\n+${_binding?.editTextText?.text?.toString()} "+"\n ${getStringFeedBack()}",
                    imageResId = imageUri!! // Replace with your image
                )
            } else {
                showToast(context ?: return@setOnClickListener, "Empty Field")
            }
        }

        _binding?.uploadTitleFrame?.setOnClickListener {
            if (checkStoragePermission()) {
                openGallery()
            } else {
                requestStoragePermission()
            }
        }
        _binding?.backBtn?.clickWithThrottle {
            findNavController().navigateUp()
        }
        setupBackPressedCallback {
            findNavController().navigateUp()
        }
    }

    private fun getStringFeedBack(): String {
        var text1 = ""
        if (_binding?.checkBox1?.isChecked == true) {
            text1 = text1 + "\n" + _binding?.checkBox1?.text.toString()
        }
        if (_binding?.checkBox2?.isChecked == true) {
            text1 = text1 + "\n" + _binding?.checkBox2?.text.toString()
        }
        if (_binding?.checkBox3?.isChecked == true) {
            text1 = text1 + "\n" + _binding?.checkBox3?.text.toString()
        }
        if (_binding?.checkBox4?.isChecked == true) {
            text1 = text1 + "\n" + _binding?.checkBox4?.text.toString()
        }
        if (_binding?.checkBox5?.isChecked == true) {
            text1 = text1 + "\n" + _binding?.checkBox5?.text.toString()
        }
        if (_binding?.checkBox6?.isChecked == true) {
            text1 = text1 + "\n" + _binding?.checkBox6?.text.toString()
        }
        if (_binding?.checkBox7?.isChecked == true) {
            text1 = text1 + "\n" + _binding?.checkBox7?.text.toString()
        }
        return text1
    }

    private fun shareContent(title: String, text: String, imageResId: Uri) {
        // Create the sharing intent
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "*/*" // General type; can be customized based on what you're sharing
            // Add the text and title
            putExtra(Intent.EXTRA_EMAIL, arrayOf("fireitinc.dev@gmail.com"))
            putExtra(Intent.EXTRA_SUBJECT, title)
            putExtra(Intent.EXTRA_TEXT, text)
            // Add the image (optional)
            imageResId.let {
                putExtra(Intent.EXTRA_STREAM, it)
                type = "image/*" // Specify the MIME type for images
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) // Ensure permission for sharing the image
            }
        }
        // Launch the chooser for sharing
        val chooser = Intent.createChooser(shareIntent, "Share via")
        startActivity(chooser)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            imageUri = data.data

            // Load image into the TextView background
            Glide.with(this).load(imageUri).into(object :
                    com.bumptech.glide.request.target.CustomTarget<android.graphics.drawable.Drawable>() {
                    override fun onResourceReady(
                        resource: android.graphics.drawable.Drawable,
                        transition: com.bumptech.glide.request.transition.Transition<in android.graphics.drawable.Drawable>?
                    ) {
                        _binding?.uploadTitleFrame?.background = resource
                    }

                    override fun onLoadCleared(placeholder: android.graphics.drawable.Drawable?) {
                        _binding?.uploadTitleFrame?.background = placeholder
                    }
                })
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST_CODE)
    }
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}