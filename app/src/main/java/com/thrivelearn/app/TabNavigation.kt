package com.thrivelearn.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class TabItem(
    val id: Int,
    val label: String,
    val icon: ImageVector,
    val contentDescription: String
)

val appTabs = listOf(
    TabItem(
        id = 0,
        label = "Notes",
        icon = Icons.Filled.Home,
        contentDescription = "Quick note taking and voice dictation"
    ),
    TabItem(
        id = 1,
        label = "Library",
        icon = Icons.Filled.LibraryBooks,
        contentDescription = "Study materials library with audio and documents"
    ),
    TabItem(
        id = 2,
        label = "Settings",
        icon = Icons.Filled.Settings,
        contentDescription = "App settings and accessibility options"
    )
)

@Composable
fun TabNavigationBar(
    currentTab: Int,
    onTabChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp)
            .semantics { contentDescription = "Navigation tabs" },
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            appTabs.forEach { tab ->
                TabButton(
                    tab = tab,
                    isSelected = currentTab == tab.id,
                    onClick = { onTabChange(tab.id) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun TabButton(
    tab: TabItem,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .semantics { contentDescription = "${tab.label} tab button" },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = onClick,
            modifier = Modifier
                .size(56.dp)
                .semantics { contentDescription = tab.contentDescription },
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isSelected)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                contentColor = if (isSelected)
                    MaterialTheme.colorScheme.onPrimary
                else
                    MaterialTheme.colorScheme.onSurfaceVariant
            )
        ) {
            Icon(
                imageVector = tab.icon,
                contentDescription = tab.label,
                modifier = Modifier.size(28.dp)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = tab.label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            fontSize = 12.sp,
            color = if (isSelected)
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.semantics { contentDescription = "${tab.label} label" }
        )
    }
}
