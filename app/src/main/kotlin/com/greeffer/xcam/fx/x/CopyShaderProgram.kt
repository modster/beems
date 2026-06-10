package com.greeffer.xcam.fx.x

import android.content.Context
import android.opengl.GLES20
import androidx.annotation.OptIn
import androidx.media3.common.VideoFrameProcessingException
import androidx.media3.common.util.GlProgram
import androidx.media3.common.util.GlUtil
import androidx.media3.common.util.Size
import androidx.media3.common.util.UnstableApi
import androidx.media3.effect.BaseGlShaderProgram
import androidx.media3.effect.GlEffect
import androidx.media3.effect.GlShaderProgram

/**
 * Base for shader-based [GlEffect]s that pair the standard copy vertex shader with a
 * caller-supplied fragment shader and configure per-frame uniforms via a callback.
 */
@OptIn(UnstableApi::class)
abstract class CopyShaderEffect(
  private val fragmentShaderAssetPath: String,
): GlEffect
{

    protected abstract fun configureExtraUniforms(
      glProgram: GlProgram,
      inputWidth: Int,
      inputHeight: Int
    )

    override fun toGlShaderProgram(
      context: Context,
      useHdr: Boolean
    ): GlShaderProgram =
      CopyShaderProgram(context, fragmentShaderAssetPath, useHdr, ::configureExtraUniforms)
}


@OptIn(UnstableApi::class)
open class CopyShaderProgram(
  context: Context,
  fragmentShaderAssetPath: String,
  useHdr: Boolean,
  private val configureExtraUniforms: (GlProgram, Int, Int) -> Unit,
): BaseGlShaderProgram(useHdr, 1)
{

    private val glProgram: GlProgram

    init
    {
        try
        {
            glProgram = GlProgram(
              context,
              VERTEX_SHADER_ASSET_PATH,
              fragmentShaderAssetPath,
            )
            // Draw the frame on the entire normalized device coordinate space, from -1 to 1, for x and y.
            glProgram.setBufferAttribute(
              "aFramePosition",
              GlUtil.getNormalizedCoordinateBounds(),
              GlUtil.HOMOGENEOUS_COORDINATE_VECTOR_SIZE,
            )
            // Provide coordinates for texture sampling mapping.
            glProgram.setBufferAttribute(
              "aTexSamplingCoord",
              GlUtil.getTextureCoordinateBounds(),
              GlUtil.HOMOGENEOUS_COORDINATE_VECTOR_SIZE,
            )
        }
        catch (e: Exception)
        {
            throw VideoFrameProcessingException(
              "Shader compilation failed for $fragmentShaderAssetPath",
              e,
            )
        }
    }


    /** Optional hook for subclasses that need to issue raw GL calls (e.g. glDrawArrays). */
    protected open fun drawQuad()
    {
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)
    }

    override fun configure(
      inputWidth: Int,
      inputHeight: Int
    ): Size
    {
        configureExtraUniforms(glProgram, inputWidth, inputHeight)
        return Size(inputWidth, inputHeight)
    }

    override fun drawFrame(
      inputTexId: Int,
      presentationTimeUs: Long
    )
    {
        glProgram.use()
        glProgram.setSamplerTexIdUniform("uTexSampler", inputTexId, 0)
        glProgram.bindAttributesAndUniforms()
        drawQuad()
    }

    override fun release()
    {
        super.release()
        glProgram.delete()
    }

    private companion object
    {

        const val VERTEX_SHADER_ASSET_PATH = "shaders/vertex_shader_copy.glsl"
    }
}
