// ui/HeaderWithDialog.kt
package com.example.waterquality.ui

import androidx.compose.foundation.border
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.waterquality.SensorViewModel
import com.example.waterquality.storage.IpDataStore
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavBar(viewModel: SensorViewModel) {
    var showDialog by remember { mutableStateOf(false) }
    var ipAddress by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    TopAppBar(
        title = { Text("WaterQuality", color = Color.White) },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        modifier = Modifier
            .border(1.dp, Color.DarkGray),
        actions = {
            IconButton(onClick = {showDialog = true}) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    tint = Color.White,
                    contentDescription = "Settings"
                )
            }
        }

    )
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Set IP Address") },
            text = {
                OutlinedTextField(
                    value = ipAddress,
                    onValueChange = { ipAddress = it },
                    label = { Text("Masukkan IP Address") },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showDialog = false
                    viewModel.initApi(ipAddress)
                    scope.launch {
                        IpDataStore.saveIp(context, ipAddress)
                    }
                }) {
                    Text("Simpan")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }
}
