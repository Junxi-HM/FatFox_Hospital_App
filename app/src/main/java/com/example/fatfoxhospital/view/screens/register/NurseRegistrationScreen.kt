package com.example.fatfoxhospital.view.screens.register

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.fatfoxhospital.R
import com.example.fatfoxhospital.viewmodel.NurseViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NurseRegistrationScreen(
    navController: NavController,
    viewModel: NurseViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    val successMessage = stringResource(R.string.registration_success_snackbar)

    LaunchedEffect(uiState.isRegistrationSuccessful) {
        if (uiState.isRegistrationSuccessful) {
            snackbarHostState.showSnackbar(successMessage)
            navController.navigate("login") {
                popUpTo("nurse_registration") { inclusive = true }
            }
            viewModel.registrationComplete()
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearErrorMessage()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp)
                    .clickable {
                        navController.navigate("main") {
                            popUpTo("main") { inclusive = true }
                        }
                    }
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = stringResource(R.string.logo_description),
                    modifier = Modifier.size(72.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = stringResource(R.string.hospital_manager_title),
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-0.5).sp
                    )
                )
            }

            Text(
                text = stringResource(R.string.create_nurse_account),
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Selector de imágenes
            ProfileImageSelector(
                profileBytes = uiState.profile,
                currentIndex = uiState.profileIndex,
                onImageSelected = viewModel::updateProfileRes
            )

            OutlinedTextField(
                value = uiState.name,
                onValueChange = viewModel::updateName,
                label = { Text(stringResource(R.string.name_label)) },
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            )

            OutlinedTextField(
                value = uiState.surname,
                onValueChange = viewModel::updateSurname,
                label = { Text(stringResource(R.string.surname_label)) },
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            )

            OutlinedTextField(
                value = uiState.email,
                onValueChange = viewModel::updateEmail,
                label = { Text(stringResource(R.string.email_label)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            )

            OutlinedTextField(
                value = uiState.username,
                onValueChange = viewModel::updateUsername,
                label = { Text(stringResource(R.string.username_label_registration)) },
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            )

            OutlinedTextField(
                value = uiState.password,
                onValueChange = viewModel::updatePassword,
                label = { Text(stringResource(R.string.password_label)) },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = viewModel::registerNurse,
                enabled = !uiState.isRegistrationSuccessful,
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text(stringResource(R.string.register_button))
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(R.string.already_have_account),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable {
                    navController.navigate("login") {
                        popUpTo("nurse_registration") { inclusive = true }
                    }
                }
            )
        }
    }
}

@Composable
private fun ProfileImageSelector(
    profileBytes: ByteArray?,
    currentIndex: Int,
    onImageSelected: (Int) -> Unit
) {
    val totalResources = NurseViewModel.PROFILE_RESOURCES.size

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(vertical = 16.dp)
    ) {
        // Si hay datos de bytes, descodifíquelos y muéstrelos como un mapa de bits; de lo contrario, muestre una imagen de marcador de posición.
        if (profileBytes != null && profileBytes.isNotEmpty()) {
            val bitmap = remember(profileBytes) {
                BitmapFactory.decodeByteArray(profileBytes, 0, profileBytes.size)
            }
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "Foto de perfil",
                modifier = Modifier.size(96.dp).padding(bottom = 8.dp)
            )
        } else {
            Image(
                painter = painterResource(id = R.drawable.perfil1),
                contentDescription = "Placeholder",
                modifier = Modifier.size(96.dp).padding(bottom = 8.dp)
            )
        }

        Button(
            onClick = {
                // Calcula el siguiente índice y vuelve a llamar al ViewModel para volver a leer los bytes de la imagen
                val nextIndex = (currentIndex + 1) % totalResources
                onImageSelected(nextIndex)
            }
        ) {
            Text(stringResource(R.string.select_profile_picture_button))
        }
    }
}