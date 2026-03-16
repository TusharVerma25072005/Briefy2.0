package com.example.breify20.model.login

data class LoginResponse(
    val success: Boolean,
    val message: String,
    val refreshToken: String?,
    val accessToken: String?,
    val name: String?,
    val email: String?,
    val provider: String?,
    val photo: String?
)