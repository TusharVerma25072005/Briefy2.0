package com.example.breify20.worker

import android.content.Context
import androidx.work.*
import java.util.concurrent.TimeUnit

object WorkManagerHelper {
    fun restartSync(context: Context) {
        WorkManager.getInstance(context)
            .cancelUniqueWork("email_sync_worker")
        WorkManager.getInstance(context)
            .cancelUniqueWork("token_refresh_worker")
        WorkManager.getInstance(context)
            .cancelUniqueWork("summary_fetch_worker")
        scheduleEmailSync(context)
        scheduleTokenRefresh(context)
        scheduleSummaryFetch(context)
    }
    fun scheduleTokenRefresh(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val request =
            PeriodicWorkRequestBuilder<TokenRefreshWorker>(
                15,
                TimeUnit.MINUTES
            )
                .setInitialDelay(30, TimeUnit.SECONDS)
                .setConstraints(constraints)
                .build()
        WorkManager.getInstance(context)
            .enqueueUniquePeriodicWork(
                "token_refresh_worker",
                ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
                request
            )
    }

    fun scheduleEmailSync(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val request =
            PeriodicWorkRequestBuilder<EmailFetchWorker>(
                15,
                TimeUnit.MINUTES
            )
                .setConstraints(constraints)
                .build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "email_sync_worker",
            ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
            request
        )
    }

    fun scheduleSummaryFetch(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val request =
            PeriodicWorkRequestBuilder<SummaryFetchWorker>(
                15,
                TimeUnit.MINUTES
            )
                .setInitialDelay(2 , TimeUnit.MINUTES)
                .setConstraints(constraints)
                .build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "summary_fetch_worker",
            ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
            request
        )
    }
}