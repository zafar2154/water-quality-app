package com.example.waterquality.ui.screen.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.waterquality.data.repository.AuthRepository
import com.example.waterquality.utils.Resource
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class AuthViewModel @Inject constructor(private val repository: AuthRepository) : ViewModel() {
    // State untuk menampung pesan error
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage
    private val _loginState = MutableStateFlow<Resource<String>?>(null)

    val loginState = _loginState.asStateFlow()

    fun register(email: String, pass: String, username: String) {
        _loginState.value = Resource.Loading()
        viewModelScope.launch {
            val result = repository.register(email, pass, username)
            _loginState.value = result // Update state UI
        }
    }
    fun login(email: String, pass: String) {
        _loginState.value = Resource.Loading()
        viewModelScope.launch {
            val result = repository.login(email, pass)
            _loginState.value = result
        }
    }

    fun logout() {
        repository.logout()
        _loginState.value = null
    }

    // Cek status login saat aplikasi baru dibuka
    fun isUserLoggedIn() = repository.isUserLoggedIn()

    fun clearError() {
        _errorMessage.value = null
    }
    // Properti untuk mengambil user saat ini
    val currentUser: FirebaseUser?
        get() = repository.getCurrentUser()
}