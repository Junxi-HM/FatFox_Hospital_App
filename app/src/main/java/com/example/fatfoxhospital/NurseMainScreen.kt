package com.example.fatfoxhospital

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fatfoxhospital.ui.theme.FatFoxHospitalTheme
import kotlin.reflect.KClass

@OptIn(ExperimentalMaterial3Api::class)
class NurseMainScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            FatFoxHospitalTheme {
                NurseMainScreenContent(screens = screens)
            }
        }
    }
}

// Screen Model
class Screen(
    val title: String,
    val description: String,
    val targetActivity: KClass<out AppCompatActivity>? = null
)

val screens = listOf(
    Screen("Login Screen", "Screen to login to the application", LoginActivity::class),
    Screen("Index Screen", "Screen where you can see all the nurses data", NurseIndexScreen::class),
    Screen("Search Screen", "Screen to search a nurse by name", Search::class),
)

@Composable
fun ScreenItem(screen: Screen, context: Context, modifier: Modifier = Modifier) {
    Card(
        onClick = {
            screen.targetActivity?.let { target ->
                val intent = Intent(context, target.java)
                context.startActivity(intent)
            }
        },
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.LocalHospital,
                contentDescription = "Hospital Icon",
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = screen.title,
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = screen.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun NurseMainScreenContent(screens: List<Screen>) {
    val context = LocalContext.current
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = {
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
                        "Nurse Main Screen",
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
            items(screens) { screen ->
                ScreenItem(screen, context)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewNurseMainScreen() {
    FatFoxHospitalTheme {
        NurseMainScreenContent(screens)
    }
}
