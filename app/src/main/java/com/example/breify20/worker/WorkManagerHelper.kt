package com.example.breify20.worker

import android.content.Context
import android.util.Log
import androidx.work.*
import java.util.concurrent.TimeUnit
object WorkManagerHelper {
    fun runImmediateSync(context: Context) {

        val request =
            OneTimeWorkRequestBuilder< EmailFetchWorker>()
                .build()

        WorkManager
            .getInstance(context)
            .enqueue(request)
    }
    fun cancelEmailSync(context: Context) {
        WorkManager
            .getInstance(context)
            .cancelUniqueWork("email_sync_worker")
    }
    fun restartEmailSync(context: Context) {

        WorkManager.getInstance(context)
            .cancelUniqueWork("email_sync_worker")
        Log.d("WORKER","RESTARTED")
        scheduleEmailSync(context)

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