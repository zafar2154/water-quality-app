package com.example.waterquality.ui.component.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.waterquality.ui.screen.auth.AuthViewModel
import com.example.waterquality.ui.theme.WaterQualityTheme

@Composable
fun ProfilePopUp(
    username: String,
    email: String,
    onDismiss: () -> Unit,
    onSettingsClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        ProfilePopUpContent( // Call the extracted content here
            onDismiss = onDismiss,
            onSettingsClick = onSettingsClick,
            onLogoutClick = onLogoutClick,
            username = username,
            email = email
        )
    }
}

// Extracted content composable
@Composable
fun ProfilePopUpContent(
    username: String,
    email: String,
    onDismiss: () -> Unit,
    onSettingsClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. Foto Profile (Placeholder)
            Surface(
                modifier = Modifier.size(80.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profile Picture",
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Nama Profile
            Text(
                text = username.ifEmpty { "user" },
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = email.ifEmpty { "email" },
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider() // Ensure this is available or use Divider()
            Spacer(modifier = Modifier.height(8.dp))

            // 3. Menu Settings
            ProfileMenuItem(
                icon = Icons.Default.Settings,
                text = "Pengaturan IP",
                onClick = {
                    onDismiss()
                    onSettingsClick()
                }
            )

            // 4. Menu Logout
            ProfileMenuItem(
                icon = Icons.AutoMirrored.Filled.ExitToApp,
                text = "Logout",
                textColor = MaterialTheme.colorScheme.error,
                iconColor = MaterialTheme.colorScheme.error,
                onClick = {
                    onDismiss()
                    onLogoutClick()
                }
            )
        }
    }
}

@Composable
fun ProfileMenuItem(
    icon: ImageVector,
    text: String,
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    iconColor: Color = MaterialTheme.colorScheme.onSurface,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            tint = iconColor
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = text,
            fontSize = 16.sp,
            color = textColor,
            fontWeight = FontWeight.Medium
        )
    }
}

// Preview the CONTENT, not the Dialog itself
@Preview(showBackground = true)
@Composable
fun ProfilePopUpContentPreview() {
    WaterQualityTheme {
        Box(modifier = Modifier.padding(16.dp)) { // Add padding to see the card shadow
            ProfilePopUpContent(
                onDismiss = {},
                onSettingsClick = {},
                onLogoutClick = {},
                username = "Preview User",
                email = "preview@email.com",
            )
        }
    }
}