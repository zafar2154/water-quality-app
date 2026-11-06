package com.example.waterquality.data.repository

import android.util.Log
import com.example.waterquality.data.model.SensorResponse
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseException
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.transformLatest
import javax.inject.Inject
import javax.inject.Named

interface SensorRepository {
    fun getLiveSensorStream(): Flow<SensorResponse>
    fun getHistoryStream(): Flow<SensorResponse>

    /** true jika HP sedang terhubung ke server Firebase Realtime Database. */
    fun observeFirebaseConnection(): Flow<Boolean>
}

/** Jeda sebelum status "terputus" dilaporkan, untuk meredam kedipan saat app baru dibuka. */
private const val DISCONNECT_GRACE_MS = 3_000L

/**
 * Parse snapshot ke SensorResponse tanpa melempar exception.
 * getValue() akan throw DatabaseException kalau tipe field tidak cocok
 * (misal perangkat mengirim "ph": "nan" sebagai string). Itu tidak boleh
 * membuat app crash, cukup record tersebut dilewati.
 */
private fun DataSnapshot.toSensorResponseOrNull(): SensorResponse? = try {
    getValue(SensorResponse::class.java)
} catch (e: DatabaseException) {
    Log.w("SensorRepo", "Format data tidak valid di node '$key': ${e.message}")
    null
}

class SensorRepositoryImpl @Inject constructor(
    @Named("live") private val liveRef: DatabaseReference,
    @Named("history") private val historyRef: DatabaseReference
) : SensorRepository {
    /**
     * Mendengarkan node live_status yang di-set() oleh perangkat setiap 5 detik.
     * Error dari Firebase (misal permission denied) diteruskan sebagai exception
     * pada Flow, jadi collector WAJIB menangkapnya dengan .catch {}.
     */
    override fun getLiveSensorStream(): Flow<SensorResponse> = callbackFlow {
        val liveListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists()) {
                    Log.w("SensorRepo", "live_status kosong")
                    trySend(SensorResponse())
                    return
                }
                snapshot.toSensorResponseOrNull()?.let { trySend(it) }
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
     * Mendengarkan node sensor_history yang di-push() oleh perangkat.
     * limitToLast(50) -> hanya ambil 50 data terbaru agar tidak membebani memori.
     */
    override fun getHistoryStream(): Flow<SensorResponse> = callbackFlow {
        val listener = object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val data = snapshot.toSensorResponseOrNull()?.copy(id = snapshot.key)
                if (data != null) trySend(data)
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val data = snapshot.toSensorResponseOrNull()?.copy(id = snapshot.key)
                if (data != null) trySend(data)
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onCancelled(error: DatabaseError) {
                Log.e("SensorRepo", "getHistoryStream cancelled: ${error.message}")
                close(error.toException())
            }
        }
        // Listener harus dilepas dari query yang SAMA dengan saat dipasang
        // (bukan dari historyRef), supaya benar-benar terlepas saat logout.
        val query = historyRef.limitToLast(50)
        query.addChildEventListener(listener)
        awaitClose { query.removeEventListener(listener) }
    }

    /**
     * Mendengarkan node khusus `.info/connected` milik Firebase.
     * Saat app baru dibuka nilainya selalu false sebentar sebelum tersambung,
     * jadi nilai false ditahan [DISCONNECT_GRACE_MS] dulu; jika dalam jeda itu
     * sudah tersambung, false tidak pernah dilaporkan.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun observeFirebaseConnection(): Flow<Boolean> = callbackFlow {
        val connectedRef = liveRef.database.getReference(".info/connected")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                trySend(snapshot.getValue(Boolean::class.java) ?: false)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("SensorRepo", "observeFirebaseConnection cancelled: ${error.message}")
                close(error.toException())
            }
        }
        connectedRef.addValueEventListener(listener)
        awaitClose { connectedRef.removeEventListener(listener) }
    }
        .distinctUntilChanged()
        .transformLatest { connected ->
            if (!connected) delay(DISCONNECT_GRACE_MS)
            emit(connected)
        }
}
