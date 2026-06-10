package com.greeffer.xcam.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.greeffer.xcam.data.DefaultXUiState
import com.greeffer.xcam.ui.theme.AppSpacing

@Composable
fun MetadataReadoutPanel(
  settings: DefaultXUiState,
  isCapturing: Boolean,
  modifier: Modifier = Modifier,
)
{
    val recColor by animateColorAsState(
      targetValue = if (isCapturing)
      {
          MaterialTheme.colorScheme.error
      }
      else
      {
          MaterialTheme.colorScheme.onSurfaceVariant
      },
      label = "rec_color",
    )

    GlassSurface(
      modifier = modifier,
      cornerRadius = 8.dp,
    ) {
        Column(
          modifier = Modifier.padding(AppSpacing.PanelPadding),
          verticalArrangement = Arrangement.spacedBy(AppSpacing.Unit),
          horizontalAlignment = Alignment.End,
        ) {
            if (isCapturing)
            {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.End,
                ) {
                    Canvas(modifier = Modifier.size(8.dp)) {
                        drawCircle(
                          color = recColor,
                          radius = size.minDimension / 2f,
                        )
                    }
                }
            }

            MetadataChip(text = "ISO ${settings.iso}")
            MetadataChip(text = "SS ${"%.1f".format(settings.shutter)}s")
            MetadataChip(text = "WB ${settings.whiteBalance}K")
            MetadataChip(text = "EV ${"%.1f".format(settings.evBias)}")

            if (settings.blendMode.isNotEmpty())
            {
                MetadataChip(
                  text = settings.blendMode.uppercase(),
                  textColor = MaterialTheme.colorScheme.secondaryContainer,
                )
            }

            if (settings.blendStrength > 0f)
            {
                MetadataChip(
                  text = "STR ${(settings.blendStrength * 100).toInt()}%",
                  textColor = MaterialTheme.colorScheme.secondaryContainer,
                )
            }

            if (settings.zoomRatio != 1.0f)
            {
                MetadataChip(text = "ZOOM ${"%.1f".format(settings.zoomRatio)}x")
            }

            settings.luminosity?.let { luminosity ->
                MetadataChip(text = "LUM ${"%.1f".format(luminosity)}")
            }
        }
    }
}


@Preview
@Composable
fun MetadataReadoutPanelPreview()
{
    MetadataReadoutPanel(
      settings = DefaultXUiState(
        iso = 100,
        shutter = 1 / 60f,
        whiteBalance = 5500,
        evBias = 0f,
        blendMode = "Normal",
        blendStrength = 0.5f,
        zoomRatio = 1.0f,
        luminosity = 0.5f,
      ),
      isCapturing = true,
    )
}
