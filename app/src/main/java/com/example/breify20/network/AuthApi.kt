package com.example.breify20.network

import com.example.breify20.model.email.RawEmails
import com.example.breify20.model.email.RawEmailsResponse
import com.example.breify20.model.email.SummariesRequest
import com.example.breify20.model.email.SummariesResponse
import com.example.breify20.model.login.EmbeddingResponse
import com.example.breify20.model.login.LoginRequest
import com.example.breify20.model.login.LoginResponse
import com.example.breify20.model.login.TextRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("api/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    @POST("api/auth/tokenRefresh")
    suspend fun refreshUsingCredentials(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    @POST("api/getSummaries")
    suspend fun getSummaries(
        @Body request: List<SummariesRequest>
    ): List<SummariesResponse>

    @POST("api/sendSummaries")
    suspend fun sendSummaries(
        @Body request : List<RawEmails>
    ): Response<RawEmailsResponse>

    @POST("api/getEmbedding")
    suspend fun getEmbedding(
        @Body request: TextRequest
    ): EmbeddingResponse
}