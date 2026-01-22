package com.example.fatfoxhospital.view.screens.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.fatfoxhospital.R
import com.example.fatfoxhospital.extension.getProfilePainter
import com.example.fatfoxhospital.model.Nurse
import com.example.fatfoxhospital.viewmodel.NurseViewModel
import com.example.fatfoxhospital.viewmodel.uistate.NurseUiState


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NurseProfileScreen(
    viewModel: NurseViewModel,
    onBack: () -> Unit
) {
    val nurseUiState = viewModel.nurseUiState

    LaunchedEffect(Unit) {
        viewModel.clearNurseUiState()
        viewModel.getById(viewModel.loggedNurse.id!!)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.nurse_profile_title)) },
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.clearSelectedNurse()
                        onBack()
                    }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            stringResource(R.string.back_icon_desc)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        when (nurseUiState) {
            is NurseUiState.Loading -> {
                Text("Loading...", modifier = Modifier.padding(innerPadding))
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

                    Button(
                        onClick = { /* Implement Update */ },
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.large
                    ) {
                        Text(
                            stringResource(R.string.updateNurse),
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = { /* Implement Delete */ },
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.large
                    ) {
                        Text(
                            stringResource(R.string.deleteNurse),
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
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