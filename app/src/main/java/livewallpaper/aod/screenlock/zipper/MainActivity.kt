package livewallpaper.aod.screenlock.zipper

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import livewallpaper.aod.screenlock.zipper.databinding.MainActivityApplicationBinding
import livewallpaper.aod.screenlock.zipper.utilities.generateRandomNumberInRange
import livewallpaper.aod.screenlock.zipper.utilities.isSplash

class MainActivity : AppCompatActivity() {

    private var binding: MainActivityApplicationBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isSplash =false
        binding = MainActivityApplicationBinding.inflate(layoutInflater)
        setContentView(binding?.root)
//      setStatusBar()
//        val config: UXConfig = UXConfig.Builder("fxxc29oeie8g0rl")
//            .enableAutomaticScreenNameTagging(true)
//            .enableImprovedScreenCapture(true)
//            .build()
//        UXCam.startWithConfiguration(config)
        // Check if intent has the update value when activity is first created

    }


    override fun attachBaseContext(newBase: Context?) {

        val newOverride = Configuration(newBase?.resources?.configuration)
        newOverride.fontScale = 1.0f
        applyOverrideConfiguration(newOverride)

        super.attachBaseContext(newBase)
    }
}
