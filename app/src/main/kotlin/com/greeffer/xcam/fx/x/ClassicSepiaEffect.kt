package com.greeffer.xcam.fx.x

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.VideoFrameProcessingException
import androidx.media3.common.util.GlProgram
import androidx.media3.common.util.Size
import androidx.media3.common.util.UnstableApi
import androidx.media3.effect.BaseGlShaderProgram
import androidx.media3.effect.GlEffect
import androidx.media3.effect.GlShaderProgram

@OptIn(UnstableApi::class) class ClassicSepiaEffect : GlEffect
{

    override fun toGlShaderProgram(context: Context, useHdr: Boolean): GlShaderProgram
    {
        return ClassicSepiaShaderProgram(context, useHdr)
    }
}

@OptIn(UnstableApi::class) class ClassicSepiaShaderProgram(
    context: Context, useHdr: Boolean
) : BaseGlShaderProgram(useHdr, 1)
{

    private val glProgram: GlProgram

    // Classic sepia color matrix (RGBA)
    private val sepiaMatrix = floatArrayOf(
        0.393f, 0.349f, 0.272f, 0f, 0.769f, 0.686f, 0.534f, 0f, 0.189f, 0.168f, 0.131f, 0f, 0f, 0f, 0f, 1f
    )

    override fun configure(inputWidth: Int, inputHeight: Int): Size
    {
        return Size(inputWidth, inputHeight)
    }

    init
    { // Use standard copy vertex shader paired with your new sepia shader
        val vertexShaderPath = "shaders/vertex_shader_copy.glsl"
        val fragmentShaderPath = "shaders/sepia_fragment.glsl"

        try
        {
            glProgram = GlProgram(context, vertexShaderPath, fragmentShaderPath)
        }
        catch (e: Exception)
        {
            throw VideoFrameProcessingException("Shader compilation failed for sepia", e)
        }
    }

    override fun drawFrame(inputTexId: Int, presentationTimeUs: Long)
    {
        glProgram.use()
        glProgram.setSamplerTexIdUniform("uTexSampler", inputTexId, 0)
        glProgram.setFloatsUniform("uColorMatrix", sepiaMatrix)
        glProgram.bindAttributesAndUniforms()
    }

    override fun release()
    {
        super.release()
        glProgram.delete()
    }
}
