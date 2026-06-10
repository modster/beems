package com.greeffer.xcam.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.greeffer.xcam.ui.theme.AppShapes
import com.greeffer.xcam.ui.theme.AppSpacing

@Composable
fun MetadataChip(
  text: String,
  modifier: Modifier = Modifier,
  textColor: Color = MaterialTheme.colorScheme.primaryContainer,
)
{
    Surface(
      shape = AppShapes.Sm,
      color = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.8f),
      modifier = modifier.border(
        1.dp,
        MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
        AppShapes.Sm
      )
    ) {
        Text(
          text = text,
          modifier = Modifier.padding(
            horizontal = AppSpacing.Unit * 2,
            vertical = AppSpacing.Unit
          ),
          style = MaterialTheme.typography.labelSmall,
          color = textColor
        )
    }
}


@Preview
@Composable
fun MetadataChipPreview()
{
    MaterialTheme {
        MetadataChip(text = "ISO 100")
    }
}
