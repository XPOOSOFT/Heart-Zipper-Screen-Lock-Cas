package livewallpaper.aod.screenlock.zipper.utilities

import android.text.InputFilter
import android.text.Spanned

// Define a custom input filter
class NoSpaceInputFilter : InputFilter {
    override fun filter(
        source: CharSequence?,
        start: Int,
        end: Int,
        dest: Spanned?,
        dstart: Int,
        dend: Int
    ): CharSequence? {
        // Iterate over the input characters
        for (i in start until end) {
            // Check if the character is a space
            if (Character.isWhitespace(source?.get(i) ?: ' ')) {
                // Return an empty string to reject the input
                return ""
            }
        }
        // Accept the input
        return null
    }
}