package com.dadaschatpos.util

import java.util.Locale
import kotlin.math.roundToInt

object CurrencyFormatter {
    fun format(amount: Double): String {
        return if (amount % 1.0 == 0.0) {
            "₹${amount.roundToInt()}"
        } else {
            "₹${String.format(Locale("en", "IN"), "%.2f", amount)}"
        }
    }
}
