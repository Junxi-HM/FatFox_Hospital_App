package com.example.fatfoxhospital.pr07.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.fatfoxhospital.pr07.ui.screens.detail.NurseDetailScreen
import com.example.fatfoxhospital.pr07.ui.screens.home.HomeScreen
import com.example.fatfoxhospital.pr07.ui.screens.list.NurseListScreen
import com.example.fatfoxhospital.pr07.ui.screens.search.SearchScreen
import com.example.fatfoxhospital.pr07.ui.viewmodel.NurseViewModel

@Composable
fun NavGraph(viewModel: NurseViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(
                onNavigateToSearch = { navController.navigate("search") },
                onNavigateToNurseList = { navController.navigate("nurse_list") }
            )
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
    }
}