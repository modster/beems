package com.greeffer.xcam.ui.main

import androidx.camera.compose.CameraXViewfinder
import androidx.camera.core.SurfaceRequest
import androidx.camera.viewfinder.compose.MutableCoordinateTransformer
import androidx.camera.viewfinder.core.ImplementationMode
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun XViewfinder(vm: MainScreenViewModel)
{
    // PERFORMANCE OPTIMIZATION: Use collectAsStateWithLifecycle() instead of collectAsState()
    // to stop flow collection when the screen goes into the background. This saves system resources,
    // prevents battery drain, and stops unnecessary background processing of CameraX events.
    val currentSurfaceRequest: SurfaceRequest? by vm.surfaceRequests.collectAsStateWithLifecycle()

    currentSurfaceRequest?.let { surfaceRequest ->
        // CoordinateTransformer for transforming from Offsets to Surface coordinates
        val coordinateTransformer = remember { MutableCoordinateTransformer() }

        CameraXViewfinder(
          surfaceRequest = surfaceRequest,
          implementationMode = ImplementationMode.EXTERNAL,
          modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    // CORRECTNESS & PERFORMANCE OPTIMIZATION: Transform screen tap coordinates to the Surface
                    // coordinates using the coordinateTransformer. This maps the local Composable's coordinate
                    // space to the actual camera/surface dimensions (accounting for letterboxing/scaling/rotation),
                    // enabling accurate Tap-To-Focus.
                    val surfaceCoords = with(coordinateTransformer) { offset.transform() }
                    vm.onTap(
                      offsetToSurface = surfaceCoords,
                    )
                }
            },
          coordinateTransformer = coordinateTransformer,
        )
    }
}
