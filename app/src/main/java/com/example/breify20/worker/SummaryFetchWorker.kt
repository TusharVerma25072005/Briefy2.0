package com.example.breify20.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.breify20.Notification.NotificationHelper
import com.example.breify20.data.SecurePrefs
import com.example.breify20.data.local.DatabaseProvider
import com.example.breify20.model.email.SummariesRequest
import com.example.breify20.network.RetrofitClient
import com.example.breify20.ui.screens.EmailPriority

class SummaryFetchWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {


    override suspend fun doWork(): Result {
        return try {
            Log.d("SummaryWorker", "Fetching summaries")
            val prefs = SecurePrefs.getPrefs(applicationContext)
            val token = prefs.getString("accessToken", "") ?: ""
            if (token.isEmpty()) {
                Log.d("SummaryWorker", "No access token found, cannot fetch summaries")
                return Result.retry()
            }
            val emailDao = DatabaseProvider.getDatabase(applicationContext).emailDao()
            val emailIds = emailDao.getEmailsWithEmptySummary()
            if (emailIds.isEmpty()) {
                Log.d("SummaryWorker", "No emails with empty summaries found")
                return Result.success()
            }else{
                val summariesRequest = emailIds.map {
                    SummariesRequest(it)
                }
                Log.d("EmailIDS for summary", summariesRequest.toString())
                val res = RetrofitClient.authApi.getSummaries(summariesRequest)
                //here
                Log.d("SUMMARIES FETCH WORKER" , res.toString())
                res.forEach { email ->
                    emailDao.updateEmailSummary(
                        emailId = email.emailId,
                        summary = email.summary,

                        priority = email.priority,
                        embedding = email.embedding,
                        category = email.category,
                        detailedSummary = email.detailedSummary
                    )
                    if (email.priority == EmailPriority.URGENT.name) {
                        NotificationHelper.showHighPriorityNotification(
                            applicationContext,
                            email.summary
                        )
                    }
                }
                return Result.success()
            }
            Log.d("SummaryWorker", "Failed to fetch summaries, retrying")
            Result.retry()

        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }
}