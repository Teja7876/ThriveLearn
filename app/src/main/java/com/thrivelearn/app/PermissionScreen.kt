package com.thrivelearn.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PermissionScreen(
    onPermissionsGranted: () -> Unit,
    onRequestPermissions: (List<String>) -> Unit
) {
    val context = LocalContext.current
    val permissions = remember { PermissionManager.getPermissionInfo(context) }
    val allGranted = remember { permissions.all { it.isGranted } }

    LaunchedEffect(Unit) {
        if (allGranted) {
            onPermissionsGranted()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
            .semantics { contentDescription = "Permissions screen" },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "🔐 Permissions Required",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(bottom = 12.dp)
                    .semantics { contentDescription = "Permissions required title" }
            )

            Text(
                text = "ThriveLearn needs access to your microphone and files to provide the best learning experience.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.semantics {
                    contentDescription =
                        "ThriveLearn needs microphone and file access for core features"
                }
            )
        }

        // Permissions List
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .semantics { contentDescription = "Permissions list" },
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(permissions) { permission ->
                PermissionCard(
                    permission = permission,
                    modifier = Modifier.semantics {
                        contentDescription = "${permission.displayName}: ${if (permission.isGranted) "Granted" else "Not granted"}"
                    }
                )
            }
        }

        // Request Permissions Button
        Button(
            onClick = {
                val permissionsToRequest = PermissionManager.getPermissionsToRequest(context)
                if (permissionsToRequest.isNotEmpty()) {
                    onRequestPermissions(permissionsToRequest)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .semantics { contentDescription = "Request permissions button" },
            enabled = !allGranted
        ) {
            Text(
                text = if (allGranted) "✓ All Permissions Granted" else "Grant Permissions",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun PermissionCard(
    permission: PermissionInfo,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .semantics { contentDescription = permission.displayName },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (permission.isGranted)
                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            else
                MaterialTheme.colorScheme.error.copy(alpha = 0.1f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (permission.isGranted) Icons.Filled.CheckCircle else Icons.Filled.Warning,
                contentDescription = if (permission.isGranted) "Granted" else "Not granted",
                tint = if (permission.isGranted)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.error,
                modifier = Modifier.size(28.dp)
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = permission.displayName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = permission.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
