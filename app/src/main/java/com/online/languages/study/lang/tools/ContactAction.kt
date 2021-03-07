package com.online.languages.study.lang.tools

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.core.app.ShareCompat
import com.online.languages.study.lang.BuildConfig
import com.online.languages.study.lang.R
import com.online.languages.study.lang.adapters.RateDialog

class ContactAction(val context: Context) {

    fun sendEmail(recepientEmail: String, mailSubject: String, mailBody: String) {
        val i = Intent(Intent.ACTION_SENDTO)
        val mailto = "mailto:" + recepientEmail +
                "?subject=" + Uri.encode(mailSubject) +
                "&body=" + Uri.encode(mailBody)
        i.data = Uri.parse(mailto)
        try {
            context.startActivity(Intent.createChooser(i, context.getString(R.string.msg_sending_mail)))
        } catch (ex: ActivityNotFoundException) {
            Toast.makeText(context, R.string.msg_no_mail_client, Toast.LENGTH_SHORT).show()
        }
    }


    fun goToPlayStore(pack: String) {
        val uri = Uri.parse("market://details?id=$pack")
        val goToMarket = Intent(Intent.ACTION_VIEW, uri)

        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK)

        try {
            context.startActivity(goToMarket)
        } catch (e: ActivityNotFoundException) {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(context.getString(R.string.google_play_address) + pack)))
        }
    }


    fun share(activity: Activity) {

        val pack: String = context.getString(R.string.app_market_link)
        val text: String = context.getString(R.string.share_advise_msg) + context.getString(R.string.google_play_address) + pack

        share(text, activity)

    }

    fun share(text: String, activity: Activity) {
        ShareCompat.IntentBuilder.from(activity)
                .setType("text/plain")
                .setChooserTitle(R.string.share_chooser_title)
                .setText(text)
                .startChooser()
    }


    fun rate() {
        val rateDialog = RateDialog(context)
        rateDialog.createDialog()
    }

    private fun openWeb(url: String) {

        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse(url)
        try {
            context.startActivity(i)
        } catch (ex: ActivityNotFoundException) {
            Toast.makeText(context, R.string.msg_no_browser, Toast.LENGTH_SHORT).show()
        }
    }

    fun getAppVersionName() : String {

        val checkPlusVersion = CheckPlusVersion(context)

        var versionName = BuildConfig.VERSION_NAME

        if (checkPlusVersion.isPlusVersion()) versionName += "+";

        val abr = context.getString(R.string.msg_version_abr)

        return context.getString(R.string.msg_version_name, abr, versionName)
    }

}