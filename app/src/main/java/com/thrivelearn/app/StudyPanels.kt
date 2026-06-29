package com.thrivelearn.app

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp

@Composable
fun StudyListPanel(
    title: String,
    items: List<String>,
    modifier: Modifier = Modifier,
    textSize: TextUnit = TextUnit.Unspecified
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(title, style = MaterialTheme.typography.titleLarge)
        if (items.isEmpty()) {
            Text("Add text first.", fontSize = textSize, modifier = Modifier.padding(top = 8.dp))
        } else {
            items.forEachIndexed { index, item ->
                StudyPanelSurface {
                    Text(
                        text = "${index + 1}. $item",
                        fontSize = textSize,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun StudyTextPanel(title: String, text: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(title, style = MaterialTheme.typography.titleLarge)
        StudyPanelSurface {
            Text(
                text = text.ifBlank { "Add text first." },
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(12.dp)
            )
        }
    }
}

@Composable
private fun StudyPanelSurface(content: @Composable () -> Unit) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
        content = content
    )
}
