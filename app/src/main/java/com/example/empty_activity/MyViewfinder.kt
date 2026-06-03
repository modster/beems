package com.example.empty_activity

import androidx.camera.compose.CameraXViewfinder
import androidx.camera.core.Preview
import androidx.camera.core.SurfaceRequest
import androidx.camera.viewfinder.compose.MutableCoordinateTransformer
import androidx.camera.viewfinder.core.ImplementationMode
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.lifecycle.ViewModel

class PreviewViewModel : ViewModel() {
    private val _surfaceRequests = MutableStateFlow<SurfaceRequest?>(null)

    val surfaceRequests: StateFlow<SurfaceRequest?>
        get() = _surfaceRequests.asStateFlow()

    private fun produceSurfaceRequests(previewUseCase: Preview) {
        // Always publish new SurfaceRequests from Preview
        previewUseCase.setSurfaceProvider { newSurfaceRequest ->
            _surfaceRequests.value = newSurfaceRequest
        }
    }

    fun focusOnPoint(
        surfaceBounds: Size,
        x: Float,
        y: Float,
    ) {
        // Create point for CameraX's CameraControl.startFocusAndMetering() and submit...
    }

    // ...
}

@Composable
fun MyCameraViewfinder(
    viewModel: PreviewViewModel,
    modifier: Modifier = Modifier,
) {
    val currentSurfaceRequest: SurfaceRequest? by viewModel.surfaceRequests.collectAsState()

    currentSurfaceRequest?.let { surfaceRequest ->

        // CoordinateTransformer for transforming from Offsets to Surface coordinates
        val coordinateTransformer = remember { MutableCoordinateTransformer() }

        CameraXViewfinder(
            surfaceRequest = surfaceRequest,
            implementationMode = ImplementationMode.EXTERNAL, // Can also use EMBEDDED
            modifier =
                modifier.pointerInput(Unit) {
                    detectTapGestures {
                        with(coordinateTransformer) {
                            val surfaceCoords = it.transform()
                            viewModel.focusOnPoint(
                                surfaceRequest.resolution,
                                surfaceCoords.x,
                                surfaceCoords.y,
                            )
                        }
                    }
                },
            coordinateTransformer = coordinateTransformer,
        )
    }
}
