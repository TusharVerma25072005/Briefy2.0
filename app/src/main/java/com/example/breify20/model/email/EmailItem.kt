package com.example.breify20.model.email

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.breify20.ui.screens.EmailPriority


enum class Category{
    SPAM, WORK , EXEC
}

@Entity(tableName = "emails")
data class EmailItem(

    @PrimaryKey
    val id: String,
    val senderName: String,
    val senderEmail: String,
    val subject: String,
    val summary: String,
    val detailedSummary : String,
    val priority: EmailPriority,
    val time: String,
    val isRead: Boolean,
    val body: String,
    val category: Category,
    val bodyType: String,
    val embedding : String=""

)