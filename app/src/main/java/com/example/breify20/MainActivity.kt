package com.example.breify20

import CategoryScreen
import LoginScreen
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHost
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.breify20.data.SecurePrefs
import com.example.breify20.data.local.DatabaseProvider
import com.example.breify20.repository.GmailRepository
import com.example.breify20.repository.OutlookRepository
import com.example.breify20.ui.screens.EmailScreen
import com.example.breify20.ui.screens.InboxScreen
import com.example.breify20.ui.screens.ProfileScreen
import com.example.breify20.ui.theme.Breify20Theme
import com.example.breify20.ui.viewModel.AuthViewModel
import com.example.breify20.ui.viewModel.EmailViewModel
import com.example.breify20.ui.viewModel.GmailViewModel
import com.example.breify20.ui.viewModel.GmailViewModelFactory
import com.example.breify20.ui.viewModel.OutlookViewModel
import com.example.breify20.ui.viewModel.OutlookViewModelFactory
import com.example.breify20.worker.WorkManagerHelper

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val data = intent?.data
        val mail = data?.getQueryParameter("mail")
        enableEdgeToEdge()
        setContent {
            Breify20Theme {
                AppNavHost(mail = mail)
            }
        }
    }
}
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Breify20Theme {
        AppNavHost(mail = "")
    }
}


@Composable
fun AppNavHost(mail: String?){
    val context = LocalContext.current
    val authViewModel: AuthViewModel = viewModel()
    val prefs = SecurePrefs.getPrefs(context).contains("email")
    var provider by remember {
        mutableStateOf(
            SecurePrefs.getPrefs(context).getString("provider", "")
        )
    }
    val loginSuccess = authViewModel.loginSuccess.value

    LaunchedEffect(loginSuccess) {
        if (loginSuccess) {
            Log.d("Recomposition","Triggered")
            provider = SecurePrefs.getPrefs(context).getString("provider", "")
        }
    }
    var navController  = rememberNavController()
    val db = DatabaseProvider.getDatabase(context)
    val viewModel : EmailViewModel? = if(provider == "gmail"){
        val repository = GmailRepository(db.emailDao() , db.sensitiveMappingDao())
        val factory = GmailViewModelFactory(repository)
        val gmailViewModel: GmailViewModel = viewModel(factory = factory)
        Log.d("GmailViewModel" , gmailViewModel.toString())
        gmailViewModel
    }else if(provider == "outlook"){
        val outlookRepo = OutlookRepository(db.emailDao() , db.sensitiveMappingDao())
        val factory = OutlookViewModelFactory(outlookRepo)

        val outlookViewModel: OutlookViewModel = viewModel(factory = factory)

        Log.d("OutlookViewModel" , outlookViewModel.toString())
        outlookViewModel
    }else{
        null
    }
    val accessToken =
        SecurePrefs.getPrefs(context)
            .getString("accessToken", "") ?: ""
    LaunchedEffect(accessToken) {
        if (accessToken.isNotEmpty()) {
            WorkManagerHelper.scheduleEmailSync(context)
        }
    }
    var startDest = if(prefs){
            "inbox"
        }else{
            "login"
        }
    NavHost(
        navController = navController,
        startDestination = startDest
    ){
        composable(
            route = "inbox"
        ){
            InboxScreen(navController = navController, viewModel = viewModel)
        }
        composable(
            route = "category"
        ) {
            CategoryScreen(navController = navController , viewModel = viewModel)
        }
        composable (
            route = "profile"
        ){
            ProfileScreen(navController = navController , emailViewModel = viewModel, viewModel = authViewModel)
        }
        composable (
            route = "login"
        ){
            LoginScreen(navController = navController , mail = mail , authViewModel = authViewModel)
        }
        composable(
            route = "email/{emailId}",
            arguments = listOf(navArgument("emailId") { type = NavType.StringType })
        ) { backStackEntry ->

            val emailId = backStackEntry.arguments?.getString("emailId")
            if(emailId!= null && viewModel != null) {
                EmailScreen(
                    emailId = emailId,
                    navController = navController,
                    viewModel = viewModel
                )
            }
        }
    }
}