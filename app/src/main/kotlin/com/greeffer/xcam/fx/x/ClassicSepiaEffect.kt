package com.greeffer.xcam.fx.x

import androidx.annotation.OptIn
import androidx.media3.common.util.GlProgram
import androidx.media3.common.util.UnstableApi

@OptIn(UnstableApi::class)
class ClassicSepiaEffect: CopyShaderEffect(FRAGMENT_SHADER_ASSET_PATH)
{
    
    override fun configureExtraUniforms(
        glProgram: GlProgram,
        inputWidth: Int,
        inputHeight: Int,
    )
    {
        glProgram.setFloatsUniform("uColorMatrix", SEPIA_MATRIX)
    }
    
    private companion object
    {
        
        const val FRAGMENT_SHADER_ASSET_PATH = "shaders/sepia_fragment.glsl"
        val SEPIA_MATRIX = floatArrayOf(
            0.393f, 0.349f, 0.272f, 0f,
            0.769f, 0.686f, 0.534f, 0f,
            0.189f, 0.168f, 0.131f, 0f,
            0f, 0f, 0f, 1f,
        )
    }
}
