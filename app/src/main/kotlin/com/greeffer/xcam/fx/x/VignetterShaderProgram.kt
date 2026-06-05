package com.greeffer.xcam.fx.x

import android.content.Context
import android.opengl.GLES20
import androidx.annotation.OptIn
import androidx.media3.common.VideoFrameProcessingException
import androidx.media3.common.util.GlProgram
import androidx.media3.common.util.Size
import androidx.media3.common.util.UnstableApi
import androidx.media3.effect.BaseGlShaderProgram
import androidx.media3.effect.GlEffect
import androidx.media3.effect.GlShaderProgram

@OptIn(UnstableApi::class)
class VignetterEffect : GlEffect {
    override fun toGlShaderProgram(context: Context, useHdr: Boolean): GlShaderProgram {
        return VignetterShaderProgram(context, useHdr)
    }
}

@OptIn(UnstableApi::class)
class VignetterShaderProgram(
  context: Context,
  useHdr: Boolean
) : BaseGlShaderProgram(useHdr, 1) {
    private val glProgram: GlProgram

    override fun configure(inputWidth: Int, inputHeight: Int): Size {
        // Configure the shader program for the current frame size and update the resolution uniform.
        val p0 = inputWidth.toFloat()
        val p1 = inputHeight.toFloat()
        glProgram.setFloatsUniform("uResolution", floatArrayOf(p0, p1))
        return Size(inputWidth, inputHeight)
    }

    init {
        // Use standard copy vertex shader paired with your new vignette shader
        val vertexShaderPath = "shaders/vertex_shader_copy.glsl"
        val fragmentShaderPath = "shaders/vignette_fragment.glsl"

        try {
            glProgram = GlProgram(context, vertexShaderPath, fragmentShaderPath)
        } catch (e: Exception) {
            throw VideoFrameProcessingException("Shader compilation failed for Vignette", e)
        }
    }

    override fun drawFrame(inputTexId: Int, presentationTimeUs: Long) {
        glProgram.use()
        glProgram.setSamplerTexIdUniform("uTexSampler", inputTexId, 0)
        glProgram.bindAttributesAndUniforms()
        // Draw a full-screen quad directly with GLES20
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)
    }

    override fun release() {
        super.release()
        glProgram.delete()
    }
}
