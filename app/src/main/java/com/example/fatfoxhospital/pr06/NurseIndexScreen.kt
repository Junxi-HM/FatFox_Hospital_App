package com.example.fatfoxhospital.pr06

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fatfoxhospital.R
import com.example.fatfoxhospital.ui.theme.FatFoxHospitalTheme

@OptIn(ExperimentalMaterial3Api::class)
class NurseIndexScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FatFoxHospitalTheme {
                NurseIndexScreenContent(nurses = NurseList.mockNurses)
            }
        }
    }
}

// ITEM OF NURSE (Consistent with Search.kt style)
@Composable
fun NurseItem(nurse: Nurse, modifier: Modifier = Modifier) {
    ElevatedCard( // Changed to ElevatedCard
        modifier = modifier
            .fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(24.dp), // Rounded corners for consistency
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar Circle (Consistent with Search.kt)
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
            ) {
                // Initial calculation logic from Search.kt
                val initials = if (nurse.name.isNotEmpty() && nurse.surname.isNotEmpty()) {
                    "${nurse.name.first()}${nurse.surname.first()}"
                } else "?"

                Text(
                    text = initials,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }

            Spacer(modifier = Modifier.width(20.dp))

            Column {
                Text(
                    text = "${nurse.name} ${nurse.surname}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${stringResource(R.string.nurse_index_username)}: ${nurse.user}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// MAIN SCREEN
@Composable
fun NurseIndexScreenContent(nurses: List<Nurse>) {
    val context = LocalContext.current

    // Replacing Scaffold TopBar with custom Column layout for same-line header consistency
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .paint(
                painterResource(id = R.drawable.appbg),
                contentScale = ContentScale.FillBounds
            )
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // --- Header Row (Back Icon and Title on the SAME LINE) ---
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 24.dp)
        ) {
            IconButton(
                onClick = {
                    // Logic Unchanged: Go back to main menu
                    val intent = Intent(context, NurseMainScreen::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    context.startActivity(intent)
                },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack, // Standard Back Icon
                    contentDescription = "Back",
                    modifier = Modifier.size(28.dp),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = stringResource(R.string.nurse_index_title),
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-0.5).sp
                )
            )
        }

        // --- List Content ---
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
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
        NurseIndexScreenContent(nurses = NurseList.mockNurses)
    }
}