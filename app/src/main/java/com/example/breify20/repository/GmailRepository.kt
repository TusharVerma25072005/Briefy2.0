package com.example.breify20.repository

import android.util.Log
import android.widget.Toast
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.breify20.data.SensitiveDataProcessor
import com.example.breify20.data.convertPairsToString
import com.example.breify20.data.cosineSimilarity
import com.example.breify20.data.decodeBody
import com.example.breify20.data.getGmailTimestamp
import com.example.breify20.data.getHeader
import com.example.breify20.data.local.EmailDao
import com.example.breify20.data.local.SensitiveMappingDao
import com.example.breify20.data.parseSender
import com.example.breify20.model.email.Category
import com.example.breify20.model.email.EmailBody
import com.example.breify20.model.email.EmailItem
import com.example.breify20.model.email.GmailMessageResponse
import com.example.breify20.model.email.Payload
import com.example.breify20.model.email.RawEmails
import com.example.breify20.model.email.SensitiveMapping
import com.example.breify20.model.login.EmbeddingResponse
import com.example.breify20.model.login.TextRequest
import com.example.breify20.network.RetrofitClient
import com.example.breify20.ui.screens.EmailPriority
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Locale


class GmailRepository(
    private val emailDao: EmailDao,
    private val mappingDao: SensitiveMappingDao,
) {
    private val gmailApi = RetrofitClient.gmailApi
    private val breifyApi = RetrofitClient.authApi

    fun formatEmailDate(dateString: String): String {
        return try {

            val gmailFormat =
                SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH)

            val date = gmailFormat.parse(dateString) ?: return dateString

            val outputFormat =
                SimpleDateFormat("dd MMM HH:mm", Locale.getDefault())

            outputFormat.format(date)

        } catch (e: Exception) {
            dateString
        }
    }
    fun extractBody(payload: Payload): EmailBody {
        payload.body?.data?.let {
            return EmailBody(
                decodeBody(it),
                payload.mimeType ?: "text/plain"
            )
        }
        payload.parts?.let { parts ->
            parts.forEach { part ->
                if (part.mimeType == "text/html") {
                    part.body?.data?.let {
                        return EmailBody(decodeBody(it), "text/html")
                    }
                }
            }
            parts.forEach { part ->
                if (part.mimeType == "text/plain") {
                    part.body?.data?.let {
                        return EmailBody(decodeBody(it), "text/plain")
                    }
                }
            }
            parts.forEach { part ->
                val result = extractBody(part)
                if (result.content.isNotEmpty()) {
                    return result
                }
            }
        }
        return EmailBody("", "text/plain")
    }
    fun mapToEmailItem(message: GmailMessageResponse): EmailItem {

        val headers = message.payload.headers

        val from = getHeader(headers, "From") ?: ""
        val subject = getHeader(headers, "Subject") ?: "(No Subject)"
        val rawDate = getHeader(headers, "Date") ?: ""
        val date = formatEmailDate(rawDate)

        val (senderName, senderEmail) = parseSender(from)

        val emailBody = extractBody(message.payload)

        return EmailItem(
            id = message.id,
            senderName = senderName,
            senderEmail = senderEmail,
            subject = subject,
            summary = "",
            detailedSummary = "",
            priority = EmailPriority.NORMAL,
            time = date,
            isRead = false,
            body = emailBody.content,
            bodyType = emailBody.mimeType,
            category = Category.OTHER,
            embedding = "",
            createdAt = getGmailTimestamp(rawDate)

        )
    }
    suspend fun fetchEmails(accessToken:String):List<EmailItem> {
        return try {
            val response =
                gmailApi.getMessages("Bearer $accessToken")
            val emails = mutableListOf<EmailItem>()
            val exists = emailDao.getAllEmailIds()
            val rawEmails = mutableListOf<RawEmails>()
            response.messages.forEach {
                val message = gmailApi.getMessageById("Bearer $accessToken", it.id)
                if(exists.contains(message.id)){
                    return@forEach
                }
                val email = mapToEmailItem(message)
                val result = SensitiveDataProcessor.extractSensitiveData(email.body)
                val pairString = convertPairsToString(result.pairs)
                mappingDao.insertMapping(
                    SensitiveMapping(
                        emailId = email.id,
                        pairs = pairString
                    )
                )
                val maskedBody = result.maskedBody
                val id = email.id
                rawEmails.add(RawEmails(id , email.subject , maskedBody))

                emails.add(email)
                Log.d("Email Mesage" , email.subject.toString())
            }
            emailDao.insertEmails(emails)
            val res = breifyApi.sendSummaries(rawEmails)
            emails
        } catch (e: Exception) {
            Log.e("GMAIL_ERROR", "Error fetching emails", e)
            emptyList()
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
    suspend fun semanticSearch(query: String, category: String?): List<EmailItem> {
        try {
            val resp: EmbeddingResponse = breifyApi.getEmbedding(TextRequest(query))
            val queryEmbedding = resp.embedding
            val emails: List<EmailItem> = if (category == null) {
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
        }catch(e : Exception){
            Toast.makeText(null , "Error in semantic search, Please try again.." , Toast.LENGTH_LONG).show()
            return emptyList()

        }
    }
    suspend fun markAsRead(emailId : String){
        emailDao.markAsRead(emailId)
    }

}