package com.example.breify20.network

import com.example.breify20.model.email.GmailMessageListResponse
import com.example.breify20.model.email.GmailMessageResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface GmailApiService {
    @GET("gmail/v1/users/me/messages")
    suspend fun getMessages(
        @Header("Authorization") token: String,
        @Query("maxResults") maxResults: Int = 20
    ): GmailMessageListResponse


    @GET("gmail/v1/users/me/messages/{id}")
    suspend fun getMessageById(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): GmailMessageResponse
}