package com.example.waterquality.di

import com.example.waterquality.data.repository.AuthRepository
import com.example.waterquality.data.repository.SensorRepository
import com.example.waterquality.data.repository.SensorRepositoryImpl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    const val databaseUrl = "https://water-quality-b7b81-default-rtdb.asia-southeast1.firebasedatabase.app/"

    // --- Firebase Auth ---
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideAuthRepository(firebaseAuth: FirebaseAuth): AuthRepository {
        return AuthRepository(firebaseAuth)
    }

    // --- Firebase Realtime Database : live_status (perangkat set() tiap 5 detik) ---
    @Provides @Singleton
    @Named("live")
    fun provideLiveRef(): DatabaseReference {
        return FirebaseDatabase.getInstance(databaseUrl)
            .getReference("live_status")
    }

    // --- Firebase Realtime Database : sensor_history (perangkat push()) ---
    @Provides @Singleton
    @Named("history")
    fun provideHistoryRef(): DatabaseReference {
        return FirebaseDatabase.getInstance(databaseUrl)
            .getReference("sensor_history")
    }

    // --- Sensor Repository Binding ---
    @Provides
    @Singleton
    fun provideSensorRepository(
        @Named("live") liveRef: DatabaseReference,
        @Named("history") historyRef: DatabaseReference
    ): SensorRepository {
        return SensorRepositoryImpl(liveRef, historyRef)
    }
}
