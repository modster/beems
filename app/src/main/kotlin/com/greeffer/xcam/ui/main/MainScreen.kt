package com.greeffer.xcam.ui.main

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavKey
import com.greeffer.xcam.data.XCameraFilterEntries
import com.greeffer.xcam.fx.x.FilterSelectorCameraScreen
import com.greeffer.xcam.ui.common.ResourceUiState

@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun MainScreen(
  modifier: Modifier = Modifier,
  @Suppress("UNUSED_PARAMETER")
  onItemClick: (NavKey) -> Boolean,
)
{
    val vm = viewModel { MainScreenViewModel(XCameraFilterEntries()) }

    val state by vm.uiState.collectAsStateWithLifecycle()

    when (state)
    {
        ResourceUiState.Loading ->
        {
            Text("Loading...")
        }

        is ResourceUiState.Success<*> ->
        {
            FilterSelectorCameraScreen(
              vm = vm,
              modifier = modifier
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        vm.onTapToFocus(offset)
                    }
                }
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        // Pinch to zoom
                        vm.onZoom(zoom)
                        // Pan to move the zoomed preview.
                        vm.onPan(pan)
                    }
                }
            )
        }

        is ResourceUiState.Error ->
        {
            Text("Error loading data: ${(state as ResourceUiState.Error).throwable.message}")
        }
    }
}
