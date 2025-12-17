package com.example.fatfoxhospital.pr07.view.screens.login

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.fatfoxhospital.R
import com.example.fatfoxhospital.pr07.viewmodel.NurseViewModel

@Composable
fun NurseLoginScreen(viewModel: NurseViewModel, navController: NavHostController) {

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val loginSuccessMessage = stringResource(R.string.login_success_toast)
    val loginFailedMessage = stringResource(R.string.login_failed_toast)
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .paint(
                painter = painterResource(id = R.drawable.loginbg),
                contentScale = ContentScale.FillBounds
            ),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(30.dp),
            shape = RoundedCornerShape(30.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(30.dp)
                    .clip(RoundedCornerShape(30.dp)),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.nurse_login_title),
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(id = R.color.lavender),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text(stringResource(R.string.user_label), fontSize = 15.sp) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = stringResource(R.string.user_icon_desc),
                            tint = colorResource(id = R.color.lavender)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 30.dp)
                        .height(60.dp),
                    singleLine = true,
                    shape = RoundedCornerShape(30.dp)
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text(stringResource(R.string.password_label_login), fontSize = 15.sp) },
                    visualTransformation = PasswordVisualTransformation(),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = stringResource(R.string.lock_icon_desc),
                            tint = colorResource(id = R.color.lavender)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 15.dp)
                        .height(60.dp),
                    singleLine = true,
                    shape = RoundedCornerShape(30.dp)
                )

                Spacer(modifier = Modifier.height(30.dp))

                Button(
                    onClick = {
                        val isValidUser = viewModel.loginAuthenticate(username, password)

                        if (isValidUser) {
                            Toast.makeText(context, loginSuccessMessage, Toast.LENGTH_SHORT).show()
                            navController.navigate("home") {
                                popUpTo("login") { inclusive = true }
                            }
                        } else {
                            Toast.makeText(context, loginFailedMessage, Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(id = R.color.lavender)
                    ),
                    shape = RoundedCornerShape(30.dp)
                ) {
                    Text(
                        text = stringResource(R.string.nurse_login_title_capital_letters),
                        fontSize = 20.sp,
                        color = colorResource(id = R.color.black)
                    )
                }

                TextButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.padding(top = 16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(id = R.color.light_gray_50)
                    ),
                    shape = RoundedCornerShape(5.dp),
                ) {
                    Text(
                        text = stringResource(R.string.back_to_home_text),
                        fontSize = 15.sp,
                        color = colorResource(
                            id = R.color.black
                        ),
                    )
                }
            }
        }
    }
}