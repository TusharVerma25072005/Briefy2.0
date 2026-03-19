package com.example.breify20.worker
import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.breify20.data.SecurePrefs
import com.example.breify20.model.login.LoginRequest
import com.example.breify20.network.RetrofitClient

class TokenRefreshWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        return try {
            val prefs = SecurePrefs.getPrefs(applicationContext)
            val email = prefs.getString("email", "") ?: ""
            val password = prefs.getString("password", "") ?: ""
            if (email.isEmpty() || password.isEmpty()) {
                return Result.failure()
            }
            val response = RetrofitClient.authApi.refreshUsingCredentials(
                LoginRequest(email, password)
            )
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.accessToken!= null && body.accessToken != "" ) {
                    prefs.edit()
                        .putString("accessToken", body.accessToken)
                        .apply()

                    Log.d("ACCESS TOKEN","Updated successfully")
                    return Result.success()
                }
            }

            Log.d("REFRESH ERROR","Error while fetching token")
            Result.retry()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }
}