package com.dadaschatpos.util

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.dadaschatpos.worker.DailySummaryWorker
import java.util.concurrent.TimeUnit

object NotificationScheduler {
    private const val WORK_NAME = "daily_sales_summary_worker"

    fun scheduleDailySalesSummary(context: Context) {
        val request = PeriodicWorkRequestBuilder<DailySummaryWorker>(1, TimeUnit.DAYS)
            .build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }
}
