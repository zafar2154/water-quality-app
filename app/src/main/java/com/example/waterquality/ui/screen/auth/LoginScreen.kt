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
// 1. STATEFUL COMPONENT
// ==========================================
@Composable
fun LoginScreen(
    navController: NavController,
    // Menggunakan hiltViewModel agar sejalan dengan optimasi sebelumnya
    authViewModel: AuthViewModel = hiltViewModel()
) {
    // Collect semua State dari ViewModel
    val email by authViewModel.email.collectAsState()
    val password by authViewModel.password.collectAsState()
    val loginState by authViewModel.loginState.collectAsState()
    val errorMessage by authViewModel.errorMessage.collectAsState()

    val context = LocalContext.current
    val isLoading = loginState is Resource.Loading

    // Menangani Efek Samping (Navigasi & Toast)
    LaunchedEffect(loginState) {
        when (val state = loginState) {
            is Resource.Success -> {
                navController.navigate(Routes.HOME) {
                    popUpTo(Routes.LOGIN) { inclusive = true }
                }
            }
            is Resource.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
            }
            else -> {}
        }
    }

    // Panggil komponen Stateless dan lempar semua data & aksinya
    LoginScreenContent(
        email = email,
        password = password,
        onEmailChange = { authViewModel.onEmailChange(it) },
        onPasswordChange = { authViewModel.onPasswordChange(it) },
        onLoginClick = {
            authViewModel.clearError()
            authViewModel.login() // Tidak perlu lempar parameter lagi
        },
        onNavigateToRegister = { navController.navigate("register") },
        isLoading = isLoading,
        errorMessage = errorMessage
    )
}


// ==========================================
// 2. STATELESS COMPONENT (Bodoh - Murni UI)
// ==========================================
@Composable
fun LoginScreenContent(
    email: String,
    password: String,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    onNavigateToRegister: () -> Unit,
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
        Text("Login", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            label = { Text("Email") },
            singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Tampilkan pesan error jika ada
        errorMessage?.let {
            Text(text = it, color = Color.Red)
            Spacer(modifier = Modifier.height(8.dp))
        }

        Button(
            onClick = onLoginClick,
            // Opsional: Tombol otomatis disabled jika form kosong atau sedang loading
            enabled = !isLoading && email.isNotBlank() && password.isNotBlank()
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Login")
            }
        }

        TextButton(onClick = onNavigateToRegister) {
            Text("Belum punya akun? Daftar di sini")
        }
    }
}


// ==========================================
// 3. PREVIEW
// ==========================================
@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    WaterQualityTheme {
        // Cukup panggil yang Stateless, masukkan data palsu
        LoginScreenContent(
            email = "afit@example.com",
            password = "mypassword123",
            onEmailChange = {},
            onPasswordChange = {},
            onLoginClick = {},
            onNavigateToRegister = {},
            isLoading = false,
            errorMessage = null // Coba isi dengan "Email salah" untuk tes UI error
        )
    }
}