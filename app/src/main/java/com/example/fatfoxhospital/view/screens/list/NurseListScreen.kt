package com.example.fatfoxhospital.view.screens.list

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import com.example.fatfoxhospital.viewmodel.uistate.NurseListUiState

var dataLoaded: Boolean = false

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NurseListScreen(
    viewModel: NurseViewModel,
    onNurseClick: (Nurse) -> Unit,
    onBack: () -> Unit
) {
    val nurseListUiState = viewModel.nurseListUiState
    if(!dataLoaded){
        viewModel.getAll()
        dataLoaded = true
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(R.string.all_nurses_title),
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            stringResource(R.string.back_button)
                        )
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when (nurseListUiState) {
                is NurseListUiState.Loading -> item { Text(stringResource(R.string.Loading)) }
                is NurseListUiState.Error -> item {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = stringResource(R.string.ErrorLoading),
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.Bold
                        )
                        Button(onClick = { viewModel.getAll() }) {
                            Text(stringResource(R.string.Retry))
                        }
                    }
                }
                is NurseListUiState.Success -> {
                    items(nurseListUiState.nurseList, key = { it.id!! }) { nurse ->
                        NurseItem(nurse = nurse, onClick = { onNurseClick(nurse) })
                    }
                }
            }
        }
    }
}

@Composable
fun NurseItem(nurse: Nurse, onClick: () -> Unit) {
    ElevatedCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = nurse.getProfilePainter(),
                contentDescription = "${stringResource(R.string.ProfilePicture)} ${nurse.name} ${nurse.surname}",
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.width(20.dp))

            Column {
                Text(
                    text = "${nurse.name} ${nurse.surname}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.username_label, nurse.user),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}