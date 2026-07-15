package com.dadaschatpos.util

import java.security.MessageDigest

object PasswordHasher {
    fun hash(value: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(value.toByteArray(Charsets.UTF_8))
        return bytes.joinToString(separator = "") { byte -> "%02x".format(byte.toInt() and 0xff) }
    }
}
