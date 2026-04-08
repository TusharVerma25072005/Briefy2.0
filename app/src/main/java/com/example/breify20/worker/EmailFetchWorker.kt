package com.example.breify20.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.breify20.data.SecurePrefs
import com.example.breify20.data.local.DatabaseProvider
import com.example.breify20.repository.GmailRepository
import com.example.breify20.repository.OutlookRepository

class EmailFetchWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val prefs = SecurePrefs.getPrefs(applicationContext)
            val token = prefs.getString("accessToken", "") ?: ""
            val provider = prefs.getString("provider" , "")?: ""
            if (token.isEmpty() || provider.isEmpty()) {
                return Result.failure()
            }
            val db = DatabaseProvider.getDatabase(applicationContext)
            if(provider == "gmail"){
                 val repo = GmailRepository(db.emailDao() , db.sensitiveMappingDao())
                 repo.fetchEmails(token)
            }else if(provider == "outlook"){
                val repo =OutlookRepository(db.emailDao() , db.sensitiveMappingDao())
                repo.fetchMails(token)
            }else{
                null
            }
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}