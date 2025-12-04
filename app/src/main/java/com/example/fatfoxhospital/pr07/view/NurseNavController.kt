package com.example.fatfoxhospital.pr07.view

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.fatfoxhospital.pr07.viewmodel.NurseViewModel

@Composable
fun NurseNavController() {
    val viewModel: NurseViewModel = viewModel()
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "Main") {
        composable(route = "Main") {
            NurseMainScreenContent(navController)
        }
        composable(route = "Register") {
        }
        composable(route = "Login") {
            NurseLoginScreen(viewModel = viewModel, navController)
        }
        composable(route = "Index") {
        }
        composable(route = "Search") {
        }

    }
}