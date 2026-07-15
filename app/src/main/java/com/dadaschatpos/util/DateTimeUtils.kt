package com.dadaschatpos.util

import com.dadaschatpos.data.model.ReportFilter
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateTimeUtils {
    fun rangeFor(filter: ReportFilter): Pair<Long, Long> {
        val calendar = Calendar.getInstance()
        val end = endOfToday(calendar).timeInMillis
        val start = when (filter) {
            ReportFilter.TODAY -> startOfToday(Calendar.getInstance()).timeInMillis
            ReportFilter.WEEKLY -> startOfToday(Calendar.getInstance()).apply {
                add(Calendar.DAY_OF_YEAR, -6)
            }.timeInMillis
            ReportFilter.MONTHLY -> startOfToday(Calendar.getInstance()).apply {
                set(Calendar.DAY_OF_MONTH, 1)
            }.timeInMillis
        }
        return start to end
    }

    fun formatDate(timestamp: Long): String = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(timestamp))

    fun formatTime(timestamp: Long): String = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(timestamp))

    fun formatDateTime(timestamp: Long): String = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(timestamp))

    private fun startOfToday(calendar: Calendar): Calendar = calendar.apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    private fun endOfToday(calendar: Calendar): Calendar = calendar.apply {
        set(Calendar.HOUR_OF_DAY, 23)
        set(Calendar.MINUTE, 59)
        set(Calendar.SECOND, 59)
        set(Calendar.MILLISECOND, 999)
    }
}
