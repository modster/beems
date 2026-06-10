package com.greeffer.xcam.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun GlassButton(
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  borderColor: Color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
  fillColor: Color = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.3f),
  pressedFillColor: Color = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.6f),
  glowColor: Color = MaterialTheme.colorScheme.primary,
  cornerRadius: Dp = 8.dp,
  content: @Composable () -> Unit
)
{
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val animatedGlowAlpha by animateFloatAsState(
      targetValue = if (isPressed) 0.7f else 0.3f,
      animationSpec = tween(durationMillis = 120),
      label = "glow_alpha"
    )
    val scale by animateFloatAsState(
      targetValue = if (isPressed) 0.96f else 1f,
      animationSpec = tween(durationMillis = 80),
      label = "scale"
    )

    Box(
      modifier = modifier
        .scale(scale)
        .clip(RoundedCornerShape(cornerRadius))
        .then(
          if (isPressed) Modifier.neonGlow(glowColor, glowRadius = 12.dp, glowAlpha = animatedGlowAlpha)
          else Modifier
        )
        .border(
          width = 1.dp,
          color = borderColor.copy(alpha = if (isPressed) 1f else 0.6f),
          shape = RoundedCornerShape(cornerRadius)
        )
        .clickable(
          interactionSource = interactionSource,
          indication = null,
          enabled = enabled,
          onClick = onClick
        ),
      content = { Box(modifier = Modifier.padding(8.dp)) { content() } }
    )
}


@Preview
@Composable
fun GlassButtonPreview()
{
    MaterialTheme {
        GlassButton(
          onClick = {},
          modifier = Modifier.padding(16.dp)
        ) {
            androidx.compose.material3.Text("Glass Button")
        }
    }
}
