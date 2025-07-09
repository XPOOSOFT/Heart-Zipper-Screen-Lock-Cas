package livewallpaper.aod.screenlock.ads_manager

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
            .addTestDeviceHashedId("4482FD1C9B039A733B15F74B5244B5E4")
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
                Log.e("cmpConsent", "form load error ${it.message}")
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
            Log.e("cmpConsent", "form load error ${it.message}")
        })
    }

    fun checkStatus() {
        Log.e("cmpConsent", "status ${consentInformation.consentStatus}")
        if (consentInformation.consentStatus == ConsentInformation.ConsentStatus.OBTAINED) {
            Log.e("cmpConsent", "cmp consent obtained ")
            consentListener?.invoke(TCFString())

        } else if (consentInformation.consentStatus == ConsentInformation.ConsentStatus.NOT_REQUIRED) {
            Log.e("cmpConsent", "cmp consent not required")
            consentListener?.invoke(true)
        }
    }

    fun TCFString(): Boolean {
        val sharePrefrences = PreferenceManager.getDefaultSharedPreferences(activity)
        val tcfString = sharePrefrences.getString("IABTCF_TCString", "null") ?: "null"
        val tcString = TCString.decode(tcfString)

        return if (tcString.specialFeatureOptIns.isEmpty && tcString.vendorConsent.isEmpty) {
            Log.e("cmpConsent", "ump no consent")
            false
        } else {
            Log.e("cmpConsent", "ump consent")
            true
        }

    }

}