package com.dadaschatpos.util

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

object ShareHelper {
    fun sharePdf(context: Context, uri: Uri) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Share bill PDF"))
    }

    fun shareTextOnWhatsApp(context: Context, text: String) {
        val whatsappIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            setPackage("com.whatsapp")
            putExtra(Intent.EXTRA_TEXT, text)
        }
        try {
            context.startActivity(whatsappIntent)
        } catch (_: ActivityNotFoundException) {
            val chooser = Intent.createChooser(
                Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, text)
                },
                "Share bill"
            )
            Toast.makeText(context, "WhatsApp not installed. Choose another app.", Toast.LENGTH_SHORT).show()
            context.startActivity(chooser)
        }
    }

    fun shareJsonBackup(context: Context, uri: Uri) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/json"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Share JSON backup"))
    }
}
