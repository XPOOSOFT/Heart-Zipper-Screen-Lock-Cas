package livewallpaper.aod.screenlock.zipper.ads_manager

import android.app.Activity
import android.preference.PreferenceManager
import android.util.Log
import com.google.android.ump.ConsentDebugSettings
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform
import com.iabtcf.decoder.TCString
import livewallpaper.aod.screenlock.zipper.ui.SplashFragment.Companion.consentListener

class CmpClass(val activity: Activity) {

    private lateinit var consentInformation: ConsentInformation


    fun initilaizeCMP() {
        val debugSettings = ConsentDebugSettings.Builder(activity)
            .addTestDeviceHashedId("05E5BE681AD5F24502796F98C8ACB518")
            .build()
        val params = ConsentRequestParameters.Builder()
            .setConsentDebugSettings(debugSettings)
            .build()

        consentInformation = UserMessagingPlatform.getConsentInformation(activity)

        consentInformation.requestConsentInfoUpdate(
            activity,
            params, {
                if (consentInformation.isConsentFormAvailable) {
                    loadCMPForm()
                } else {
                    checkStatus()
                }
            }, {
            }
        )
    }

    fun loadCMPForm() {
        UserMessagingPlatform.loadConsentForm(activity, {
            if (consentInformation.consentStatus == ConsentInformation.ConsentStatus.REQUIRED) {
                it.show(activity) {
                    loadCMPForm()
                }
            } else {
                checkStatus()
            }
        }, {
        })
    }

    fun checkStatus() {
        if (consentInformation.consentStatus == ConsentInformation.ConsentStatus.OBTAINED) {
            consentListener?.invoke(TCFString())

        } else if (consentInformation.consentStatus == ConsentInformation.ConsentStatus.NOT_REQUIRED) {
            consentListener?.invoke(true)
        }
    }

    fun TCFString(): Boolean {
        val sharePrefrences = PreferenceManager.getDefaultSharedPreferences(activity)
        val tcfString = sharePrefrences.getString("IABTCF_TCString", "null") ?: "null"
        val tcString = TCString.decode(tcfString)

        return if (tcString.specialFeatureOptIns.isEmpty && tcString.vendorConsent.isEmpty) {
            false
        } else {
            true
        }

    }

}