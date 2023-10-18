package com.practicum.playlistmaker.sharing.data.impl

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.practicum.playlistmaker.sharing.domain.ExternalNavigator
import com.practicum.playlistmaker.sharing.domain.model.EmailData

class ExternalNavigatorImpl(private val context: Context): ExternalNavigator {
    override fun shareLink(link: String) {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, link)
            type = "text/plain"
        }
        shareIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(shareIntent)
    }

    override fun openLink(link: String) {
        val agreementIntent = Intent(Intent.ACTION_VIEW)
        agreementIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        agreementIntent.data = Uri.parse(link)
        context.startActivity(agreementIntent)
    }

    override fun openEmail(email: EmailData) {
        val supportIntent = Intent(Intent.ACTION_SENDTO)

        supportIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

        supportIntent.data = Uri.parse(
            email.mailTo +
                    email.extraTopic +
                    email.extraText
        )

        val chooserIntent = Intent.createChooser(supportIntent, email.extraTopic)
        chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        context.startActivity(chooserIntent)
    }

}