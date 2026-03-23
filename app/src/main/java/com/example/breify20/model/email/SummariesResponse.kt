package com.example.breify20.model.email

data class SummariesResponse(
    val emailId : String,
    val summary : String,
    val detailedSummary: String,
    val priority : String,
    val embedding : String,
    val category : String
)