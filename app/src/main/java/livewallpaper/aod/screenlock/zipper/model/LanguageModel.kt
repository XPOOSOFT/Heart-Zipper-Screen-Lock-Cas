package livewallpaper.aod.screenlock.zipper.model

import livewallpaper.aod.screenlock.zipper.R


data class LanguageModel(
    var country_name: String,
    var country_code: String,
    var country_flag: Int = R.drawable.splash_icon,
    var check: Boolean,
)