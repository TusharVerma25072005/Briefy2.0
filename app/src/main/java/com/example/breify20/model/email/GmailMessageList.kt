package com.example.breify20.model.email

data class GmailMessageListResponse(
    val messages: List<MessageId>,
    val nextPageToken: String?
)

data class MessageId(
    val id: String,
    val threadId: String
)