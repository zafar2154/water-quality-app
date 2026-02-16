package com.example.waterquality.di

import com.example.waterquality.data.repository.AuthRepository
import com.example.waterquality.data.repository.SensorRepository
import com.example.waterquality.data.repository.SensorRepositoryImpl
import com.example.waterquality.data.storage.IpDataStore
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // --- Firebase Auth ---
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideAuthRepository(firebaseAuth: FirebaseAuth): AuthRepository {
        return AuthRepository(firebaseAuth)
    }

    // --- Sensor & Network (YANG HILANG DI KODE ANDA) ---

    @Provides
    @Singleton
    fun provideRetrofitBuilder(): Retrofit.Builder {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
    }

    @Provides
    @Singleton
    fun provideSensorRepository(
        ipDataStore: IpDataStore,
        retrofitBuilder: Retrofit.Builder
    ): SensorRepository {
        return SensorRepositoryImpl(ipDataStore, retrofitBuilder)
    }
}