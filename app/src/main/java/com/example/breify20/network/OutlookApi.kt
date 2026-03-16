package com.example.breify20.network

import com.example.breify20.model.email.OutlookResponse
import retrofit2.http.GET
import retrofit2.http.Header

interface OutlookApiService {

    @GET("v1.0/me/messages")
    suspend fun getMessages(
        @Header("Authorization") token: String
    ): OutlookResponse
}