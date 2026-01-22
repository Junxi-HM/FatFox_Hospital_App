package com.example.fatfoxhospital.view.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.fatfoxhospital.view.screens.detail.NurseDetailScreen
import com.example.fatfoxhospital.view.screens.home.HomeScreen
import com.example.fatfoxhospital.view.screens.home.MainScreen
import com.example.fatfoxhospital.view.screens.list.NurseListScreen
import com.example.fatfoxhospital.view.screens.login.NurseLoginScreen
import com.example.fatfoxhospital.view.screens.profile.NurseProfileScreen
import com.example.fatfoxhospital.view.screens.register.NurseRegistrationScreen
import com.example.fatfoxhospital.view.screens.search.SearchScreen
import com.example.fatfoxhospital.viewmodel.NurseViewModel

@Composable
fun NavGraph() {
    val viewModel: NurseViewModel = viewModel()
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "main") {
        composable("main") {
            MainScreen(
                onNavigateToNurseRegistration = { navController.navigate("nurse_registration") },
                onNavigateToLogin = { navController.navigate("login") },
                onNavigateToMain = { navController.navigate("main") }
            )
        }

        composable("home") {
            HomeScreen(
                onNavigateToSearch = { navController.navigate("search") },
                onNavigateToNurseList = { navController.navigate("nurse_list") },
                onNavigateToHome = { navController.navigate("home") },
                onNavigateToProfile = { navController.navigate("nurse_profile") }
            )
        }

        composable("nurse_registration") {
            NurseRegistrationScreen(
                navController = navController,
                viewModel = viewModel
            )
        }

        composable("login") {
            NurseLoginScreen(viewModel, navController)
        }

        composable("search") {
            SearchScreen(
                viewModel = viewModel,
                onNurseClick = { nurse ->
                    viewModel.selectNurse(nurse)
                    navController.navigate("nurse_detail/${nurse.id}")
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable("nurse_list") {
            NurseListScreen(
                viewModel = viewModel,
                onNurseClick = { nurse ->
                    viewModel.selectNurse(nurse)
                    navController.navigate("nurse_detail/${nurse.id}")
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = "nurse_detail/{nurseId}",
            arguments = listOf(navArgument("nurseId") { type = NavType.LongType })
        ) { backStackEntry ->
            val nurseId = backStackEntry.arguments?.getLong("nurseId")
            val selectedNurse by viewModel.selectedNurse.observeAsState()

            NurseDetailScreen(
                viewModel = viewModel,
                nurseId = nurseId,
                onBack = {
                    viewModel.clearSelectedNurse()
                    navController.popBackStack()
                }
            )
        }

        composable(route = "nurse_profile") {
            NurseProfileScreen(
                viewModel = viewModel,
                onBack = {
                    viewModel.clearSelectedNurse()
                    navController.popBackStack()
                }
            )
        }
    }
}