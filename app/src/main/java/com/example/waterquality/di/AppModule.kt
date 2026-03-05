package com.example.waterquality.di

import android.content.Context
import com.example.waterquality.data.repository.AuthRepository
import com.example.waterquality.data.repository.SensorRepository
import com.example.waterquality.data.repository.SensorRepositoryImpl
import com.example.waterquality.data.storage.IpDataStore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
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

    // --- Firebase Realtime Database ---
    @Provides
    @Singleton
    fun provideFirebaseDatabase(): DatabaseReference {
        val databaseUrl = "https://water-quality-b7b81-default-rtdb.asia-southeast1.firebasedatabase.app/"
        // Node "sensor_data" harus SAMA dengan yang ada di script Python
        return FirebaseDatabase.getInstance(databaseUrl).getReference("sensor_data")
    }

    // --- Data Storage (IpDataStore) ---
    @Provides
    @Singleton
    fun provideIpDataStore(@ApplicationContext context: Context): IpDataStore {
        return IpDataStore(context)
    }

    // --- Sensor Repository Binding ---
    @Provides
    @Singleton
    fun provideSensorRepository(
        ipDataStore: IpDataStore,
        dbRef: DatabaseReference
    ): SensorRepository {
        return SensorRepositoryImpl(ipDataStore, dbRef)
    }

    // --- Retrofit Builder (Opsional: Jika masih ada komponen yang butuh) ---
    @Provides
    @Singleton
    fun provideRetrofitBuilder(): Retrofit.Builder {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
    }
}