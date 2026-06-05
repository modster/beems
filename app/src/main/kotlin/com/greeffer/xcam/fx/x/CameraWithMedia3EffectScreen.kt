package com.greeffer.xcam.fx.x

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.compose.CameraXViewfinder
import androidx.camera.core.CameraEffect.IMAGE_CAPTURE
import androidx.camera.core.CameraEffect.PREVIEW
import androidx.camera.core.CameraEffect.VIDEO_CAPTURE
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.core.SurfaceRequest
import androidx.camera.core.UseCaseGroup
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.media3.effect.Media3Effect
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.common.util.UnstableApi
import androidx.media3.effect.RgbFilter
import java.io.File
import java.util.concurrent.Executor

@UnstableApi
@Composable
fun CameraWithMedia3EffectScreen(
    vm : XCamViewModel
)
{
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val mainExecutor = remember { ContextCompat.getMainExecutor(context) }
    var surfaceRequest by remember { mutableStateOf<SurfaceRequest?>(null) }
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context , Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }
    var requestedPermission by remember { mutableStateOf(false) }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasCameraPermission = granted
    }

    // 1. Initialize CameraX UseCases
    val preview = remember {
        Preview
            .Builder()
            .build()
    }
    val imageCapture = remember {
        ImageCapture
            .Builder()
            .setCaptureMode(CAPTURE_MODE_MINIMIZE_LATENCY)
            .build()
    }

    // 2. Create the Media3 Effect
    val media3Effect = remember {
        Media3Effect(
            context ,
            IMAGE_CAPTURE or PREVIEW or VIDEO_CAPTURE , // Applies to both live preview and taken photo
            mainExecutor
        ) { error ->
            Log.e("CameraMedia3" , "Effect processing error: ${error.message}" , error)
        }.apply {
            // Apply a grayscale filter using Media3 standard RGB transformations
            setEffects(listOf(RgbFilter.createGrayscaleFilter()))
        }
    }

    LaunchedEffect(hasCameraPermission , requestedPermission) {
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

        val cameraProvider = ProcessCameraProvider
            .getInstance(context)
            .get()

        // Bundle your UseCases and Effects together into a UseCaseGroup
        val useCaseGroup = UseCaseGroup
            .Builder()
            .addUseCase(preview)
            .addUseCase(imageCapture)
            .addEffect(media3Effect) // Attaches Media3 pipeline directly to the stream
            .build()

        try
        {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner ,
                CameraSelector.DEFAULT_BACK_CAMERA ,
                useCaseGroup
            )
        }
        catch (e : Exception)
        {
            Log.e("CameraMedia3" , "Binding failed, lol" , e)
        }
    }

    // 4. Compose UI Layout
    Box(modifier = Modifier.fillMaxSize()) {
        if (! hasCameraPermission)
        {
            Button(
                onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) } ,
                modifier = Modifier.align(Alignment.Center)
            ) {
                Text("Grant camera permission")
            }
            return@Box
        }

        // Official Compose-native Surface wrapper for CameraX
        surfaceRequest?.let { request ->
            CameraXViewfinder(
                surfaceRequest = request ,
                modifier = Modifier.fillMaxSize()
            )
        }

        // Capture Action Button
        Button(
            onClick = { capturePhoto(context , imageCapture , mainExecutor) } ,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
        ) {
            Text("Capture Photo")
        }
    }
}


// 5. Trigger Filtered Photo Storage
private fun capturePhoto(context : Context , imageCapture : ImageCapture , executor : Executor)
{
    val outputFile = File(context.cacheDir , "media3_effect_${System.currentTimeMillis()}.jpg")
    val outputOptions = ImageCapture.OutputFileOptions
        .Builder(outputFile)
        .build()

    imageCapture.takePicture(
        outputOptions ,
        executor ,
        object : ImageCapture.OnImageSavedCallback
        {
            override fun onImageSaved(outputFileResults : ImageCapture.OutputFileResults)
            {
                Log.d("CameraMedia3" , "Filtered photo saved successfully: ${outputFile.absolutePath}")
            }

            override fun onError(exception : ImageCaptureException)
            {
                Log.e("CameraMedia3" , "Photo capture failed: ${exception.message}" , exception)
            }
        }
    )
}
