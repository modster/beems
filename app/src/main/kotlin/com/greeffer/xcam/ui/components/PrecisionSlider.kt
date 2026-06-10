package com.greeffer.xcam.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.greeffer.xcam.ui.theme.Primary
import com.greeffer.xcam.ui.theme.Secondary
import com.greeffer.xcam.ui.theme.SurfaceContainerHigh

@Composable
fun PrecisionSlider(
  value: Float,
  onValueChange: (Float) -> Unit,
  modifier: Modifier = Modifier,
  trackHeight: Dp = 2.dp,
  thumbRadius: Dp = 10.dp,
  trackColor: Color = SurfaceContainerHigh.copy(alpha = 0.5f),
  gradientStart: Color = Primary,
  gradientEnd: Color = Secondary
)
{
    var sliderPosition by remember(value) { mutableFloatStateOf(value) }

    val animatedPosition by animateFloatAsState(
      targetValue = sliderPosition,
      animationSpec = tween(durationMillis = 80),
      label = "slider_position"
    )

    Box(
      modifier = modifier
        .fillMaxWidth()
        .height(40.dp)
        .padding(horizontal = thumbRadius)
    ) {
        Canvas(
          modifier = Modifier
            .align(Alignment.CenterStart)
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectHorizontalDragGestures { change, _ ->
                    val newPosition = (change.position.x / size.width).coerceIn(0f, 1f)
                    sliderPosition = newPosition
                    onValueChange(newPosition)
                }
            }
        ) {
            val trackY = size.height / 2f
            val thumbX = animatedPosition * size.width

            drawLine(
              color = trackColor,
              start = Offset(0f, trackY),
              end = Offset(size.width, trackY),
              strokeWidth = trackHeight.toPx(),
              cap = StrokeCap.Round
            )

            if (animatedPosition > 0f)
            {
                drawLine(
                  brush = Brush.horizontalGradient(
                    colors = listOf(gradientStart, gradientEnd),
                    startX = 0f,
                    endX = thumbX
                  ),
                  start = Offset(0f, trackY),
                  end = Offset(thumbX, trackY),
                  strokeWidth = trackHeight.toPx(),
                  cap = StrokeCap.Round
                )
            }

            drawCircle(
              color = gradientStart,
              radius = thumbRadius.toPx(),
              center = Offset(thumbX, trackY)
            )

            drawCircle(
              color = Color.White.copy(alpha = 0.3f),
              radius = thumbRadius.toPx() * 0.4f,
              center = Offset(thumbX, trackY)
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PrecisionSliderPreview()
{
    PrecisionSlider(
      value = 0.5f,
      onValueChange = {}
    )
}

