package com.greeffer.xcam.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.greeffer.xcam.ui.theme.AppSpacing
import com.greeffer.xcam.ui.theme.Primary
import com.greeffer.xcam.ui.theme.readoutLarge

@Composable
fun TechnicalReadoutStrip(
  iso: Int,
  shutter: Float,
  isCapturing: Boolean,
  exposureDurationMs: Long,
  modifier: Modifier = Modifier
)
{
    val elapsedLabel = if (isCapturing)
    {
        val totalSecs = exposureDurationMs / 1000
        val mins = totalSecs / 60
        val secs = totalSecs % 60
        val tenths = (exposureDurationMs % 1000) / 100
        "${mins}:${secs.toString().padStart(2, '0')}.${tenths}"
    }
    else
    {
        "STANDBY"
    }
    val recColor by animateColorAsState(
      targetValue = if (isCapturing) MaterialTheme.colorScheme.error
      else MaterialTheme.colorScheme.onSurfaceVariant,
      label = "rec_color"
    )

    GlassSurface(
      modifier = modifier,
      cornerRadius = 99.dp
    ) {
        Row(
          modifier = Modifier.padding(
            horizontal = AppSpacing.Gutter,
            vertical = AppSpacing.Unit
          ),
          horizontalArrangement = Arrangement.spacedBy(AppSpacing.Gutter),
          verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
              imageVector = Icons.Default.FiberManualRecord,
              contentDescription = if (isCapturing) "Recording" else "Standby",
              tint = recColor,
              modifier = Modifier.size(12.dp)
            )
            Text(
              text = elapsedLabel,
              color = recColor,
              style = readoutLarge
            )
            Text(
              "ISO $iso",
              color = Primary,
              style = readoutLarge
            )
            Text(
              "SS ${"%.1f".format(shutter)}s",
              color = Primary,
              style = readoutLarge
            )
        }
    }
}
