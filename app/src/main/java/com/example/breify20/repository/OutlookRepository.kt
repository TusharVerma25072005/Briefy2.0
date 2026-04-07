package com.example.breify20.repository

import android.util.Log
import android.widget.Toast
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.breify20.data.SensitiveDataProcessor
import com.example.breify20.data.convertPairsToString
import com.example.breify20.data.cosineSimilarity
import com.example.breify20.data.getOutlookTimestamp
import com.example.breify20.data.local.EmailDao
import com.example.breify20.data.local.SensitiveMappingDao
import com.example.breify20.data.toFloatArray
import com.example.breify20.model.email.Category
import com.example.breify20.model.email.EmailItem
import com.example.breify20.model.email.RawEmails
import com.example.breify20.model.email.SensitiveMapping
import com.example.breify20.model.login.EmbeddingResponse
import com.example.breify20.model.login.TextRequest
import com.example.breify20.network.RetrofitClient
import com.example.breify20.ui.screens.EmailPriority
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
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
                    priority = EmailPriority.NORMAL,
                    time = formatOutlookDate(it.receivedDateTime),
                    isRead = it.isRead,
                    body = it.body.content,
                    category = Category.OTHER,
                    bodyType = if (it.body.contentType == "html") "text/html" else "text/plain",
                    embedding = "",
                    createdAt = getOutlookTimestamp(it.receivedDateTime)
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
    suspend fun semanticSearch(
        query: String,
        category: String?
    ): List<EmailItem> {
        try{

            val resp: EmbeddingResponse = breifyAppi.getEmbedding(TextRequest(query))
            val queryEmbedding = resp.embedding
            val emails:List<EmailItem> = if (category == null) {
                emailDao.getAllEmails()
            } else {
                emailDao.getEmailsByCategoryList(Category.valueOf(category))
            }
            val gson = Gson()
            return emails.map { email ->
                val type = object : TypeToken<List<Float>>() {}.type
                val emailEmbedding: List<Float> = if (email.embedding.isNotEmpty()) {
                    gson.fromJson(email.embedding, type)
                } else {
                    emptyList()
                }

                val similarity = if (emailEmbedding.isNotEmpty()) {
                    cosineSimilarity(queryEmbedding, emailEmbedding)
                } else {
                    0f
                }

                email to similarity
            }
                .sortedByDescending { it.second }
                .map { it.first }

        }catch(e : Exception) {
                Toast.makeText(null , "Error in semantic search, Please try again.." , Toast.LENGTH_LONG).show()
                return emptyList()
        }
    }
}