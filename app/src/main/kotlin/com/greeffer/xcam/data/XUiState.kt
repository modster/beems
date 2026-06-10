package com.greeffer.xcam.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


data class DefaultXUiState(
  val shutter: Float,
  val iso: Int,
  val evBias: Float,
  val whiteBalance: Int,
  val blendMode: String,
  val blendStrength: Float,
  val zoomRatio: Float,
  val luminosity: Float? = null,
): DataRepository
{

    override val data: Flow<List<String>> = flow {
        emit(Xcam.entries.map { it.displayName })
    }
}

enum class Xcam
{

    SUCCESS,
    ERROR,
    FRONT,
    RECORD,
    RECORDING,
    RECORDED,
    CAPTURE,
    CAPTURING,
    CAPTURED;

    val displayName: String
        get() = name.lowercase().replaceFirstChar { it.uppercase() }
}

