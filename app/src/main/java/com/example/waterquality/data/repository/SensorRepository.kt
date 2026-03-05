package com.example.waterquality.data.repository

import android.util.Log
import com.example.waterquality.data.model.SensorResponse
import com.example.waterquality.data.storage.IpDataStore
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

interface SensorRepository {
    fun getSensorStream(): Flow<SensorResponse>
    suspend fun saveIp(ip: String)
}

class SensorRepositoryImpl @Inject constructor(
    private val ipDataStore: IpDataStore,
    private val dbRef: DatabaseReference
) : SensorRepository {

    override fun getSensorStream(): Flow<SensorResponse> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Log untuk memantau data mentah yang masuk dari Cloud
                val data = snapshot.getValue(SensorResponse::class.java)
                if (data != null) {
                    // Jika kamu pakai .push() di Python, id diambil dari snapshot.key
                    // Jika data class SensorResponse punya field 'id', kita pasangkan di sini
                    val dataWithId = data.copy(id = snapshot.key)
                    trySend(dataWithId)
                } else {
                    trySend(SensorResponse(ph = null, tds = null, temperature = null))
                }
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        dbRef.addValueEventListener(listener)

        // Melepaskan listener saat tidak digunakan (agar hemat baterai & data)
        awaitClose {
            dbRef.removeEventListener(listener)
        }
    }

    override suspend fun saveIp(ip: String) {
        ipDataStore.saveIp(ip)
    }
}