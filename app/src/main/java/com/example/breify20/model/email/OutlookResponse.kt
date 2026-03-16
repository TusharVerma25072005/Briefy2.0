package com.example.breify20.model.email


data class OutlookResponse(
    val value: List<OutlookMessage>
)

data class OutlookMessage(
    val id: String,
    val subject: String?,
    val bodyPreview: String?,
    val receivedDateTime: String,
    val body: OutlookBody,
    val from: OutlookSender,
    val isRead : Boolean,

)

data class OutlookBody(
    val content: String,
    val contentType: String
)

data class OutlookSender(
    val emailAddress: OutlookEmail
)

data class OutlookEmail(
    val name: String?,
    val address: String
)