// ui/HeaderWithDialog.kt
package com.example.waterquality.ui.component

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.waterquality.ui.screen.homepage.SensorViewModel
import com.example.waterquality.storage.IpDataStore
import com.example.waterquality.ui.component.auth.ProfilePopUp
import com.example.waterquality.ui.screen.auth.AuthViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavBar(
    viewModel: SensorViewModel,
    onLogout: () -> Unit,
    username: String,
    email: String,
    ) {

    var showDialog by remember { mutableStateOf(false) }
    var ipAddress by remember { mutableStateOf("") }
    var showIpDialog by remember { mutableStateOf(false) }
    var showProfileDialog by remember { mutableStateOf(false) }

    // AMBIL DATA USER DARI VIEWMOD
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
                    imageVector = Icons.Default.AccountCircle,
                    tint = Color.White,
                    contentDescription = "Settings"
                )
            }
        }

    )
    if (showDialog) {
        ProfilePopUp(
            onDismiss = { showDialog = false },
            onSettingsClick = { showIpDialog = true },
            onLogoutClick = {
                onLogout()
                showDialog=false
            },
            username = username,
            email = email
        )
    }
    if (showIpDialog) {
        AlertDialog(
            onDismissRequest = { showIpDialog = false },
            title = {
                Column {
                    Text("Set IP Address")
                    Text("Current : $ipAddress", fontSize = 15.sp, fontWeight = FontWeight.Light)
                }
                    },
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
                    showIpDialog = false
                    viewModel.initApi(ipAddress)
                    scope.launch {
                        IpDataStore.saveIp(context, ipAddress)
                    }
                }) {
                    Text("Simpan")
                }
            },
            dismissButton = {
                TextButton(onClick = { showIpDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }
}