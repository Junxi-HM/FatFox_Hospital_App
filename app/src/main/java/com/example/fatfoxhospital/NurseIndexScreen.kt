package com.example.fatfoxhospital

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fatfoxhospital.ui.theme.FatFoxHospitalTheme

@OptIn(ExperimentalMaterial3Api::class)
class NurseIndexScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            FatFoxHospitalTheme {
                NurseIndexScreen(nurses = NurseList.mockNurses)
            }
        }
    }
}

// ITEM OF NURSE
@Composable
fun NurseItem(nurse: Nurse, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Nurse Icon",
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "${nurse.name} ${nurse.surname}",
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = stringResource(R.string.nurse_index_username) + ": ${nurse.user}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}

// MAIN SCREEN
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NurseIndexScreen(nurses: List<Nurse>) {
    val context = LocalContext.current
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = {
                        // Ir al main menu
                        val intent = Intent(context, NurseMainScreen::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        context.startActivity(intent)
                    }) {
                        Icon(
                            imageVector = Icons.Default.LocalHospital,
                            contentDescription = "Hospital Icon",
                            modifier = Modifier.size(40.dp)
                        )
                    }
                },
                title = {
                    Text(
                        text = stringResource(R.string.nurse_index_title),
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(8.dp)
        ) {
            items(nurses) { nurse ->
                NurseItem(nurse)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewNurseIndexScreen() {
    FatFoxHospitalTheme {
        NurseIndexScreen(nurses = NurseList.mockNurses)
    }
}
