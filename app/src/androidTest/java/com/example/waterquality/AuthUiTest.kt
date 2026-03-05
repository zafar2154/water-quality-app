package com.example.waterquality

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.waterquality.ui.screen.auth.LoginScreenContent
import com.example.waterquality.ui.theme.WaterQualityTheme
import org.junit.Rule
import org.junit.Test

class AuthUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun loginScreen_TombolDisabledJikaFormKosong() {
        // 1. Jalankan UI Login (Stateless version agar mudah dites)
        composeTestRule.setContent {
            WaterQualityTheme {
                LoginScreenContent(
                    email = "",
                    password = "",
                    onEmailChange = {},
                    onPasswordChange = {},
                    onLoginClick = {},
                    onNavigateToRegister = {},
                    isLoading = false,
                    errorMessage = null
                )
            }
        }

        // 2. Pastikan tombol Login tidak bisa diklik (Disabled)
        composeTestRule.onNodeWithText("Login").assertIsNotEnabled()
    }

    @Test
    fun loginScreen_MenampilkanErrorJikaAdaPesanError() {
        val pesanError = "Email atau Password Salah"

        composeTestRule.setContent {
            WaterQualityTheme {
                LoginScreenContent(
                    email = "afit@test.com",
                    password = "123",
                    onEmailChange = {},
                    onPasswordChange = {},
                    onLoginClick = {},
                    onNavigateToRegister = {},
                    isLoading = false,
                    errorMessage = pesanError
                )
            }
        }

        // Pastikan teks error muncul di layar
        composeTestRule.onNodeWithText(pesanError).assertIsDisplayed()
    }

    @Test
    fun loginScreen_MenampilkanLoadingSaatProses() {
        composeTestRule.setContent {
            WaterQualityTheme {
                LoginScreenContent(
                    email = "afit@test.com",
                    password = "123",
                    onEmailChange = {},
                    onPasswordChange = {},
                    onLoginClick = {},
                    onNavigateToRegister = {},
                    isLoading = true, // Simulasi sedang loading
                    errorMessage = null
                )
            }
        }

        // Karena CircularProgressIndicator tidak punya teks,
        // kita bisa mengecek apakah tombol Login menghilang/digantikan
        composeTestRule.onNodeWithText("Login").assertDoesNotExist()
    }
}