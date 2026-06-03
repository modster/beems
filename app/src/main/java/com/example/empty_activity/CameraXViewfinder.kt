package com.example.empty_activity

import androidx.camera.compose.*
import androidx.camera.core.*
import androidx.camera.viewfinder.compose.*
import androidx.camera.viewfinder.core.*
import androidx.compose.foundation.gestures.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.input.pointer.*
import androidx.lifecycle.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.MutableStateFlow 
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectAsState
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview


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
                            viewModel. (
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
