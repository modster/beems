package com.greeffer.xcam.fx.x

import androidx.annotation.OptIn
import androidx.media3.common.util.GlProgram
import androidx.media3.common.util.UnstableApi

@OptIn(UnstableApi::class)
class VignetterEffect : CopyShaderEffect(FRAGMENT_SHADER_ASSET_PATH)
{

    override fun configureExtraUniforms(
        glProgram : GlProgram ,
        inputWidth : Int ,
        inputHeight : Int ,
    )
    {
        // No-op: vignette_fragment.glsl operates on normalized coordinates and doesn't require uResolution.
    }

    private companion object
    {

        const val FRAGMENT_SHADER_ASSET_PATH = "shaders/vignette_fragment.glsl"
    }
}
