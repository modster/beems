package com.greeffer.xcam.fx.x

import androidx.annotation.OptIn
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.util.UnstableApi
import com.greeffer.xcam.data.XCameraFilterEntries
import com.greeffer.xcam.ui.common.ResourceUiState
import com.greeffer.xcam.ui.main.MainScreenViewModel

@Composable
fun XCamScreen(
  viewModel: MainScreenViewModel = viewModel {
      MainScreenViewModel(
        XCameraFilterEntries(),
      )
  },
)
{
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    when (state)
    {
        is ResourceUiState.Loading ->
        {
            Text("Loading...")
        }

        is ResourceUiState.Success ->
        {

            XCam()

        }

        is ResourceUiState.Error   ->
        {
            Text("Error loading data: ${(state as ResourceUiState.Error).throwable.message}")
        }
    }

}


@OptIn(UnstableApi::class)
@Composable
internal fun XCam()
{
    Scaffold(modifier = Modifier.fillMaxSize()) { padding ->
        Box(
          modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .pointerInput(Unit) { detectTapGestures { } }
        ) {
            FilterSelectorCameraScreen(
              vm = viewModel(),
              modifier = Modifier.fillMaxSize(),
            )
        }
    }
}
