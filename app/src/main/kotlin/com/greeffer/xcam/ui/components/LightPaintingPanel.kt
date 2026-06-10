package com.greeffer.xcam.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LightPaintingPanel(
  modifier: Modifier = Modifier
)
{
    val activeColor = Color(0xFF3B95FF)
    val panelBackground = Color(0xFF1E1E1E).copy(alpha = 0.9f)
    val dividerColor = Color.White.copy(alpha = 0.2f)

    Box(modifier = modifier.padding(16.dp)) {
        // Corner markers
        Canvas(modifier = Modifier.matchParentSize()) {
            val length = 12.dp.toPx()
            val stroke = 2.dp.toPx()
            val color = Color.White

            // Top Left
            drawLine(color, Offset(0f, 0f), Offset(length, 0f), stroke)
            drawLine(color, Offset(0f, 0f), Offset(0f, length), stroke)

            // Top Right
            drawLine(color, Offset(size.width, 0f), Offset(size.width - length, 0f), stroke)
            drawLine(color, Offset(size.width, 0f), Offset(size.width, length), stroke)

            // Bottom Left
            drawLine(color, Offset(0f, size.height), Offset(length, size.height), stroke)
            drawLine(color, Offset(0f, size.height), Offset(0f, size.height - length), stroke)

            // Bottom Right
            drawLine(color, Offset(size.width, size.height), Offset(size.width - length, size.height), stroke)
            drawLine(color, Offset(size.width, size.height), Offset(size.width, size.height - length), stroke)
        }

        Column(
          modifier = Modifier
            .padding(8.dp)
            .background(panelBackground)
            .border(1.dp, dividerColor)
            .padding(bottom = 12.dp)
        ) {
            // Title
            Text(
              text = "Light Painting",
              color = Color.White,
              fontSize = 20.sp,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(16.dp)
            )

            // Tab Indicator Line
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .background(dividerColor)
            ) {
                Box(
                  modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .height(2.dp)
                    .background(activeColor)
                    .align(
                      Alignment.Center
                    ) // Wait, in the image, it's slightly to the right? Let's keep it centered for now, or just fill a certain fraction.
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Mode Row
            var selectedMode by remember { mutableStateOf("LIGHT") }
            SettingsRowDotted(label = "Mode") {
                val modes: List<Triple<String, String, Boolean>> = listOf(
                  Triple("LIGHT", "L", true),
                  Triple("WATER", "W", true),
                  Triple("STARS", "S", false),
                  Triple("BULB", "B", true)
                )
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    modes.forEach { (title, iconLabel, _) ->
                        val isSelected = selectedMode == title
                        Box(
                          modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (isSelected) activeColor else Color.Transparent)
                            .border(1.dp, if (isSelected) activeColor else dividerColor, RoundedCornerShape(4.dp))
                            .clickable { selectedMode = title },
                          contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                  text = iconLabel,
                                  color = if (isSelected) Color.Black else Color.White,
                                  fontSize = 14.sp,
                                  fontWeight = FontWeight.ExtraBold
                                )
                                Text(
                                  text = title,
                                  color = if (isSelected) Color.Black else Color.White,
                                  fontSize = 8.sp,
                                  fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Quality Row
            var selectedQuality by remember { mutableStateOf("MAX") }
            SettingsRowDotted(label = "Quality") {
                val qualities = listOf("L", "HQ", "MAX")
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    qualities.forEach { q ->
                        val isSelected = selectedQuality == q
                        Box(
                          modifier = Modifier
                            .height(32.dp)
                            .widthIn(min = 36.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (isSelected) activeColor else Color.Transparent)
                            .border(1.dp, if (isSelected) activeColor else dividerColor, RoundedCornerShape(4.dp))
                            .clickable { selectedQuality = q }
                            .padding(horizontal = 8.dp),
                          contentAlignment = Alignment.Center
                        ) {
                            Text(
                              text = q,
                              color = if (isSelected) Color.Black else Color.White,
                              fontSize = 12.sp,
                              fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Length Row
            var lengthValue by remember { mutableFloatStateOf(0.1f) }
            SettingsRowBasic(label = "Length") {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Slider(
                      value = lengthValue,
                      onValueChange = { lengthValue = it },
                      modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp),
                      colors = SliderDefaults.colors(
                        thumbColor = activeColor,
                        activeTrackColor = activeColor,
                        inactiveTrackColor = dividerColor
                      )
                    )
                    Box(
                      modifier = Modifier
                        .height(32.dp)
                        .width(48.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .border(1.dp, dividerColor, RoundedCornerShape(4.dp)),
                      contentAlignment = Alignment.Center
                    ) {
                        Text(
                          text = "1 S",
                          color = Color.White,
                          fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Timer Row
            var timerValue by remember { mutableFloatStateOf(0f) }
            SettingsRowDotted(label = "Timer") {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val isOff = timerValue == 0f
                    Box(
                      modifier = Modifier
                        .height(32.dp)
                        .width(44.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (isOff) activeColor else Color.Transparent)
                        .border(1.dp, if (isOff) activeColor else dividerColor, RoundedCornerShape(4.dp))
                        .clickable { timerValue = 0f },
                      contentAlignment = Alignment.Center
                    ) {
                        Text(
                          text = "OFF",
                          color = if (isOff) Color.Black else Color.White,
                          fontSize = 12.sp,
                          fontWeight = FontWeight.Bold
                        )
                    }

                    Slider(
                      value = timerValue,
                      onValueChange = { timerValue = it },
                      modifier = Modifier.width(60.dp),
                      colors = SliderDefaults.colors(
                        thumbColor = dividerColor,
                        activeTrackColor = dividerColor,
                        inactiveTrackColor = dividerColor
                      )
                    )

                    val isOn = timerValue > 0f
                    Box(
                      modifier = Modifier
                        .height(32.dp)
                        .width(44.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (isOn) activeColor else Color.Transparent)
                        .border(1.dp, if (isOn) activeColor else dividerColor, RoundedCornerShape(4.dp))
                        .clickable { timerValue = 1f },
                      contentAlignment = Alignment.Center
                    ) {
                        Text(
                          text = "ON",
                          color = if (isOn) Color.Black else Color.White,
                          fontSize = 12.sp,
                          fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun SettingsRowDotted(
  label: String,
  content: @Composable () -> Unit
)
{
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
          text = label,
          color = Color.White,
          fontSize = 14.sp
        )
        Canvas(
          modifier = Modifier
            .weight(1f)
            .padding(horizontal = 8.dp)
            .height(1.dp)
        ) {
            drawLine(
              color = Color.White.copy(alpha = 0.3f),
              start = Offset(0f, 0f),
              end = Offset(size.width, 0f),
              strokeWidth = 2f,
              pathEffect = PathEffect.dashPathEffect(floatArrayOf(5f, 10f), 0f)
            )
        }
        content()
    }
}


@Composable
fun SettingsRowBasic(
  label: String,
  content: @Composable () -> Unit
)
{
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
          text = label,
          color = Color.White,
          fontSize = 14.sp
        )
        Spacer(modifier = Modifier.width(8.dp))
        Box(modifier = Modifier.weight(1f)) {
            content()
        }
    }
}


@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
fun LightPaintingPanelPreview()
{
    MaterialTheme {
        LightPaintingPanel()
    }
}
