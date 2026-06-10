package com.greeffer.xcam.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


@Composable
fun GlassSurface(
  modifier: Modifier = Modifier,
  blurRadius: Dp = 30.dp,
  fillColor: Color = Color.White.copy(alpha = 0.08f),
  strokeColor: Color = Color.White.copy(alpha = 0.15f),
  cornerRadius: Dp = 8.dp,
  content: @Composable BoxScope.() -> Unit
)
{
    Box(
      modifier = modifier
        .clip(RoundedCornerShape(cornerRadius))
        .then(
          Modifier.drawBehind {
              drawRect(
                brush = Brush.verticalGradient(
                  colors = listOf(
                    strokeColor,
                    Color.Transparent
                  ),
                  startY = 0f,
                  endY = size.height * 0.3f
                )
              )
          }
        )
        .background(
          brush = Brush.verticalGradient(
            colors = listOf(fillColor, fillColor.copy(alpha = 0.04f))
          )
        )
        .border(
          width = 1.dp,
          color = strokeColor,
          shape = RoundedCornerShape(cornerRadius)
        ),
      content = content
    )
}


@Composable
fun AmbientGlow(
  modifier: Modifier = Modifier,
  glowColor: Color = Color(0xFF00F2FF).copy(alpha = 0.05f),
  glowRadius: Dp = 40.dp,
  content: @Composable BoxScope.() -> Unit
)
{
    Box(
      modifier = modifier.drawBehind {
          drawCircle(
            color = glowColor,
            radius = glowRadius.toPx(),
            center = Offset(size.width / 2f, size.height / 2f),
            alpha = 0.4f
          )
      },
      content = content
    )
}

fun Modifier.neonGlow(
  color: Color = Color(0xFF00F2FF),
  glowRadius: Dp = 10.dp,
  glowAlpha: Float = 0.6f
): Modifier =
  this.drawBehind {
      drawCircle(
        color = color.copy(alpha = glowAlpha),
        radius = glowRadius.toPx(),
        center = Offset(size.width / 2f, size.height / 2f)
      )
  }


@Preview
@Composable
fun GlassSurfacePreview()
{
    GlassSurface(
      modifier = Modifier.padding(16.dp)
    ) {
        androidx.compose.material3.Text("Glass Surface")
    }
}


@Preview
@Composable
fun AmbientGlowPreview()
{
    AmbientGlow(
      modifier = Modifier.padding(16.dp)
    ) {
        androidx.compose.material3.Text("Ambient Glow")
    }
}


@Preview
@Composable
fun NeonGlowPreview()
{
    Box(
      modifier = Modifier
        .padding(32.dp)
        .neonGlow()
        .padding(16.dp)
    ) {
        androidx.compose.material3.Text("Neon Glow")
    }
}
