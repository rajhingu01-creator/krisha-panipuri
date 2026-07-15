package com.dadaschatpos.util

import android.util.Patterns

object Validators {
    fun isEmail(value: String): Boolean = Patterns.EMAIL_ADDRESS.matcher(value.trim()).matches()

    fun isPassword(value: String): Boolean = value.length >= 6

    fun isMobile(value: String): Boolean = value.trim().matches(Regex("^[6-9]\\d{9}$"))

    fun isRequired(value: String): Boolean = value.trim().isNotEmpty()
}
