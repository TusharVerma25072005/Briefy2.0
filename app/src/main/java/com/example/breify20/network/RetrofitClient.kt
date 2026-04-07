package com.example.breify20.network
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private const val SERVER_URL = "https://briefy2-0-backend.onrender.com/"
    private const val OUTLOOK_BASE_URL = "https://graph.microsoft.com/"
    private const val GMAIL_BASE_URL = "https://gmail.googleapis.com/"


    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(120, TimeUnit.SECONDS)
        .readTimeout(120, TimeUnit.SECONDS)
        .writeTimeout(120, TimeUnit.SECONDS)
        .build()

    val authApi: AuthApi by lazy {
        Retrofit.Builder()
            .baseUrl(SERVER_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthApi::class.java)
    }
    val outlookApi: OutlookApiService by lazy {
        Retrofit.Builder()
            .baseUrl(OUTLOOK_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OutlookApiService::class.java)
    }

    val gmailApi: GmailApiService by lazy {
        Retrofit.Builder()
            .baseUrl(GMAIL_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GmailApiService::class.java)
    }
}