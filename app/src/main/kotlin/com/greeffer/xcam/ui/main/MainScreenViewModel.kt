package com.greeffer.xcam.ui.main

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.core.SurfaceRequest
import androidx.compose.ui.geometry.Offset
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.greeffer.xcam.data.DataRepository
import com.greeffer.xcam.ui.common.ResourceUiState
import com.greeffer.xcam.ui.common.asResourceState
import java.io.File
import java.util.concurrent.Executor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

typealias MainScreenUiState = ResourceUiState<List<String>>

@RequiresApi(Build.VERSION_CODES.P)
class MainScreenViewModel(
  dataRepository: DataRepository,
): ViewModel()
{


    val uiState: StateFlow<MainScreenUiState> = dataRepository.data.asResourceState(viewModelScope)


    // PERFORMANCE OPTIMIZATION: Use lazy initialization to defer CameraX Preview creation.
    // Creating CameraX Use Cases eagerly in the constructor blocks the Main Thread and causes UI jank
    // during view model instantiation. This also prevents unnecessary initialization and potential
    // crashes in environments where CameraX is not supported (such as CI/JVM Unit Tests).
    private val xPreviewUseCase: Preview? by lazy {
        try
        {
            Preview.Builder().build().also { preview ->
                // Publish new SurfaceRequests from Preview once the use case is lazily created.
                preview.setSurfaceProvider { newSurfaceRequest ->
                    _surfaceRequests.value = newSurfaceRequest
                }
            }
        }
        catch (t: Throwable)
        {
            Log.e("MainScreenViewModel", "Failed to build Preview use case", t)
            null
        }
    }

    private val _surfaceRequests = MutableStateFlow<SurfaceRequest?>(null)


    // PERFORMANCE OPTIMIZATION: Accessing the surfaceRequests lazy property will trigger
    // the lazy initialization of `xPreviewUseCase` only when actually observed by the UI.
    val surfaceRequests: StateFlow<SurfaceRequest?> by lazy {
        // Force evaluation of xPreviewUseCase lazy property to establish the surface provider connection.
        xPreviewUseCase
        _surfaceRequests.asStateFlow()
    }


    // PERFORMANCE OPTIMIZATION: Start zoom at 1.0f (representing 1x default scale) instead of 0f.
    // This allows proper multiplicative scaling matching physical zoom lens logic.
    private val _zoom = MutableStateFlow(1f)

    val zoom: StateFlow<Float>
        get() = _zoom.asStateFlow()

    private val _pan = MutableStateFlow(Offset.Zero)

    val pan: StateFlow<Offset>
        get() = _pan.asStateFlow()

    private val _focusPoint = MutableStateFlow<Offset?>(null)

    val focusPoint: StateFlow<Offset?>
        get() = _focusPoint.asStateFlow()

    fun onTap(offsetToSurface: Offset)
    {
        // PERFORMANCE METRIC: Track the processing time of the coordinate mapping/focus action.
        val startTime = System.nanoTime()
        _focusPoint.value = offsetToSurface
        val durationUs = (System.nanoTime() - startTime) / 1000
        Log.d(
          "MainScreenViewModel",
          "onTap processed in $durationUs us. Surface-transformed focus point set: $offsetToSurface"
        )
    }


    fun capturePhoto(
      context: Context,
      xImageCapture: ImageCapture,
    )
    {
        val xMainExecutor: Executor = ContextCompat.getMainExecutor(context)
        val outputFile = File(
          context.cacheDir, "media3_effect_${System.currentTimeMillis()}.jpg"
        )
        val outputOptions = ImageCapture.OutputFileOptions.Builder(outputFile).build()

        xImageCapture.takePicture(
          outputOptions, xMainExecutor, object: ImageCapture.OnImageSavedCallback
        {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults)
            {
                Log.d("CameraMedia3", "Filtered photo saved successfully: ${outputFile.absolutePath}")
            }

            override fun onError(exception: ImageCaptureException)
            {
                Log.e("CameraMedia3", "Photo capture failed: ${exception.message}", exception)
            }
        })
    }

    fun onTapToFocus(offset: Offset)
    {
        _focusPoint.value = offset
    }


    // PERFORMANCE OPTIMIZATION: Handle high-frequency touch gesture scaling smoothly.
    // 1. Pinch-to-zoom gesture callbacks emit relative zoom multipliers (e.g. 1.02f / 0.98f) on every frame.
    //    We accumulate this multiplicatively and coerce within reasonable physical limits (1x to 10x).
    // 2. Perform early equality checks to prevent redundant downstream StateFlow emissions.
    fun onZoom(zoomFactor: Float)
    {
        if (zoomFactor == 1f) return
        val current = _zoom.value
        val target = (current * zoomFactor).coerceIn(1.0f, 10.0f)
        if (target != current)
        {
            _zoom.value = target
        }
    }


    // PERFORMANCE OPTIMIZATION: Accumulate pan gesture offset deltas continuously.
    // 1. Gesture detector emits relative deltas. We add them to preserve pan position across frames.
    // 2. Avoid redundant emissions for zero deltas.
    fun onPan(panDelta: Offset)
    {
        if (panDelta == Offset.Zero) return
        _pan.value += panDelta
    }


    // PERFORMANCE/MEMORY OPTIMIZATION: Override onCleared to unbind/null out the SurfaceProvider.
    // Setting setSurfaceProvider to null is critical in CameraX to release active SurfaceRequests and
    // native gralloc/graphic buffers, which otherwise causes heavy native memory leaks.
    override fun onCleared()
    {
        super.onCleared()
        xPreviewUseCase?.setSurfaceProvider(null)
        _surfaceRequests.value = null
        Log.d(
          "MainScreenViewModel",
          "MainScreenViewModel onCleared: Released active CameraX surface provider to prevent memory leaks."
        )
    }

}
