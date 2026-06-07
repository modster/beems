package com.greeffer.xcam.data

import androidx.annotation.OptIn
import androidx.media3.common.Effect
import androidx.media3.common.util.UnstableApi
import androidx.media3.effect.RgbFilter
import com.greeffer.xcam.fx.x.ClassicSepiaEffect
import com.greeffer.xcam.fx.x.VignetterEffect
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

@OptIn(UnstableApi::class)
enum class XCameraFilter(val displayName: String)
{
    NONE("None"),
    GRAYSCALE("Grayscale"),
    INVERT("Invert"),
    SEPIA("Sepia"),
    VIGNETTE("Vignette");

    fun getMedia3Effects(): List<Effect>
    {
        return when (this)
        {
            NONE      -> emptyList()
            GRAYSCALE -> listOf(RgbFilter.createGrayscaleFilter())
            INVERT    -> listOf(RgbFilter.createInvertedFilter())
            SEPIA     -> listOf(ClassicSepiaEffect())
            VIGNETTE  -> listOf(VignetterEffect())
        }
    }
}

class DefaultDataRepository: DataRepository
{
    override val data: Flow<List<String>> = flowOf(
        XCameraFilter.entries.map(XCameraFilter::displayName),
    )
}

