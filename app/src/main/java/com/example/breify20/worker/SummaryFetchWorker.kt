package com.example.breify20.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.breify20.Notification.NotificationHelper
import com.example.breify20.data.SecurePrefs
import com.example.breify20.data.convertStringToPairs
import com.example.breify20.data.local.DatabaseProvider
import com.example.breify20.data.restoreSensitiveData
import com.example.breify20.model.email.Category
import com.example.breify20.model.email.SummariesRequest
import com.example.breify20.network.RetrofitClient
import com.example.breify20.ui.screens.EmailPriority

class SummaryFetchWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {


    override suspend fun doWork(): Result {
        return try {
            val prefs = SecurePrefs.getPrefs(applicationContext)
            val token = prefs.getString("accessToken", "") ?: ""
            if (token.isEmpty()) {
                return Result.retry()
            }
            val emailDao = DatabaseProvider.getDatabase(applicationContext).emailDao()
            val mappingDao = DatabaseProvider.getDatabase(applicationContext).sensitiveMappingDao()
            val emailIds = emailDao.getEmailsWithEmptySummary()
            if (emailIds.isEmpty()) {
                return Result.success()
            }else{
                val summariesRequest = emailIds.map {
                    SummariesRequest(it)
                }
                val res = RetrofitClient.authApi.getSummaries(summariesRequest)
                res.forEach { email ->
                    val mapping = mappingDao.getPairs(email.emailId)
                    val pairs = convertStringToPairs(mapping)

                    val restoredSummary = restoreSensitiveData(email.summary, pairs)
                    val restoredDetailedSummary = restoreSensitiveData(email.detailedSummary, pairs)
                    emailDao.updateEmailSummary(
                        emailId = email.emailId,
                        summary = restoredSummary,
                        priority = (email.priority).uppercase(),
                        embedding = email.embedding,
                        category = Category.valueOf((email.category).uppercase()).name,
                        detailedSummary = restoredDetailedSummary
                    )
                    if (((email.priority).uppercase() == EmailPriority.URGENT.name) || ((email.priority).uppercase() == EmailPriority.IMPORTANT.name)) {
                        NotificationHelper.showHighPriorityNotification(
                            applicationContext,
                            email.summary
                        )
                    }
                }
                return Result.success()
            }
            Result.retry()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }
}