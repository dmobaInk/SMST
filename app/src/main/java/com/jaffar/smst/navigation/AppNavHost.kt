package com.jaffar.smst.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.jaffar.smst.ui.screen.Home
import com.jaffar.smst.ui.screen.Messages
import com.jaffar.smst.ui.screen.Numbers
import com.jaffar.smst.viewmodel.SmsViewModel

@Composable
fun MyAppNavHost(){
    val navController = rememberNavController()
    val smsViewModel: SmsViewModel = viewModel()

    smsViewModel.loadNumbersFromStorage()
    smsViewModel.loadMessagesFromStorage()
    smsViewModel.loadAuthCodeFromStorage()

    NavHost(navController, startDestination = "home"){
        composable("home") {
            Home(onNavigate = {route -> navController.navigate(route)}, smsViewModel)
        }
        composable("numbers") {
            Numbers(onBack = {navController.popBackStack()}, smsViewModel)
        }
        composable("messages") {
            Messages(onBack = {navController.popBackStack()}, smsViewModel)
        }
    }
}