package com.example.breify20.worker

import android.content.Context
import android.util.Log
import androidx.work.*
import java.util.concurrent.TimeUnit
object WorkManagerHelper {
    fun restartSync(context: Context) {
        WorkManager.getInstance(context)
            .cancelUniqueWork("email_sync_worker")
        WorkManager.getInstance(context)
            .cancelUniqueWork("token_refresh_worker")
        Log.d("WORKER","RESTARTED")

        scheduleEmailSync(context)
        scheduleTokenRefresh(context)
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
                .setInitialDelay(1, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .build()
        WorkManager.getInstance(context)
            .enqueueUniquePeriodicWork(
                "token_refresh_worker",
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
        Log.d("WORKER", "TOKEN REFRESH SCHEDULED")
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
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }
}