package com.example.fatfoxhospital.pr07

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NurseRegistrationScreen(
    // NavController inyectado para gestionar la navegación
    navController: NavController,
    viewModel: NurseRegistrationViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Manejar el éxito del registro: Muestra Snackbar y redirige a Login
    LaunchedEffect(uiState.isRegistrationSuccessful) {
        if (uiState.isRegistrationSuccessful) {
            // Muestra mensaje de éxito antes de redirigir
            snackbarHostState.showSnackbar("Registro exitoso. Redirigiendo a Iniciar Sesión...")
/*
            // Navega a la pantalla de Login y borra la pantalla de registro de la pila
            navController.navigate(Screen.Login.route) {
                popUpTo(Screen.Home.route) { inclusive = false }
            }
*/
            viewModel.registrationComplete() // Reinicia el estado para futuras navegaciones
        }
    }

    // Manejar mensajes de error (duplicados, formato, etc.)
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearErrorMessage()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Registro de Enfermero") },
                navigationIcon = {
                    // Botón para volver a la página de inicio
/*
                    IconButton(onClick = { navController.popBackStack(Screen.Home.route, inclusive = false) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver a Inicio")
                    }

 */
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Crear Cuenta de Enfermero",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // --- Campos del Formulario ---
            OutlinedTextField(
                value = uiState.name,
                onValueChange = viewModel::updateName,
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            )

            OutlinedTextField(
                value = uiState.surname,
                onValueChange = viewModel::updateSurname,
                label = { Text("Apellido") },
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            )

            // Correo Electrónico (Con KeyboardType.Email)
            OutlinedTextField(
                value = uiState.email,
                onValueChange = viewModel::updateEmail,
                label = { Text("Correo Electrónico") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            )

            // Nombre de Usuario
            OutlinedTextField(
                value = uiState.username,
                onValueChange = viewModel::updateUsername,
                label = { Text("Nombre de Usuario") },
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            )

            // Contraseña (Oculta y con KeyboardType.Password)
            OutlinedTextField(
                value = uiState.password,
                onValueChange = viewModel::updatePassword,
                label = { Text("Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Botón de Registro
            Button(
                onClick = viewModel::registerNurse,
                enabled = !uiState.isRegistrationSuccessful,
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text("REGISTRAR CUENTA")
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Opción: "¿Ya tienes un usuario? Iniciar sesión"
            Text(
                text = "¿Ya tienes un usuario? Iniciar sesión",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable {
                    // Navega a la pantalla de Login y borra la actual de la pila
/*
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.NurseRegistration.route) { inclusive = true }
                    }
 */
                }
            )
        }
    }
}