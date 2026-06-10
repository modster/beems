package com.greeffer.xcam.fx.x

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RadialGradientShader
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.tooling.preview.Preview

val largeRadialGradient = object: ShaderBrush()
{
    override fun createShader(size: Size): Shader
    {
        val biggerDimension = maxOf(size.height, size.width)
        return RadialGradientShader(
          colors = listOf(Color(0xFF2be4dc), Color(0xFF243484)),
          center = size.center,
          radius = biggerDimension / 2f,
          colorStops = listOf(0f, 0.95f)
        )
    }
}


@Preview(showBackground = true)
@Composable
fun ShaderBrushPreview()
{
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(largeRadialGradient)
    )
}
