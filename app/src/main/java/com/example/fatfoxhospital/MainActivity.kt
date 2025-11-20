package com.example.fatfoxhospital

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NurseSearchScreen(nurses = NurseList.mockNurses)
            }
    }
}
@Composable
fun HomeScreen(onNavigate: (String) -> Unit) {
    Column {
        Text("Home")
        Button(onClick = { onNavigate("login") }) { Text("Login") }
        Button(onClick = { onNavigate("registro") }) { Text("Registro") }
        Button(onClick = { onNavigate("busqueda") }) { Text("Búsqueda") }
    }
}

@Composable
fun LoginScreen(onBack: () -> Unit) {
    Column {
        Text("Login")
        Button(onClick = onBack) { Text("Volver") }
    }
}

@Composable
fun RegistroScreen(onBack: () -> Unit) {
    Column {
        Text("Registro")
        Button(onClick = onBack) { Text("Volver") }
    }
}

@Composable
fun BusquedaScreen(onBack: () -> Unit) {
    Column {
        Text("Búsqueda")
        Button(onClick = onBack) { Text("Volver") }
    }
}
@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HomeScreen { route ->
                navController.navigate(route)
            }
        }
        composable("login") {
            LoginScreen { navController.popBackStack() }
        }
        composable("registro") {
            RegistroScreen { navController.popBackStack() }
        }
        composable("busqueda") {
            BusquedaScreen { navController.popBackStack() }
        }
    }
}
