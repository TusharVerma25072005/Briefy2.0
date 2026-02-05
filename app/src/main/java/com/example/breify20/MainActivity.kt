package com.example.breify20

import CategoryScreen
import LoginScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHost
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.breify20.ui.screens.EmailScreen
import com.example.breify20.ui.screens.InboxScreen
import com.example.breify20.ui.screens.ProfileScreen
import com.example.breify20.ui.theme.Breify20Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Breify20Theme {
                AppNavHost()
            }
        }
    }
}
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Breify20Theme {
        AppNavHost()
    }
}


@Composable
fun AppNavHost(){
    var navController  = rememberNavController()
    var startDest = "login"
    NavHost(
        navController = navController,
        startDestination = startDest
    ){
        composable(
            route = "login"
        ){
            InboxScreen(navController = navController)
        }
        composable(
            route = "category"
        ) {
            CategoryScreen(navController = navController)
        }
        composable (
            route = "profile"
        ){
            ProfileScreen(navController = navController)
        }
        composable (
            route = "login"
        ){
            LoginScreen(navController = navController)
        }
        composable(route = "email") {
            EmailScreen(navController = navController)
        }
    }
}