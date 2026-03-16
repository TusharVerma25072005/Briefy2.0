package com.example.breify20.model.email

data class GmailMessageResponse(
    val id: String,
    val snippet: String,
    val payload: Payload
)

data class Payload(
    val headers: List<Header>,
    val body: Body?,
    val mimeType: String?,
    val parts: List<Payload>?
)

data class Header(
    val name: String,
    val value: String
)

data class Body(
    val data: String?
)