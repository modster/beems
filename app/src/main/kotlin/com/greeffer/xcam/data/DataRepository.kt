package com.greeffer.xcam.data

import androidx.annotation.OptIn
import androidx.media3.common.Effect
import androidx.media3.common.util.UnstableApi
import androidx.media3.effect.RgbFilter
import com.greeffer.xcam.fx.x.ClassicSepiaEffect
import com.greeffer.xcam.fx.x.VignetterEffect
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
interface DataRepository {
    val data: Flow<List<String>>
}
@OptIn(UnstableApi::class)
enum class XCameraFilter(val displayName: String) {
    NONE("Normal"),
    GRAYSCALE("Grayscale"),
    INVERT("Invert"),
    SEPIA("Sepia"),
    VIGNETTE("Vignette");

    // Map each enum option to its respective Media3 Effect object
    fun getMedia3Effects(): List<Effect> {
        return when (this) {
            NONE -> emptyList()
            GRAYSCALE -> listOf(RgbFilter.createGrayscaleFilter())
            INVERT -> listOf(RgbFilter.createInvertedFilter())
            SEPIA -> listOf(ClassicSepiaEffect())
            VIGNETTE -> listOf(VignetterEffect())
        }
    }
}

class DefaultDataRepository : DataRepository {
    override val data: Flow<List<String>> = flow {
        emit(listOf(
            XCameraFilter.NONE.displayName ,
            XCameraFilter.GRAYSCALE.displayName ,
            XCameraFilter.INVERT.displayName ,
            XCameraFilter.SEPIA.displayName ,
            XCameraFilter.VIGNETTE.displayName
          )) }
}
