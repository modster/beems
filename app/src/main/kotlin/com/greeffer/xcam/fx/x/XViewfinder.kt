package com.greeffer.xcam.fx.x

import androidx.camera.compose.CameraXViewfinder
import androidx.camera.core.SurfaceRequest
import androidx.camera.viewfinder.compose.MutableCoordinateTransformer
import androidx.camera.viewfinder.core.ImplementationMode
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput

@Composable
fun XViewfinder(
  vm: XCamViewModel,
  modifier: Modifier = Modifier,
) {
    val currentSurfaceRequest: SurfaceRequest? by vm.surfaceRequests.collectAsState()

    currentSurfaceRequest?.let { surfaceRequest ->
//        val currentSurfaceRequest: SurfaceRequest? by vm.surfaceRequests.collectAsState()

        // CoordinateTransformer for transforming from Offsets to Surface coordinates
        val coordinateTransformer = remember { MutableCoordinateTransformer() }

        CameraXViewfinder(
            surfaceRequest = surfaceRequest,
            implementationMode = ImplementationMode.EXTERNAL,
            modifier = modifier.pointerInput(Unit) {
                detectTapGestures { offset -> vm.onTap(coordinateTransformer.run { offset.transform() }) }
            },
            coordinateTransformer = coordinateTransformer,
        )
    }
}
