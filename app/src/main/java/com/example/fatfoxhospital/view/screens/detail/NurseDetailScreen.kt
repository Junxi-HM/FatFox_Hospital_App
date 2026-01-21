package com.example.fatfoxhospital.view.screens.detail

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.fatfoxhospital.R
import com.example.fatfoxhospital.extension.getProfilePainter
import com.example.fatfoxhospital.viewmodel.NurseViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NurseDetailScreen(
    viewModel: NurseViewModel,
    nurseId: Long?,
    onBack: () -> Unit
) {
    val selectedNurse by viewModel.selectedNurse.observeAsState()

    LaunchedEffect(nurseId) {
        if (nurseId != null && selectedNurse?.id != nurseId) {
            val nurse = viewModel.nurses.value?.find { it.id == nurseId }
            nurse?.let { viewModel.selectNurse(it) }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.nurse_detail_title)) },
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.clearSelectedNurse()
                        onBack()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.back_icon_desc))
                    }
                }
            )
        }
    ) { padding ->
        selectedNurse?.let { nurse ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = nurse.getProfilePainter(),
                    contentDescription = "Foto de perfil de ${nurse.name} ${nurse.surname}",
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

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { /* Implementar contacto */ },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.large
                ) {
                    Text(
                        stringResource(R.string.contact_nurse_button),
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
        } ?: run {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.nurse_not_found),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.error
                )
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