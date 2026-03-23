package com.example.breify20.repository

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.breify20.data.SensitiveDataProcessor
import com.example.breify20.data.convertPairsToString
import com.example.breify20.data.local.EmailDao
import com.example.breify20.data.local.SensitiveMappingDao
import com.example.breify20.data.toFloatArray
import com.example.breify20.model.email.Category
import com.example.breify20.model.email.EmailItem
import com.example.breify20.model.email.RawEmails
import com.example.breify20.model.email.SensitiveMapping
import com.example.breify20.network.RetrofitClient
import com.example.breify20.ui.screens.EmailPriority
import kotlinx.coroutines.flow.Flow

class OutlookRepository(
    private val emailDao : EmailDao,
    private val mappingDao: SensitiveMappingDao
) {
    private val api = RetrofitClient.outlookApi
    private val breifyAppi = RetrofitClient.authApi
    fun formatOutlookDate(date: String): String {
        val instant = java.time.Instant.parse(date)
        val formatter = java.time.format.DateTimeFormatter
            .ofPattern("hh:mm a")
            .withZone(java.time.ZoneId.systemDefault())

        return formatter.format(instant)
    }

    suspend fun fetchMails(accessToken: String) {
        try {
            val response = api.getMessages("Bearer $accessToken")
            val exists = emailDao.getAllEmailIds()
            val rawEmails = mutableListOf<RawEmails>()
            val emails = response.value.mapNotNull {
                if(exists.contains(it.id)){
                    return@mapNotNull null
                }
                val email = EmailItem(
                    id = it.id,
                    senderName = it.from.emailAddress.name ?: "",
                    senderEmail = it.from.emailAddress.address,
                    subject = it.subject ?: "(No Subject)",
                    summary = it.bodyPreview ?: "",
                    detailedSummary = "",
                    priority = EmailPriority.MEDIUM,
                    time = formatOutlookDate(it.receivedDateTime),
                    isRead = it.isRead,
                    body = it.body.content,
                    category = Category.WORK,
                    bodyType = if (it.body.contentType == "html") "text/html" else "text/plain",
                    embedding = ""
                )
                val result = SensitiveDataProcessor.extractSensitiveData(email.body)
                val pairString = convertPairsToString(result.pairs)
                mappingDao.insertMapping(
                    SensitiveMapping(
                        emailId = it.id,
                        pairs = pairString
                    )
                )
                //here send to server
                val maskedBody = result.maskedBody
                val id = it.id
                rawEmails.add(RawEmails(id , email.subject , maskedBody))
                email
            }
            val res = breifyAppi.sendSummaries(
                rawEmails
            )
            Log.d("SUMMARIES RESPONSE" , res.message().toString())
            emailDao.insertEmails(emails)
        } catch (e: Exception) {
            Log.e("OUTLOOK_FETCH", "Error fetching emails", e)
        }
    }
    fun getPagedEmails(): Flow<PagingData<EmailItem>> {

        return Pager<Int, EmailItem>(
            config = PagingConfig(
                pageSize = 20
            ),
            pagingSourceFactory = { emailDao.pagingSource() }
        ).flow
    }
    fun deleteAllMails(){
        emailDao.deleteAll()
    }
    fun getEmailsByCategory( category : Category) : Flow<PagingData<EmailItem>>{
        return Pager<Int, EmailItem>(
            config = PagingConfig(
                pageSize = 20
            ),
            pagingSourceFactory = { emailDao.getEmailsByCategory(category) }
        ).flow
    }
    fun getMailById(emailId : String): Flow<EmailItem>{
        return emailDao.getMailById(emailId)
    }
//    suspend fun semanticSearch(
//        query: String,
//        category: String?
//    ): List<EmailItem> {
//
//    }
}