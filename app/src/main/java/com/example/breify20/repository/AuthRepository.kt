package com.example.breify20.repository

import com.example.breify20.model.login.LoginRequest
import com.example.breify20.network.RetrofitClient

class AuthRepository {
    suspend fun Login(mail : String , password : String) = RetrofitClient.authApi.login(
        LoginRequest(mail, password)
    )
}