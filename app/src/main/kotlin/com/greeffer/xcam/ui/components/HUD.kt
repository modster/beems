package com.greeffer.xcam.ui.components


import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.greeffer.xcam.ui.theme.Primary

@Composable
fun CrosshairOverlay(
  modifier: Modifier = Modifier,
  color: Color = Primary.copy(alpha = 0.7f),
  strokeWidth: Dp = 1.dp
)
{
    Canvas(modifier = modifier.fillMaxSize()) {
        val centerX = size.width / 2f
        val centerY = size.height / 2f
        val lineLength = 20f
        val gap = 10f

        drawLine(
          color = color,
          start = Offset(centerX - lineLength - gap, centerY),
          end = Offset(centerX - gap, centerY),
          strokeWidth = strokeWidth.toPx(),
          cap = StrokeCap.Round
        )
        drawLine(
          color = color,
          start = Offset(centerX + gap, centerY),
          end = Offset(centerX + lineLength + gap, centerY),
          strokeWidth = strokeWidth.toPx(),
          cap = StrokeCap.Round
        )
        drawLine(
          color = color,
          start = Offset(centerX, centerY - lineLength - gap),
          end = Offset(centerX, centerY - gap),
          strokeWidth = strokeWidth.toPx(),
          cap = StrokeCap.Round
        )
        drawLine(
          color = color,
          start = Offset(centerX, centerY + gap),
          end = Offset(centerX, centerY + lineLength + gap),
          strokeWidth = strokeWidth.toPx(),
          cap = StrokeCap.Round
        )

        drawCircle(
          color = color,
          radius = 3f,
          center = Offset(centerX, centerY),
          style = Stroke(width = strokeWidth.toPx())
        )
    }
}


@Composable
fun LevelIndicator(
  modifier: Modifier = Modifier,
  angle: Float = 0f,
  color: Color = Primary.copy(alpha = 0.6f),
  strokeWidth: Dp = 1.dp
)
{
    Canvas(modifier = modifier.size(60.dp)) {
        val centerX = size.width / 2f
        val centerY = size.height / 2f

        val indicatorLength = size.height / 3f
        val radians = Math.toRadians(angle.toDouble())
        val endX = centerX + (indicatorLength * kotlin.math.sin(radians)).toFloat()
        val endY = centerY - (indicatorLength * kotlin.math.cos(radians)).toFloat()

        drawLine(
          color = color,
          start = Offset(centerX, centerY),
          end = Offset(endX, endY),
          strokeWidth = strokeWidth.toPx(),
          cap = StrokeCap.Round
        )

        drawCircle(
          color = color,
          radius = 4f,
          center = Offset(centerX, centerY)
        )

        val baseLength = size.height / 5f
        drawLine(
          color = color.copy(alpha = 0.3f),
          start = Offset(centerX - baseLength, centerY),
          end = Offset(centerX + baseLength, centerY),
          strokeWidth = strokeWidth.toPx() * 0.5f,
          cap = StrokeCap.Round
        )
    }
}


@Composable
fun HistogramView(
  modifier: Modifier = Modifier,
  data: List<Float> = List(64) { (Math.random() * 0.3f + 0.7f).toFloat() },
  color: Color = Primary.copy(alpha = 0.6f),
  fillColor: Color = Primary.copy(alpha = 0.15f)
)
{
    Canvas(modifier = modifier) {
        val barWidth = size.width / data.size
        val maxHeight = size.height

        drawRect(
          color = fillColor,
          topLeft = Offset.Zero,
          size = androidx.compose.ui.geometry.Size(size.width, size.height)
        )

        data.forEachIndexed { index, value ->
            val barHeight = value * maxHeight
            drawRect(
              color = color,
              topLeft = Offset(index * barWidth, maxHeight - barHeight),
              size = androidx.compose.ui.geometry.Size(barWidth - 1f, barHeight)
            )
        }
    }
}


@Composable
fun GridOverlay(
  modifier: Modifier = Modifier,
  color: Color = Primary.copy(alpha = 0.1f),
  strokeWidth: Dp = 1.dp
)
{
    Canvas(modifier = modifier.fillMaxSize()) {
        val spacing = size.width / 8f

        for (i in 1 .. 7)
        {
            drawLine(
              color = color,
              start = Offset(i * spacing, 0f),
              end = Offset(i * spacing, size.height),
              strokeWidth = strokeWidth.toPx()
            )
        }

        for (i in 1 .. 7)
        {
            drawLine(
              color = color,
              start = Offset(0f, i * spacing),
              end = Offset(size.width, i * spacing),
              strokeWidth = strokeWidth.toPx()
            )
        }
    }
}


@Preview
@Composable
fun CrosshairOverlayPreview()
{
    Box(modifier = Modifier.size(200.dp)) {
        CrosshairOverlay()
    }
}
