package com.example.waterquality.data.repository

import android.util.Log
import com.example.waterquality.data.model.SensorResponse
import com.example.waterquality.data.storage.IpDataStore
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Named

interface SensorRepository {
    fun getLiveSensorStream(): Flow<SensorResponse>
    fun getHistoryStream(): Flow<SensorResponse>
    suspend fun saveIp(ip: String)
}

class SensorRepositoryImpl @Inject constructor(
    private val ipDataStore: IpDataStore,
    @Named("live") private val liveRef: DatabaseReference,
    @Named("history") private val historyRef: DatabaseReference
) : SensorRepository {
    /**
     * Mendengarkan node live_status yang di-set() oleh Python setiap 5 detik.
     * Node ini selalu flat (tidak ada child key), jadi getValue() bekerja sempurna.
     */
    override fun getLiveSensorStream(): Flow<SensorResponse> = callbackFlow {
        val liveListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Log untuk memantau data mentah yang masuk dari Cloud
                val data = snapshot.getValue(SensorResponse::class.java)
                if (data != null) {
                trySend(data)
                } else {
                    Log.w("SensorRepo", "live_status null atau format salah")
                    trySend(SensorResponse())
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("SensorRepo", "getLiveSensorStream cancelled: ${error.message}")
                close(error.toException())
            }
        }
        liveRef.addValueEventListener(liveListener)
        awaitClose { liveRef.removeEventListener(liveListener) }
    }
    /**
     * Mendengarkan node sensor_history yang di-push() oleh Python setiap ~50 detik
     * atau saat tombol simpan ditekan di hardware ESP32.
     *
     * Menggunakan ChildEventListener agar setiap record diterima satu per satu
     * sebagai SensorResponse lengkap dengan id-nya (snapshot.key).
     *
     * limitToLast(50) → hanya ambil 50 data terbaru agar tidak membebani memori.
     */
    override fun getHistoryStream(): Flow<SensorResponse> = callbackFlow {
        val listener = object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val data = snapshot.getValue(SensorResponse::class.java)
                    ?.copy(id = snapshot.key)
                if (data != null) {
                    trySend(data)
                } else {
                    Log.w("SensorRepo", "History record null: ${snapshot.key}")
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previusChildName: String?) {
                val data = snapshot.getValue(SensorResponse::class.java)
                    ?.copy(id = snapshot.key)
                if (data != null) trySend(data)
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
            }
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                // Tidak relevan untuk data sensor
            }
            override fun onCancelled(error: DatabaseError) {
                Log.e("SensorRepo", "getHistoryStream cancelled: ${error.message}")
                close(error.toException())
            }
        }
        historyRef.limitToLast(50).addChildEventListener(listener)
        awaitClose { historyRef.removeEventListener(listener) }
    }
    override suspend fun saveIp(ip: String) {
        ipDataStore.saveIp(ip)
    }
}