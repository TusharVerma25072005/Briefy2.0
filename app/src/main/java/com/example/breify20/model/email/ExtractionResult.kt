package com.example.breify20.model.email

data class ExtractionResult(
    val maskedBody: String,
    val pairs: List<Pair<String,String>>
)