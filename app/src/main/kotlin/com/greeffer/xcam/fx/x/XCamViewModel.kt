package com.greeffer.xcam.fx.x

import android.content.Context
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.core.SurfaceRequest
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.greeffer.xcam.data.XCameraFilterEntries
import com.greeffer.xcam.ui.common.ResourceUiState
import com.greeffer.xcam.ui.common.asResourceState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

typealias XCamUiState = ResourceUiState<List<String>>

class XCamViewModel(
  repo: XCameraFilterEntries,
): ViewModel()
{


    val uiState: StateFlow<XCamUiState> = repo.data.asResourceState(viewModelScope)


    sealed class XCamActions
    {


        object LOADING: XCamActions()
        object XVIDEO: XCamActions()
        object XPHOTO: XCamActions()
        object TAP: XCamActions()
    }


    @Suppress("UNUSED_PARAMETER")
    val actions: XCamActions
        get()
        {
            // Return a default action or compute based on current UI state
            return XCamActions.TAP
        }


    // -- Surface request handling --
    private val _surfaceRequests = MutableStateFlow<SurfaceRequest?>(null)

    val surfaceRequests: StateFlow<SurfaceRequest?>
        get() = _surfaceRequests.asStateFlow()


    @Suppress("UNUSED_PARAMETER")
    fun produceSurfaceRequests(previewUseCase: Preview = Preview.Builder().build())
    { // Always publish new SurfaceRequests from Preview.
        previewUseCase.setSurfaceProvider { newSurfaceRequest ->
            _surfaceRequests.value = newSurfaceRequest
        }
    }


    @Suppress("UNUSED_PARAMETER")
    @Composable
    fun XCamUIState(modifier: Modifier = Modifier)
    {
        // Implement UI based on current action
        when (actions)
        {
            is XCamActions.LOADING ->
            {
                // Show loading indicator (e.g., CircularProgressIndicator)
            }

            is XCamActions.XVIDEO  ->
            {
                // Show surface request UI
                val isVideoMode = true
                val isRecording = false
                val isPaused = false
            }

            is XCamActions.XPHOTO  ->
            {
                // Show tap interaction UI
            }

            is XCamActions.TAP     ->
            { /* default tap state */
            }
        }
        Box(modifier = modifier)
    }

}


// Add capturePhoto function to XCamViewModel
fun XCamViewModel.capturePhoto(
  context: Context,
  xImageCapture: ImageCapture,
)
{
    val xMainExecutor: java.util.concurrent.Executor = ContextCompat.getMainExecutor(context)
    val outputDirectory = getOutputDirectory(context) // Assuming you have this utility
    val photoFile = java.io.File(
      outputDirectory,
      System.currentTimeMillis().toString() + ".jpg"
    )
    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
    xImageCapture.takePicture(outputOptions, xMainExecutor, object: ImageCapture.OnImageSavedCallback
    {
        override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults)
        { /* handle */
        }

        override fun onError(exception: ImageCaptureException)
        { /* handle */
        }
    })
}
