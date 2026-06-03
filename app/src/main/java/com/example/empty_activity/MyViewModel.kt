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

    // fun focusOnPoint(surfaceBounds: Size, x: Float, y: Float) {
    //     // Create point for CameraX's CameraControl.startFocusAndMetering() and submit...
    // }

    // // ...
}