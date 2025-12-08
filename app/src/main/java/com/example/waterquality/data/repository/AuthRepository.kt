package com.example.waterquality.data.repository

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import com.example.waterquality.utils.Resource // Wrapper yang kita buat sebelumnya
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest

class AuthRepository(private val firebaseAuth: FirebaseAuth) {
    suspend fun register(email: String, pass: String, username: String): Resource<String> {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, pass).await()
            val user = result.user
            if (user != null) {
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(username)
                    .build()
                user.updateProfile(profileUpdates).await()
                Resource.Success(user.uid)
            }
            else {
                Resource.Error("Terjadi kesalahan: User tidak ada")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Register Gagal, hubungi admin")
        }
    }
    // Fungsi login yang mengembalikan Resource (Success/Error)
    suspend fun login(email: String, pass: String): Resource<String> {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, pass).await()
            Resource.Success(result.user?.uid ?: "Unknown ID")
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Login Gagal")
        }
    }

    // Fungsi tambahan: Ambil ID user saat ini (opsional, berguna buat request data)
    fun getCurrentUserId(): String? {
        return firebaseAuth.currentUser?.uid
    }
    fun getCurrentUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }
    // Cek apakah user sudah login
    fun isUserLoggedIn(): Boolean = firebaseAuth.currentUser != null

    fun logout() = firebaseAuth.signOut()
}