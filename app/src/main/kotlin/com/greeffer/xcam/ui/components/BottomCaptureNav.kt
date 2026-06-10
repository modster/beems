package com.greeffer.xcam.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.greeffer.xcam.ui.theme.AppSpacing
import com.greeffer.xcam.ui.theme.Primary

@Composable
fun BottomCaptureNav(
  modifier: Modifier = Modifier,
  isCapturing: Boolean,
  galleryThumbnailModel: Any? = null,
  onOpenGallery: () -> Unit,
  onCapture: () -> Unit,
  onOpenSettings: () -> Unit,
)
{
    GlassSurface(
      modifier = modifier
        .fillMaxWidth()
        .navigationBarsPadding()
        .padding(horizontal = AppSpacing.Gutter),
      cornerRadius = 24.dp
    ) {
        Row(
          modifier = Modifier.padding(
            horizontal = AppSpacing.Gutter,
            vertical = AppSpacing.Unit
          ),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
            GlassIconButton(
              onClick = onOpenSettings,
              contentDescription = "Mode",
              imageVector = Icons.Default.Settings,
              modifier = Modifier.size(48.dp)
            )
            GlassIconButton(
              onClick = onOpenGallery,
              contentDescription = "Open gallery",
              modifier = Modifier.size(48.dp),
              imageVector = Icons.Default.Photo,
              thumbnailModel = galleryThumbnailModel
            )

            CaptureButton(isCapturing = isCapturing, onClick = onCapture)

            GlassIconButton(
              onClick = {},
              contentDescription = "Effects",
              imageVector = Icons.Default.Cameraswitch,
              modifier = Modifier.size(48.dp)
            )
            GlassIconButton(
              onClick = {},
              contentDescription = "Analytics",
              imageVector = Icons.Default.Settings,
              modifier = Modifier.size(48.dp)
            )
        }
    }
}


@Composable
private fun CaptureButton(
  isCapturing: Boolean,
  onClick: () -> Unit
)
{
    val infiniteTransition = rememberInfiniteTransition(label = "capture_pulse")
    val pulseScale by infiniteTransition.animateFloat(
      initialValue = 1f,
      targetValue = if (isCapturing) 1.18f else 1f,
      animationSpec = infiniteRepeatable(
        animation = tween(durationMillis = 600),
        repeatMode = RepeatMode.Reverse
      ),
      label = "pulse_scale"
    )
    val buttonColor by animateColorAsState(
      targetValue = if (isCapturing) MaterialTheme.colorScheme.errorContainer
      else Primary,
      label = "button_color"
    )
    val iconColor by animateColorAsState(
      targetValue = if (isCapturing) MaterialTheme.colorScheme.onErrorContainer
      else MaterialTheme.colorScheme.onPrimary,
      label = "icon_color"
    )
    val glowColor by animateColorAsState(
      targetValue = if (isCapturing) MaterialTheme.colorScheme.error else Primary,
      label = "glow_color"
    )

    Button(
      onClick = onClick,
      shape = CircleShape,
      colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
      modifier = Modifier
        .size(74.dp)
        .scale(pulseScale)
        .then(
          if (isCapturing) Modifier.neonGlow(glowColor, glowRadius = 16.dp, glowAlpha = 0.5f)
          else Modifier.neonGlow(glowColor, glowRadius = 12.dp, glowAlpha = 0.4f)
        )
    ) {
        Icon(
          imageVector = if (isCapturing) Icons.Default.Stop else Icons.Default.PhotoCamera,
          contentDescription = if (isCapturing) "Stop long exposure" else "Start long exposure",
          tint = iconColor
        )
    }
}


@Composable
private fun GlassIconButton(
  onClick: () -> Unit,
  contentDescription: String,
  modifier: Modifier = Modifier,
  imageVector: ImageVector = Icons.Default.Photo,
  thumbnailModel: Any? = null
)
{
    IconButton(
      onClick = onClick,
      modifier = modifier
        .clip(CircleShape)
        .background(MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.65f))
        .border(
          width = 1.dp,
          color = MaterialTheme.colorScheme.primary.copy(alpha = 0.45f),
          shape = CircleShape
        )
    ) {
        if (thumbnailModel != null)
        {
            AsyncImage(
              model = thumbnailModel,
              contentDescription = contentDescription,
              contentScale = ContentScale.Crop,
              modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
            )
        }
        else
        {
            Icon(
              imageVector = imageVector,
              contentDescription = contentDescription,
              tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}


@Preview
@Composable
fun BottomCaptureNavPreview()
{
    MaterialTheme {
        BottomCaptureNav(
          isCapturing = false,
          onOpenGallery = {
              // Handle gallery open
          },
          onCapture = {},
          onOpenSettings = {

          }
        )
    }
}


@Preview
@Composable
fun BottomCaptureNavCapturingPreview()
{
    MaterialTheme {
        BottomCaptureNav(
          isCapturing = true,
          onOpenGallery = {},
          onCapture = {},
          onOpenSettings = {}
        )
    }
}
