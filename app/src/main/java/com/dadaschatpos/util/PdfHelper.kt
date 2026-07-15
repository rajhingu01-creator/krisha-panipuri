package com.dadaschatpos.util

import android.content.Context
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import androidx.core.content.FileProvider
import com.dadaschatpos.data.model.OrderWithItems
import com.dadaschatpos.data.model.UserEntity
import java.io.File
import java.io.FileOutputStream

object PdfHelper {
    fun createReceiptPdf(context: Context, user: UserEntity?, receipt: OrderWithItems): Uri {
        val width = 420
        val height = 360 + (receipt.items.size * 32)
        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(width, height, 1).create()
        val page = document.startPage(pageInfo)
        val canvas = page.canvas
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            textSize = 14f
            color = android.graphics.Color.BLACK
        }
        var y = 34f

        fun drawCenter(text: String, size: Float = 14f, bold: Boolean = false) {
            paint.textAlign = Paint.Align.CENTER
            paint.textSize = size
            paint.typeface = if (bold) Typeface.DEFAULT_BOLD else Typeface.DEFAULT
            canvas.drawText(text, width / 2f, y, paint)
            y += size + 10f
        }

        fun drawLeft(text: String, size: Float = 14f, bold: Boolean = false) {
            paint.textAlign = Paint.Align.LEFT
            paint.textSize = size
            paint.typeface = if (bold) Typeface.DEFAULT_BOLD else Typeface.DEFAULT
            canvas.drawText(text, 24f, y, paint)
            y += size + 8f
        }

        fun drawRight(text: String, size: Float = 14f, bold: Boolean = false) {
            paint.textAlign = Paint.Align.RIGHT
            paint.textSize = size
            paint.typeface = if (bold) Typeface.DEFAULT_BOLD else Typeface.DEFAULT
            canvas.drawText(text, width - 24f, y, paint)
        }

        fun separator() {
            paint.strokeWidth = 1f
            canvas.drawLine(24f, y, width - 24f, y, paint)
            y += 18f
        }

        drawCenter(user?.shopName ?: "DADA'S CHAT POS", 20f, true)
        drawCenter(user?.address ?: "Main Road, Your City", 12f)
        drawCenter("Mobile: ${user?.mobile ?: "+91 98765 43210"}", 12f)
        separator()
        drawLeft("Bill No: #${receipt.order.id}", bold = true)
        drawLeft("Date: ${DateTimeUtils.formatDate(receipt.order.date)}")
        drawLeft("Time: ${DateTimeUtils.formatTime(receipt.order.date)}")
        separator()
        drawLeft("Item", bold = true)
        drawRight("Amount", bold = true)
        y += 4f
        receipt.items.forEach { item ->
            drawLeft("${item.itemName} x ${item.qty}")
            drawRight(CurrencyFormatter.format(item.amount))
            y += 4f
        }
        separator()
        drawLeft("Total", 18f, true)
        drawRight(CurrencyFormatter.format(receipt.order.total), 18f, true)
        y += 26f
        drawCenter("Thank you! Visit Again", 13f, true)

        document.finishPage(page)
        val file = File(context.cacheDir, "receipt_${receipt.order.id}.pdf")
        FileOutputStream(file).use { output -> document.writeTo(output) }
        document.close()
        return FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
    }
}
