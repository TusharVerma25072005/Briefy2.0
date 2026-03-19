package com.example.breify20.network

import com.example.breify20.model.login.LoginRequest
import com.example.breify20.model.login.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("api/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>


    @POST("api/auth/token-refresh")
    suspend fun refreshUsingCredentials(
        @Body request: LoginRequest
    ): Response<LoginResponse>

}