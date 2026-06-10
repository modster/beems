package com.greeffer.xcam.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.greeffer.xcam.ui.theme.labelCaps

@Composable
fun SegmentedControl(
  items: List<String>,
  selectedIndex: Int,
  onItemSelected: (Int) -> Unit,
  modifier: Modifier = Modifier,
  activeColor: Color = MaterialTheme.colorScheme.primary,
  inactiveColor: Color = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.3f),
  strokeColor: Color = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
  cornerRadius: Dp = 8.dp
)
{
    Row(
      modifier = modifier
        .clip(RoundedCornerShape(cornerRadius))
        .background(inactiveColor)
        .border(
          width = 1.dp,
          color = strokeColor,
          shape = RoundedCornerShape(cornerRadius)
        )
        .padding(4.dp),
      horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        items.forEachIndexed { index, item ->
            val isSelected = index == selectedIndex
            val backgroundColor by animateColorAsState(
              targetValue = if (isSelected) MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.8f)
              else Color.Transparent,
              animationSpec = tween(durationMillis = 200),
              label = "segment_bg"
            )
            val textColor by animateColorAsState(
              targetValue = if (isSelected) activeColor else MaterialTheme.colorScheme.onSurfaceVariant,
              animationSpec = tween(durationMillis = 200),
              label = "segment_text"
            )

            Box(
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(cornerRadius))
                .background(backgroundColor)
                .clickable { onItemSelected(index) }
                .padding(vertical = 8.dp, horizontal = 16.dp),
              contentAlignment = Alignment.Center
            ) {
                Text(
                  text = item.uppercase(),
                  style = labelCaps,
                  color = textColor
                )
            }
        }
    }
}


@Preview
@Composable
fun SegmentedControlPreview()
{
    MaterialTheme {
        SegmentedControl(
          items = listOf("Auto", "Manual", "Pro"),
          selectedIndex = 1,
          onItemSelected = {},
          modifier = Modifier.padding(16.dp)
        )
    }
}
