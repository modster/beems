package com.greeffer.xcam.fx.x

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.graphics.RuntimeColorFilter
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.graphics.createBitmap

/**
 * To apply a sepia filter in your Android app, you can use the ColorMatrixColorFilter to adjust the RGB values of your
 * images or bitmaps, or implement the RuntimeColorFilter for custom effects. This process provides a warm, vintage tone
 * to your media.
 *
 * Method 1: Using ColorMatrix (Best for Bitmaps)You can alter the hue and saturation of a Bitmap by
 * applying a 5x5 matrix using a Paint object.Kotlin Example:
 */
fun applySepiaFilter(sourceBitmap: Bitmap): Bitmap
{
    // Create a mutable bitmap for the result
    val resultBitmap = sourceBitmap.config?.let {
        createBitmap(sourceBitmap.width, sourceBitmap.height, it)
    }
                       ?: return sourceBitmap

    // Define the Sepia Color Matrix
    val sepiaMatrix = ColorMatrix().apply {
        setSaturation(0f) // Convert to grayscale first
        val scaleMatrix = ColorMatrix(
          floatArrayOf(
            1.2f, 0.5f, 0.1f, 0f, 0f,
            0.1f, 1.2f, 0.5f, 0f, 0f,
            0.1f, 0.5f, 1.2f, 0f, 0f,
            0f, 0f, 0f, 1f, 0f
          )
        )
        postConcat(scaleMatrix)
    }

    val paint = Paint().apply {
        colorFilter = ColorMatrixColorFilter(sepiaMatrix)
    }

    // Draw the image with the filter
    val canvas = Canvas(resultBitmap)
    canvas.drawBitmap(sourceBitmap, 0f, 0f, paint)

    return resultBitmap
}


/**
 * Method 2: Using RuntimeColorFilter (Android 16+)If your app's minimum SDK targets the latest API (API level 16+),
 * you can utilize the RuntimeColorFilter to apply math-based visual effects directly into the Paint.Kotlin
 *
 * Example:
 */
const val sepiaEffectString = """
    uniform half4 main(half4 c) {
        half luminosity = dot(c.rgb, half3(0.299, 0.587, 0.114));
        half3 sepiaColor = luminosity * half3(1.2, 0.5, 0.1);
        return half4(min(sepiaColor, 1.0), c.a);
    }
"""


@RequiresApi(Build.VERSION_CODES.BAKLAVA)
fun setCustomColorFilter(paint: Paint)
{
    val filter = RuntimeColorFilter(sepiaEffectString)
    paint.colorFilter = filter
}


/**
 * Applies sepia filter using RuntimeColorFilter (Android 16+).
 */
@RequiresApi(Build.VERSION_CODES.BAKLAVA)
fun applySepiaFilterWithRuntimeColorFilter(sourceBitmap: Bitmap): Bitmap
{
    val resultBitmap = sourceBitmap.config?.let {
        createBitmap(sourceBitmap.width, sourceBitmap.height, it)
    }
                       ?: return sourceBitmap

    val paint = Paint()
    setCustomColorFilter(paint)

    val canvas = Canvas(resultBitmap)
    canvas.drawBitmap(sourceBitmap, 0f, 0f, paint)

    return resultBitmap
}
