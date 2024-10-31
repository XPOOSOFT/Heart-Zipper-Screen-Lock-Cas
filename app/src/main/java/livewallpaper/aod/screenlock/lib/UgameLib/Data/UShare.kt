package livewallpaper.aod.screenlock.lib.UgameLib.Data

import android.content.Context
import android.content.Intent
import android.net.Uri
import java.io.File
import java.util.Locale

object UShare {
    fun Share(context: Context, str: String?, str2: String?) {
        val intent = Intent()
        intent.action = "android.intent.action.SEND"
        intent.type = "text/plain"
        intent.putExtra("android.intent.extra.TEXT", str2)
        context.startActivity(Intent.createChooser(intent, str))
    }

    fun ShareByMail(context: Context, str: String?, str2: String?, str3: String) {
        val intent = Intent()
        intent.action = "android.intent.action.SEND"
        intent.type = "message/rfc822"
        intent.putExtra("android.intent.extra.EMAIL", arrayOf(str3))
        intent.putExtra("android.intent.extra.SUBJECT", str)
        intent.putExtra("android.intent.extra.TEXT", str2)
        context.startActivity(intent)
    }

    fun ShareImage(context: Context, str: String) {
        val endsWith = str.lowercase(Locale.getDefault()).endsWith(".png")
        val intent = Intent("android.intent.action.SEND")
        if (endsWith) {
            intent.type = "image/png"
        } else {
            intent.type = "image/jpeg"
        }
        intent.putExtra("android.intent.extra.STREAM", Uri.fromFile(File(str)))
        context.startActivity(Intent.createChooser(intent, "Share via"))
    }

    fun OpenAppFromStore(context: Context, str: String) {
        val intent = Intent("android.intent.action.VIEW")
        intent.data = Uri.parse("market://details?id=$str")
        context.startActivity(intent)
    }

    fun OpenDevloperApps(context: Context, str: String) {
        val intent = Intent("android.intent.action.VIEW")
        intent.data = Uri.parse("market://search?q=pub:$str")
        context.startActivity(intent)
    }
}