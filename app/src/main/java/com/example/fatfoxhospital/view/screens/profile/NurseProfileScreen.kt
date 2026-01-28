package com.example.fatfoxhospital.view.screens.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.fatfoxhospital.R
import com.example.fatfoxhospital.extension.getProfilePainter
import com.example.fatfoxhospital.model.Nurse
import com.example.fatfoxhospital.viewmodel.NurseViewModel
import com.example.fatfoxhospital.viewmodel.uistate.NurseUiState
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NurseProfileScreen(
    viewModel: NurseViewModel,
    onBack: () -> Unit,
    onDeleteSuccess: () -> Unit = onBack // Navigate back after delete
) {
    val nurseUiState = viewModel.nurseUiState
    val updateUiState by viewModel.updateUiState.collectAsState()
    val deleteUiState by viewModel.deleteUiState.collectAsState()

    // Edit mode state
    var isEditMode by remember { mutableStateOf(false) }

    // Editable fields
    var editedName by remember { mutableStateOf("") }
    var editedSurname by remember { mutableStateOf("") }
    var editedEmail by remember { mutableStateOf("") }
    var editedUsername by remember { mutableStateOf("") }
    var editedPassword by remember { mutableStateOf("") }

    // Dialog states
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }

    // Collect update success
    LaunchedEffect(Unit) {
        viewModel.updateUiState.collectLatest { state ->
            if (state.isSuccess) {
                isEditMode = false
                viewModel.clearUpdateState()
            }
        }
    }

    // Collect delete success
    LaunchedEffect(Unit) {
        viewModel.deleteUiState.collectLatest { state ->
            if (state.isSuccess) {
                viewModel.clearDeleteState()
                onDeleteSuccess()
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.clearNurseUiState()
        viewModel.getById(viewModel.loggedNurse.id!!)
    }

    // Update editable fields when nurse data changes
    LaunchedEffect(nurseUiState) {
        if (nurseUiState is NurseUiState.Success) {
            val nurse = nurseUiState.nurse
            editedName = nurse.name
            editedSurname = nurse.surname
            editedEmail = nurse.email
            editedUsername = nurse.user
            editedPassword = nurse.password
        }
    }

    // Delete confirmation dialog
    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = { Text(stringResource(R.string.delete_confirmation_title)) },
            text = { Text(stringResource(R.string.delete_confirmation_message)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirmDialog = false
                        val nurseId = (nurseUiState as? NurseUiState.Success)?.nurse?.id
                        nurseId?.let { viewModel.deleteNurse(it) }
                    }
                ) {
                    Text(stringResource(R.string.delete_confirm), color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.nurse_profile_title)) },
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.clearSelectedNurse()
                        viewModel.clearUpdateState()
                        viewModel.clearDeleteState()
                        onBack()
                    }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            stringResource(R.string.back_icon_desc)
                        )
                    }
                },
                actions = {
                    if (nurseUiState is NurseUiState.Success && !isEditMode) {
                        IconButton(onClick = { isEditMode = true }) {
                            Text("✏️")
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        when (nurseUiState) {
            is NurseUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is NurseUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.nurse_profile_error),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            is NurseUiState.Success -> {
                val nurse: Nurse = nurseUiState.nurse

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = nurse.getProfilePainter(),
                        contentDescription = "${stringResource(R.string.ProfilePicture)} ${nurse.name} ${nurse.surname}",
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    if (isEditMode) {
                        // EDIT MODE: Editable fields
                        OutlinedTextField(
                            value = editedName,
                            onValueChange = { editedName = it },
                            label = { Text(stringResource(R.string.name_label)) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = editedSurname,
                            onValueChange = { editedSurname = it },
                            label = { Text(stringResource(R.string.surname_label)) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = editedEmail,
                            onValueChange = { editedEmail = it },
                            label = { Text(stringResource(R.string.email_label)) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = editedUsername,
                            onValueChange = { editedUsername = it },
                            label = { Text(stringResource(R.string.username_label)) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = editedPassword,
                            onValueChange = { editedPassword = it },
                            label = { Text(stringResource(R.string.password_label)) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation()
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Error message
                        updateUiState.errorMessage?.let { error ->
                            Text(
                                text = error,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }

                        // Save and Cancel buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    isEditMode = false
                                    viewModel.clearUpdateState()
                                    // Reset to original values
                                    editedName = nurse.name
                                    editedSurname = nurse.surname
                                    editedEmail = nurse.email
                                    editedUsername = nurse.user
                                    editedPassword = nurse.password
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(stringResource(R.string.cancel))
                            }

                            Button(
                                onClick = {
                                    val updatedNurse = nurse.copy(
                                        name = editedName.trim(),
                                        surname = editedSurname.trim(),
                                        email = editedEmail.trim(),
                                        user = editedUsername.trim(),
                                        password = editedPassword
                                    )
                                    viewModel.updateProfile(updatedNurse)
                                },
                                modifier = Modifier.weight(1f),
                                enabled = !updateUiState.isLoading
                            ) {
                                if (updateUiState.isLoading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Text(stringResource(R.string.save))
                                }
                            }
                        }
                    } else {
                        // VIEW MODE: Display info
                        Text(
                            text = "${nurse.name} ${nurse.surname}",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                            )
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                DetailRow(stringResource(R.string.id_label), "#${nurse.id}")
                                Spacer(modifier = Modifier.height(8.dp))
                                DetailRow(stringResource(R.string.username_detail_label), nurse.user)
                                Spacer(modifier = Modifier.height(8.dp))
                                DetailRow(stringResource(R.string.email_detail_label), nurse.email)
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Update Button
                        Button(
                            onClick = { isEditMode = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.large
                        ) {
                            Text(
                                stringResource(R.string.updateNurse),
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Delete Button
                        OutlinedButton(
                            onClick = { showDeleteConfirmDialog = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.large,
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            if (deleteUiState.isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.dp,
                                    color = MaterialTheme.colorScheme.error
                                )
                            } else {
                                Text(
                                    stringResource(R.string.deleteNurse),
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }
                        }

                        // Error messages
                        deleteUiState.errorMessage?.let { error ->
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = error,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}