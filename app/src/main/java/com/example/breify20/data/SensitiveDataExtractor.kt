package com.example.breify20.data
import com.example.breify20.model.email.ExtractionResult
import com.google.mlkit.nl.entityextraction.*
import kotlinx.coroutines.tasks.await
import java.security.MessageDigest

object SensitiveDataProcessor {
    private val extractor: EntityExtractor by lazy {
        val options = EntityExtractorOptions.Builder(
            EntityExtractorOptions.ENGLISH
        ).build()
        EntityExtraction.getClient(options)
    }
    suspend fun extractSensitiveData(body: String): ExtractionResult {
        extractor.downloadModelIfNeeded().await()
        val params = EntityExtractionParams.Builder(body).build()
        val annotations = extractor.annotate(params).await()
        var maskedBody = body
        val pairs = mutableListOf<Pair<String,String>>()
        annotations.forEach {
            val value = body.substring(it.start, it.end)
            val hash = sha256(value)
            pairs.add(Pair(value, hash))
            maskedBody = maskedBody.replace(value, "<PRIVATE_$hash>")
        }
        return ExtractionResult(maskedBody, pairs)
    }
    private fun sha256(input: String): String {
        val digest = MessageDigest
            .getInstance("SHA-256")
            .digest(input.toByteArray())
        return digest.joinToString("") { "%02x".format(it) }
    }
}
