package com.dadaschatpos.util

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

object BackupFileHelper {
    fun writeJsonToCache(context: Context, json: String): Uri {
        val file = File(context.cacheDir, "dadas_chat_pos_backup_${System.currentTimeMillis()}.json")
        file.writeText(json)
        return FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
    }

    fun readText(context: Context, uri: Uri): String {
        return context.contentResolver.openInputStream(uri)?.bufferedReader().use { reader ->
            reader?.readText() ?: error("Unable to read backup file")
        }
    }
}
