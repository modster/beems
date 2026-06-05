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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavKey
import com.greeffer.xcam.data.DefaultDataRepository
import com.greeffer.xcam.ui.main.Greeting

@Composable
fun XCamScreen(
  onItemClick: (NavKey) -> Unit,
  modifier: Modifier = Modifier,
  viewModel: XCamViewModel = viewModel { XCamViewModel(DefaultDataRepository()) },
  ) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    when (state) {
        is XCamUiState.Loading -> {
            Text("Loading...")
        }

        is XCamUiState.Success -> {
            XCam(data = (state as XCamUiState.Success).data, modifier = modifier, onItemClick = onItemClick)
        }

        is XCamUiState.Error -> {
            Text("Error loading data: ${(state as XCamUiState.Error).throwable.message}")
        }
    }


}
@Composable
internal fun XCam(
  data: List<String>,
  modifier: Modifier = Modifier,
  vm: XCamViewModel = XCamViewModel(dataRepository = DefaultDataRepository()),
  onItemClick: (NavKey) -> Unit,
  )
{
    Box(
      modifier = Modifier
        .fillMaxSize()
        .pointerInput(Unit) { detectTapGestures { } }) {

        Scaffold(modifier = Modifier.fillMaxSize()) {
            // UI elements on top of the viewfinder
            Column(
              modifier = Modifier
                .fillMaxSize()
                .padding(it)
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column(modifier) { data.forEach { item -> Greeting(item) } }
                }
            }
        }
        FilterSelectorCameraScreen()
    }
}

//@Preview(showBackground = true)
//@Composable
//fun XPreview() {
//    XCamTheme { XCam(listOf("Android", "CameraXViewfinder", "CameraEffect")) {} }
//}

//@Preview(showBackground = true, widthDp = 340)
//@Composable
//fun XPortraitPreview() {
//    XCamTheme { XCam(listOf("Android", "CameraXViewfinder", "CameraEffect")) {} }
//}

