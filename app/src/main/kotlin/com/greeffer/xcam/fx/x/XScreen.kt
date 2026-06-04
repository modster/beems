package com.greeffer.xcam.fx.x

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavKey
import com.greeffer.xcam.data.DefaultDataRepository
import com.greeffer.xcam.theme.XCamTheme
import com.greeffer.xcam.ui.main.Greeting
import com.greeffer.xcam.ui.main.MainScreen
import com.greeffer.xcam.ui.main.MainScreenUiState
import com.greeffer.xcam.ui.main.MainScreenViewModel

@Composable
fun XScreen(
  onItemClick: (NavKey) -> Unit,
  modifier: Modifier = Modifier,
  viewModel: MainScreenViewModel = viewModel { MainScreenViewModel(DefaultDataRepository()) },
  xViewModel: XViewModel = viewModel(),
  data: List<String>,

  ) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    when (state) {
        MainScreenUiState.Loading -> {
                    Text("Loading...")
        }

        is MainScreenUiState.Success -> {
            MainScreen(data = (state as MainScreenUiState.Success).data, modifier = modifier)
        }

        is MainScreenUiState.Error -> {
            Text("Error loading data: ${(state as MainScreenUiState.Error).throwable.message}")
        }
    }

    Box(modifier= Modifier.fillMaxSize().pointerInput(Unit) { detectTapGestures { } }) {
        XViewfinder(vm = xViewModel, modifier = Modifier.fillMaxSize())
        Scaffold(modifier = Modifier.fillMaxSize()) {
            // UI elements on top of the viewfinder
            Column(modifier = Modifier.fillMaxSize().padding(it)) {
                Row (modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("XCam")
                }
            }
        }
    }
}
@Composable
internal fun XCamScreen(
  data: List<String>,
  modifier: Modifier = Modifier,
) {
    Column(modifier) { data.forEach { Greeting(it) } }
}

@Preview(showBackground = true)
@Composable
fun XScreenPreview() {
    XCamTheme { XCamScreen(listOf("Android", "CameraXViewfinder", "CameraEffect")) }
}

@Preview(showBackground = true, widthDp = 340)
@Composable
fun XScreenPortraitPreview() {
    XCamTheme { XCamScreen(listOf("Android", "CameraXViewfinder", "CameraEffect")) }
}

