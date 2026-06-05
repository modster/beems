package com.greeffer.xcam.fx.x


import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.camera.compose.CameraXViewfinder
import androidx.camera.core.CameraEffect.IMAGE_CAPTURE
import androidx.camera.core.CameraEffect.PREVIEW
import androidx.camera.core.CameraEffect.VIDEO_CAPTURE
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.core.SurfaceRequest
import androidx.camera.core.UseCaseGroup
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.media3.effect.Media3Effect
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.common.Effect
import androidx.media3.common.util.UnstableApi
import com.greeffer.xcam.data.XCameraFilter
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.Executor

/**
 * Shared camera scaffold. Encapsulates the CameraX permission flow, Media3 effect pipeline,
 * use-case-group binding, surface request handling, and capture file persistence.
 *
 * Callers supply a [bottomBar] composable (capture button + optional filter selector) and
 * an optional [imageCaptureConfig] to customise the [ImageCapture] use case. The scaffold
 * hands the bottomBar a [onCapture] callback that, when invoked, persists a JPEG to the
 * app's cache directory.
 */
@OptIn(UnstableApi::class)
@Composable
fun XCameraScaffold(
)
{
    val logTag = "XCameraScaffold"
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val mainExecutor = remember { ContextCompat.getMainExecutor(context) }
    var surfaceRequest by remember { mutableStateOf<SurfaceRequest?>(null) }
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }
    var requestedPermission by remember { mutableStateOf(false) }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasCameraPermission = granted
    }
    
    // 1. Keep track of the currently selected filter asset
    var selectedFilter: XCameraFilter by remember { mutableStateOf(XCameraFilter.NONE) }
    
    val preview = remember { Preview.Builder().build() }
    val imageCapture = remember { ImageCapture.Builder().build() }
    
    // 2. Initialize the Media3 Camera Pipeline Adapter
    val media3Effect = remember {
        Media3Effect(
            context,
            IMAGE_CAPTURE or PREVIEW or VIDEO_CAPTURE, // Applies to both live preview and taken photo
            mainExecutor
        ) { error -> Log.e("FilterSelector", "Media3 execution error: ${error.message}", error) }
    }
    
    LaunchedEffect(hasCameraPermission, requestedPermission) {
        if (! hasCameraPermission && ! requestedPermission)
        {
            requestedPermission = true
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }
    
    // 3. Bind the Camera Lifecycle
    LaunchedEffect(hasCameraPermission) {
        if (! hasCameraPermission) return@LaunchedEffect
        
        preview.setSurfaceProvider { request ->
            surfaceRequest = request
        }
    }
    // 3. React to state selection changes by hot-swapping the active effects pipeline
    LaunchedEffect(selectedFilter) {
        media3Effect.setEffects(selectedFilter.getMedia3Effects())
    }
    
    // 4. Bind CameraX Use Cases once during configuration initialization
    LaunchedEffect(Unit) {
        val cameraProvider = ProcessCameraProvider.getInstance(context).get()
        val useCaseGroup = UseCaseGroup.Builder()
          .addUseCase(preview)
          .addUseCase(imageCapture)
          .addEffect(media3Effect)
          .build()
        
        try
        {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                CameraSelector.DEFAULT_BACK_CAMERA,
                useCaseGroup
            )
        }
        catch (e: Exception)
        {
            Log.e("FilterSelector", "Camera initialization binding failed", e)
        }
        
    }
    
    Box(modifier = Modifier.fillMaxSize()) {
        if (! hasCameraPermission)
        {
            Button(
                onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) },
                modifier = Modifier.align(Alignment.Center),
            ) { Text("Grant camera permission") }
            return@Box
        }
        surfaceRequest?.let { request ->
            CameraXViewfinder(surfaceRequest = request, modifier = Modifier.fillMaxSize())
        }
    }
}

/** Saves a JPEG to [Context.getCacheDir] with a timestamped filename and logs the result. */
internal fun takePicture(
    context: Context,
    imageCapture: ImageCapture,
    executor: Executor,
    filenamePrefix: String = "IMG",
    logTag: String = "CameraScaffold",
)
{
    val photoFile = File(
        context.cacheDir,
        "${filenamePrefix}_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())}.jpg",
    )
    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
    imageCapture.takePicture(
        outputOptions,
        executor,
        object: ImageCapture.OnImageSavedCallback
        {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults)
            {
                Log.d(logTag, "Photo saved: ${outputFileResults.savedUri ?: Uri.fromFile(photoFile)}")
            }
            
            override fun onError(exception: ImageCaptureException)
            {
                Log.e(logTag, "Photo capture failed", exception)
            }
        },
    )
}
