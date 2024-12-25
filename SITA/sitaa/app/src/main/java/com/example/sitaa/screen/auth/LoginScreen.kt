package com.example.sitaa.screen.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sitaa.R
import com.example.sitaa.utils.SharedPref
import kotlinx.coroutines.delay

@Composable
fun CustomAlert(
    message: String,
    isSuccess: Boolean = true,
    show: Boolean = false,
    onDismiss: () -> Unit = {}
) {
    AnimatedVisibility(
        visible = show,
        enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(8.dp),
            tonalElevation = 2.dp,
            color = Color.White
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = if (isSuccess) Icons.Default.CheckCircle else Icons.Default.Error,
                    contentDescription = if (isSuccess) "Success" else "Error",
                    tint = if (isSuccess) Color(0xFF4CAF50) else Color(0xFFE53935),
                    modifier = Modifier.size(24.dp)
                )

                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }

    LaunchedEffect(show) {
        if (show) {
            delay(2000)
            onDismiss()
        }
    }
}

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    viewModel: LoginViewModel = viewModel()
) {
    val context = LocalContext.current
    val sharedPref = remember { SharedPref.getInstance(context) }
    var passwordVisible by remember { mutableStateOf(false) }

    val gradientColors = listOf(
        Color(0xFF64B5F6),
        Color(0xFF1976D2),
        Color(0xFF0D47A1)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = Brush.verticalGradient(colors = gradientColors))
    ) {
        // Success Alert
        CustomAlert(
            message = "Login berhasil!",
            isSuccess = true,
            show = viewModel.showSuccessAlert,
            onDismiss = { viewModel.dismissAlerts() }
        )

        // Error Alert
        CustomAlert(
            message = viewModel.errorMessage ?: "Login gagal",
            isSuccess = false,
            show = viewModel.showErrorAlert,
            onDismiss = { viewModel.dismissAlerts() }
        )

        // Illustration
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 250.dp)
                .height(250.dp)
                .zIndex(1f)
        ) {
            Image(
                painter = painterResource(id = R.drawable.login_illustration),
                contentDescription = "Login Illustration",
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize()
            )
        }

        // White Card Container at the bottom
        Surface(
            modifier = Modifier.align(Alignment.BottomCenter),
            color = Color.White,
            shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(top = 50.dp, bottom = 60.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Username Section
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Username",
                        color = Color.DarkGray,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                    OutlinedTextField(
                        value = viewModel.username,
                        onValueChange = { viewModel.updateUsername(it) },
                        placeholder = { Text("Enter your username", color = Color.Gray) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color(0xFFEEEEEE),
                            focusedBorderColor = Color(0xFF1976D2),
                            unfocusedContainerColor = Color(0xFFF5F5F5),
                            focusedContainerColor = Color(0xFFF5F5F5)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next
                        )
                    )
                }

                // Password Section
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Password",
                        color = Color.DarkGray,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                    OutlinedTextField(
                        value = viewModel.password,
                        onValueChange = { viewModel.updatePassword(it) },
                        placeholder = { Text("Enter your password", color = Color.Gray) },
                        singleLine = true,
                        visualTransformation = if (passwordVisible)
                            VisualTransformation.None
                        else
                            PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible)
                                        Icons.Default.VisibilityOff
                                    else
                                        Icons.Default.Visibility,
                                    contentDescription = if (passwordVisible)
                                        "Hide password"
                                    else
                                        "Show password",
                                    tint = Color.Gray
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color(0xFFEEEEEE),
                            focusedBorderColor = Color(0xFF1976D2),
                            unfocusedContainerColor = Color(0xFFF5F5F5),
                            focusedContainerColor = Color(0xFFF5F5F5)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        )
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Login Button
                Button(
                    onClick = { viewModel.login(sharedPref, onLoginSuccess) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF4081)
                    ),
                    enabled = !viewModel.isLoading,
                    shape = RoundedCornerShape(26.dp)
                ) {
                    if (viewModel.isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text(
                            "Log In",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}