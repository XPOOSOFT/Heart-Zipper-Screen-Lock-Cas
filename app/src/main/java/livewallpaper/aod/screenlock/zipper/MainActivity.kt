package livewallpaper.aod.screenlock.zipper

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import livewallpaper.aod.screenlock.reward.RewardPreferences
import livewallpaper.aod.screenlock.zipper.databinding.MainActivityApplicationBinding
import livewallpaper.aod.screenlock.zipper.utilities.generateRandomNumberInRange
import livewallpaper.aod.screenlock.zipper.utilities.getImage
import livewallpaper.aod.screenlock.zipper.utilities.isSplash

class MainActivity : AppCompatActivity() {

    companion object{
        var background : Int = R.drawable.main_bg
    }
    private var binding: MainActivityApplicationBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isSplash =false
        binding = MainActivityApplicationBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        background=getImage(generateRandomNumberInRange(0,5))
//      setStatusBar()
//        val config: UXConfig = UXConfig.Builder("fxxc29oeie8g0rl")
//            .enableAutomaticScreenNameTagging(true)
//            .enableImprovedScreenCapture(true)
//            .build()
//        UXCam.startWithConfiguration(config)
        // Check if intent has the update value when activity is first created
        intent?.let {
            handleIntent(it)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        // Handle the new intent if activity is already open
        intent?.let {
            handleIntent(it)
        }
    }

    private fun handleIntent(intent: Intent) {
        // Retrieve the extra from the intent
        val updateValue = intent.getStringExtra("KEY_UPDATE_VALUE")
        if (updateValue != null) {
            // Update the value in your application as needed
            updateYourValue(updateValue)
        }
    }

    private fun updateYourValue(value: String) {
        // Logic to update your application value
        println("Updated value: $value")
        if(value == "Reward"){
            RewardPreferences(applicationContext).incrementUnlockDay(applicationContext)
        }
        // Update UI or perform other operations based on the updated value
    }


    override fun attachBaseContext(newBase: Context?) {

        val newOverride = Configuration(newBase?.resources?.configuration)
        newOverride.fontScale = 1.0f
        applyOverrideConfiguration(newOverride)

        super.attachBaseContext(newBase)
    }
}
