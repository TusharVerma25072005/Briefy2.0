package com.example.breify20.ui.viewModel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.breify20.data.SecurePrefs
import com.example.breify20.repository.AuthRepository
import kotlinx.coroutines.launch

class AuthViewModel: ViewModel() {
    private val repository = AuthRepository()
    var loginSuccess = mutableStateOf(false)
    var loginError = mutableStateOf("")


    fun LoginUser(mail : String , password : String, context : Context){
        viewModelScope.launch{
            try {
                val response = repository.Login(mail, password)
                Log.d("LOGIN RESP" , response.toString())
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        if (body.success == true) {
                            val prefs = SecurePrefs.getPrefs(context)

                            prefs.edit()
                                .putString("email", body.email)
                                .putString("name", body.name)
                                .putString("provider", body.provider)
                                .putString("photo", body.photo)
                                .putString("accessToken", body.accessToken)
                                .putString("refreshToken", body.refreshToken)
                                .apply()
                            loginSuccess.value = true
                        } else {
                            loginError.value = "Login failed. Invalid Credentials"
                        }
                    }
                } else {
                    loginError.value = "Unable to reach server"
                }
            }catch(e : Exception){
                loginError.value = "Unable to reach server"
            }
        }
    }
    fun logoutUser(context: Context){
        SecurePrefs.getPrefs(context).edit().clear().apply()
        loginSuccess.value = false

    }
}