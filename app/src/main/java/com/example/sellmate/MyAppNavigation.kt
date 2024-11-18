package com.example.sellmate

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import com.example.sellmate.page.HomePage
import com.example.sellmate.page.LoginPage
import com.example.sellmate.page.SignupPage

@Composable
fun MyAppNavigation(modifier: Modifier = Modifier, authViewModel: AuthViewModel){
    val navController = rememberNavController()

    NavHost(navController, startDestination= "home", builder = {

        composable("home") {
            HomePage(modifier, navController)
        }

        composable("login") {
            LoginPage(modifier, navController, authViewModel)
        }

        composable("signup") {
            SignupPage(modifier,navController)
        }

    })
}