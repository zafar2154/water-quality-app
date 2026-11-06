package com.example.waterquality.ui.component

import androidx.compose.foundation.border
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.waterquality.ui.component.auth.ProfilePopUp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavBar(
    onLogout: () -> Unit,
    username: String,
    email: String,
) {
    var showProfileDialog by remember { mutableStateOf(false) }

    TopAppBar(
        title = { Text("WaterQuality", color = Color.White) },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        modifier = Modifier
            .border(1.dp, Color.DarkGray),
        actions = {
            IconButton(onClick = { showProfileDialog = true }) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    tint = Color.White,
                    contentDescription = "Profil"
                )
            }
        }
    )

    if (showProfileDialog) {
        ProfilePopUp(
            onDismiss = { showProfileDialog = false },
            onLogoutClick = {
                onLogout()
                showProfileDialog = false
            },
            username = username,
            email = email
        )
    }
}
