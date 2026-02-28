package com.example.waterquality.ui.screen.auth

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.waterquality.navigation.Routes
import com.example.waterquality.utils.Resource
import com.example.waterquality.ui.theme.WaterQualityTheme

// ==========================================
// 1. STATEFUL COMPONENT (Pintar - Pengendali)
// ==========================================
@Composable
fun RegisterScreen(
    navController: NavController,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    // Collect semua State dari ViewModel
    val username by authViewModel.username.collectAsState()
    val email by authViewModel.email.collectAsState()
    val password by authViewModel.password.collectAsState()
    val loginState by authViewModel.loginState.collectAsState()
    val errorMessage by authViewModel.errorMessage.collectAsState()

    val context = LocalContext.current
    val isLoading = loginState is Resource.Loading

    // Menangani Navigasi dan Toast
    LaunchedEffect(loginState) {
        when (val state = loginState) {
            is Resource.Success -> {
                navController.navigate(Routes.HOME) {
                    popUpTo(Routes.REGISTER) { inclusive = true }
                }
            }
            is Resource.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
            }
            else -> {}
        }
    }

    // Panggil komponen Stateless
    RegisterScreenContent(
        username = username,
        email = email,
        password = password,
        onUsernameChange = { authViewModel.onUsernameChange(it) },
        onEmailChange = { authViewModel.onEmailChange(it) },
        onPasswordChange = { authViewModel.onPasswordChange(it) },
        onRegisterClick = {
            authViewModel.clearError()
            authViewModel.register() // Panggil tanpa parameter
        },
        onNavigateToLogin = { navController.navigate("login") },
        isLoading = isLoading,
        errorMessage = errorMessage
    )
}

// ==========================================
// 2. STATELESS COMPONENT (Bodoh - Murni UI)
// ==========================================
@Composable
fun RegisterScreenContent(
    username: String,
    email: String,
    password: String,
    onUsernameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onRegisterClick: () -> Unit,
    onNavigateToLogin: () -> Unit,
    isLoading: Boolean,
    errorMessage: String?
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Daftar Akun", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = username,
            onValueChange = onUsernameChange,
            label = { Text("Nama Lengkap / Username") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(16.dp))

        errorMessage?.let {
            Text(text = it, color = Color.Red)
            Spacer(modifier = Modifier.height(8.dp))
        }

        Button(
            onClick = onRegisterClick,
            // Tombol mati jika ada field kosong atau sedang loading
            enabled = !isLoading && username.isNotBlank() && email.isNotBlank() && password.isNotBlank(),
            modifier = Modifier.fillMaxWidth(0.5f)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Daftar")
            }
        }

        TextButton(onClick = onNavigateToLogin) {
            Text("Sudah punya akun? Login")
        }
    }
}

// ==========================================
// 3. PREVIEW
// ==========================================
@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    WaterQualityTheme {
        RegisterScreenContent(
            username = "",
            email = "",
            password = "",
            onUsernameChange = {},
            onEmailChange = {},
            onPasswordChange = {},
            onRegisterClick = {},
            onNavigateToLogin = {},
            isLoading = false,
            errorMessage = null
        )
    }
}