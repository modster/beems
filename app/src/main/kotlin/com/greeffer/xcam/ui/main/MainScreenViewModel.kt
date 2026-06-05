package com.greeffer.xcam.ui.main

import android.content.Context
import android.util.Log
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.core.SurfaceRequest
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

class MainScreenViewModel(
    dataRepository: DataRepository,
): ViewModel()
{
    
    val uiState: StateFlow<MainScreenUiState> = dataRepository.data.asResourceState(viewModelScope)
    
    val xPreviewUseCase = Preview
      .Builder()
      .build()
    
    private val _surfaceRequests = MutableStateFlow<SurfaceRequest?>(null)
    
    val surfaceRequests: StateFlow<SurfaceRequest?>
        get() = _surfaceRequests.asStateFlow()
    
    init
    {
        produceSurfaceRequests(xPreviewUseCase)
    }
    
    private fun produceSurfaceRequests(previewUseCase: Preview)
    {
        // Always publish new SurfaceRequests from Preview
        previewUseCase.setSurfaceProvider { newSurfaceRequest ->
            _surfaceRequests.value = newSurfaceRequest
        }
    }
    
    
    fun onTap(offsetToSurface: Any)
    {
    }
    
    // fun focusOnPoint(surfaceBounds: Size, x: Float, y: Float) {
    //     // Create point for CameraX's CameraControl.startFocusAndMetering() and submit...
    // }
    
    // // ...
    
    fun capturePhoto(context: Context, imageCapture: ImageCapture, executor: Executor)
    {
        val outputFile = File(context.cacheDir, "media3_effect_${System.currentTimeMillis()}.jpg")
        val outputOptions = ImageCapture.OutputFileOptions
          .Builder(outputFile)
          .build()
        
        imageCapture.takePicture(
            outputOptions,
            executor,
            object: ImageCapture.OnImageSavedCallback
            {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults)
                {
                    Log.d("CameraMedia3", "Filtered photo saved successfully: ${outputFile.absolutePath}")
                }
                
                override fun onError(exception: ImageCaptureException)
                {
                    Log.e("CameraMedia3", "Photo capture failed: ${exception.message}", exception)
                }
            }
        )
    }
}
