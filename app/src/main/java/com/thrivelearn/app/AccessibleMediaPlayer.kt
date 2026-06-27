package com.thrivelearn.app

import android.net.Uri
import android.view.KeyEvent
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.*
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

@OptIn(UnstableApi::class)
@Composable
fun AccessibleMediaPlayer(
    modifier: Modifier = Modifier,
    mediaUri: Uri,
    mediaTitle: String
) {
    val context = LocalContext.current
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(mediaUri))
            prepare()
        }
    }

    // FIXED: Properly dispose of resources
    DisposableEffect(Unit) {
        onDispose {
            try {
                exoPlayer.stop()
                exoPlayer.release()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .semantics {
                contentDescription =
                    "Media player loaded with: $mediaTitle. Use play, pause, and skip controls. Press volume buttons for hardware controls."
                // FIXED: Add role for accessibility
                roleDescription = "Media player"
            }
    ) {
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = exoPlayer
                    setShowNextButton(true)
                    setShowPreviousButton(true)
                    controllerAutoShow = true
                    // FIXED: Enable keyboard controls for accessibility
                    useController = true
                    controllerShowTimeoutMs = 10000 // Show controls for 10 seconds
                }
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}
