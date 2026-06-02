package com.example.empty_activity

import androidx.camera.core.CameraImplementationModeCompat
import androidx.camera.core.SurfaceRequest
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.camera.core.MutableCoordinateTransformer

@Composable
fun cameraXViewfinder(
    surfaceRequest: SurfaceRequest,
    modifier: Modifier = Modifier,
    implementationMode: CameraImplementationModeCompat = CameraImplementationModeCompat.chooseCompatibleMode(surfaceRequest.camera.cameraInfo),
    coordinateTransformer: MutableCoordinateTransformer? = null,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit
) {
    Box(modifier = modifier, contentAlignment = alignment) {
    }
}