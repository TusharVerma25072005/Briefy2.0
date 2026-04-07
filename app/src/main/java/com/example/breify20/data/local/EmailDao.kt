package com.example.breify20.data.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.breify20.model.email.Category
import com.example.breify20.model.email.EmailItem
import com.example.breify20.model.email.SummariesResponse
import com.example.breify20.ui.screens.EmailPriority
import kotlinx.coroutines.flow.Flow


@Dao
interface EmailDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEmail(email: EmailItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEmails(emails: List<EmailItem>)

    @Query("SELECT * FROM emails ORDER BY time DESC")
    suspend fun getAllEmails(): List<EmailItem>

    @Query("SELECT * FROM emails ORDER BY time DESC")
    fun pagingSource(): PagingSource<Int, EmailItem>

    @Query("DELETE FROM emails")
    fun deleteAll()

    @Query("SELECT * FROM emails WHERE category = :category ORDER BY time DESC")
    fun getEmailsByCategory(category: Category): PagingSource<Int, EmailItem>

    @Query("SELECT * FROM emails WHERE id = :emailId")
    fun getMailById(emailId : String): Flow<EmailItem>

    @Query("SELECT id FROM emails")
    suspend fun getAllEmailIds(): List<String>

    @Query("SELECT * FROM emails WHERE category = :category")
    suspend fun getEmailsByCategoryList(category: Category): List<EmailItem>

    @Query("""
    SELECT id FROM emails 
    WHERE summary = '' OR summary IS NULL 
    ORDER BY createdAt DESC
    LIMIT 10
""")    suspend fun getEmailsWithEmptySummary(): List<String>

    @Query("""
        UPDATE emails
        SET summary = :summary,
            detailedSummary = :detailedSummary,
            priority = :priority,
            embedding = :embedding,
            category = :category
        WHERE id = :emailId
    """)
    suspend fun updateEmailSummary(
        emailId: String,
        summary: String,
        detailedSummary : String,
        priority: String,
        embedding: String,
        category: String
    )

    @Transaction
    suspend fun updateEmailSummariesBulk(summaries: List<SummariesResponse>) {
        for (summary in summaries) {
            updateEmailSummary(
                emailId = summary.emailId,
                summary = summary.summary,
                priority = summary.priority,
                embedding = summary.embedding,
                category = summary.category,
                detailedSummary = summary.detailedSummary
            )
        }
    }
}