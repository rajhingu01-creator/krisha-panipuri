package com.dadaschatpos.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.dadaschatpos.util.NotificationHelper

class DailySummaryWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        NotificationHelper.showDailySalesSummary(applicationContext)
        return Result.success()
    }
}
