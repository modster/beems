package com.greeffer.xcam.fx.x

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.annotation.RequiresApi
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.util.UnstableApi
import com.greeffer.xcam.data.CameraCoreUtil.getAllCamerasPropertiesJSONArray
import com.greeffer.xcam.data.CameraCoreUtil.writeFileExternalStorage
import com.greeffer.xcam.data.XCameraFilters
import com.greeffer.xcam.ui.main.MainScreenViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import org.koin.compose.viewmodel.koinViewModel

@RequiresApi(Build.VERSION_CODES.P)
@OptIn(UnstableApi::class)
@Composable
fun FilterSelectorCameraScreen(
  vm: MainScreenViewModel = koinViewModel<MainScreenViewModel>(),
  modifier: Modifier
)
{
    val horizontalPadder = 8.dp

    val state by vm.uiState.collectAsStateWithLifecycle()

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
    var selectedFilter: XCameraFilters by remember { mutableStateOf(XCameraFilters.NONE) }

    val preview = remember { Preview.Builder().build() }

    val imageCapture = remember { ImageCapture.Builder().build() }

    // 2. Initialize the Media3 Camera Pipeline Adapter
    val media3Effect = remember {
        Media3Effect(
          context, IMAGE_CAPTURE or PREVIEW or VIDEO_CAPTURE, // Applies to both live preview and taken photo
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

    // 3. Bind the Camera Lifecycle if we have permission
    LaunchedEffect(hasCameraPermission) {
        if (! hasCameraPermission) return@LaunchedEffect

        preview.setSurfaceProvider { request ->
            surfaceRequest = request
        }

        try
        {
            val cameraProvider = ProcessCameraProvider.getInstance(context).get()
            val cameraInfos = cameraProvider.availableCameraInfos
            val infos = getAllCamerasPropertiesJSONArray(cameraInfos)
            val outputDir = context.getExternalFilesDir(null)
            if (outputDir != null)
            {
                val outputFile = File(outputDir, "cameraInfos.json")
                writeFileExternalStorage(outputFile.absolutePath, infos.toString(2))
                Log.d("FilterSelector", "Saved camera info to: ${outputFile.absolutePath}")
            }
        }
        catch (e: Exception)
        {
            Log.e("FilterSelector", "Failed to retrieve or save camera info", e)
        }
    }

    // 3a. React to state selection changes by hot-swapping the active effects pipeline
    LaunchedEffect(selectedFilter) {
        media3Effect.setEffects(selectedFilter.getMedia3Effects())
    }

    // 4. Bind CameraX Use Cases once during configuration initialization
    LaunchedEffect(Unit) {
        val cameraProvider = ProcessCameraProvider.getInstance(context).get()
        val useCaseGroup =
          UseCaseGroup.Builder().addUseCase(preview).addUseCase(imageCapture).addEffect(media3Effect).build()

        try
        {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
              lifecycleOwner, CameraSelector.DEFAULT_BACK_CAMERA, useCaseGroup
            )

        }
        catch (e: Exception)
        {
            Log.e("FilterSelector", "Camera initialization binding failed", e)
        }
    }

    // 5. Build User Interface Layout
    Box(modifier = Modifier.fillMaxSize()) {

        if (! hasCameraPermission)
        {
            Button(
              onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) },
              modifier = Modifier.align(Alignment.Center)
            ) {
                Text("Grant camera permission")
            }
            return@Box
        }

        // 5 Full screen camera stream CameraXViewfinder (view finder wrapper)
        surfaceRequest?.let { request ->
            CameraXViewfinder(
              surfaceRequest = request, modifier = Modifier.fillMaxSize()
            )
        }

        // 6. Bottom control bar containing Selector and Trigger button
        Column(
          modifier = Modifier
            .align(Alignment.BottomCenter)
            .fillMaxWidth()
            .background(Color.Black.copy(alpha = 0.4f))
            .padding(vertical = 50.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // 7. Dynamic Horizontal Filter Option Selector Bar
            LazyRow(
              contentPadding = PaddingValues(horizontal = horizontalPadder), horizontalArrangement = Arrangement
              .spacedBy(horizontalPadder),
              modifier = Modifier.fillMaxWidth()
            ) {
                items(XCameraFilters.entries.toTypedArray()) { filter ->
                    val isSelected = filter == selectedFilter

                    Card(
                      colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.DarkGray
                      ), modifier = Modifier
                      .width(85.dp)
                      .height(35.dp)
                      .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }) { selectedFilter = filter }) {
                        Box(
                          contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()
                        ) {
                            Text(
                              text = filter.displayName,
                              color = if (isSelected) MaterialTheme.colorScheme.onPrimary else Color.White,
                              style = MaterialTheme.typography.bodyMedium,
                              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            // 8. Traditional circular camera capture button
            Box(
              modifier = Modifier
                .size(90.dp)
                .clip(CircleShape)
                .background(Color.White)
                .clickable(
                  indication = null, interactionSource = remember { MutableInteractionSource() }) {
                    takePicture(
                      context, imageCapture, mainExecutor
                    )
                }, contentAlignment = Alignment.Center
            ) {
                Box(
                  modifier = Modifier
                    .size(75.dp)
                    .clip(CircleShape)
                    .background(Color.Transparent)
                    .background(Color.Red)
                ) // Give it a camera shutter concentric ring appearance
            }
        }
    }
}


// 9. Capture the photo/video
private fun takePicture(
  context: android.content.Context,
  imageCapture: ImageCapture,
  executor: java.util.concurrent.Executor
)
{
    val appName = "XCam"
    val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())

    // 10. Newer phones use this method
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
    { // Android 10+ (API 29+): Use MediaStore for scoped storage
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "IMG_$timestamp.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, "${Environment.DIRECTORY_PICTURES}/$appName")
        }

        val outputOptions = ImageCapture.OutputFileOptions.Builder(
          context.contentResolver, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues
        ).build()

        imageCapture.takePicture(
          outputOptions, executor, object: ImageCapture.OnImageSavedCallback
        {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults)
            {
                Log.d("FilterSelector", "Photo saved: ${outputFileResults.savedUri}")
            }

            override fun onError(exception: ImageCaptureException)
            {
                Log.e("FilterSelector", "Photo capture failed", exception)
            }
        })
    }

    // 11. Android 9 and below: Use legacy external storage
    else
    {
        val picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val appDir = File(picturesDir, appName).apply { mkdirs() }
        val photoFile = File(appDir, "IMG_$timestamp.jpg")
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
          outputOptions, executor, object: ImageCapture.OnImageSavedCallback
        {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults)
            {
                val savedUri = outputFileResults.savedUri
                               ?: Uri.fromFile(photoFile)
                Log.d("FilterSelector", "Photo saved: $savedUri")
            }

            override fun onError(exception: ImageCaptureException)
            {
                Log.e("FilterSelector", "Photo capture failed", exception)
            }
        })
    }
}
