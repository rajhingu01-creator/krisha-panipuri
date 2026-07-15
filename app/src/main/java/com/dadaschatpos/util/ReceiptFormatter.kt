package com.dadaschatpos.util

import com.dadaschatpos.data.model.OrderWithItems
import com.dadaschatpos.data.model.UserEntity

object ReceiptFormatter {
    fun format(user: UserEntity?, receipt: OrderWithItems): String = buildString {
        appendLine(user?.shopName ?: "DADA'S CHAT POS")
        appendLine(user?.address ?: "Main Road, Your City")
        appendLine("Mobile: ${user?.mobile ?: "+91 98765 43210"}")
        appendLine("--------------------------------")
        appendLine("Bill No: #${receipt.order.id}")
        appendLine("Date: ${DateTimeUtils.formatDate(receipt.order.date)}")
        appendLine("Time: ${DateTimeUtils.formatTime(receipt.order.date)}")
        appendLine("--------------------------------")
        appendLine("Item              Qty  Amt")
        receipt.items.forEach { item ->
            val name = item.itemName.take(16).padEnd(16)
            val qty = item.qty.toString().padStart(3)
            val amount = CurrencyFormatter.format(item.amount).padStart(6)
            appendLine("$name $qty $amount")
        }
        appendLine("--------------------------------")
        appendLine("Total: ${CurrencyFormatter.format(receipt.order.total)}")
        appendLine("Thank you! Visit Again")
    }
}
