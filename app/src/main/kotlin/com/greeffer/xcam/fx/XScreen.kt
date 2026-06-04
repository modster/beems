package com.greeffer.xcam.fx

import androidx.camera.core.Preview
import androidx.camera.core.SurfaceRequest
import androidx.camera.viewfinder.compose.MutableCoordinateTransformer
import androidx.camera.viewfinder.core.ImplementationMode
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import com.greeffer.xcam.fx.XViewModel
import com.greeffer.xcam.fx.XViewfinder
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import com.greeffer.xcam.data.DefaultDataRepository
import com.greeffer.xcam.theme.XCamTheme

@Composable
fun XScreen(
    viewModel: XViewModel,
    modifier: Modifier = Modifier,
) {
  onItemClick: (NavKey) -> Unit,
    modifierW: Modifier = Modifier,
    viewModel: XViewModel = viewModel { XViewModel(DefaultDataRepository()) },
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    when (state) {
        XScreenUiState.Loading -> {
                    Text("Loading...")
        }

        is XScreenUiState.Success -> {
            XScreen(data = (state as XScreenUiState.Success).data, modifier = modifier)
        }

        is XScreenUiState.Error -> {
            Text("Error loading data: ${(state as XScreenUiState.Error).throwable.message}")
        }
    }

    Box(modifier= Modifier.fillMaxSize().pointerInput(Unit) { detectTapGestures { } }) {
        XViewfinder(viewModel = viewModel, modifier = Modifier.fillMaxSize())
        Scaffold(modifier = Modifier.fillMaxSize(), backgroundColor = Color.Transparent) {
            // UI elements on top of the viewfinder
            Column(modifier = Modifier.padding(it)) {
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


// @Composable
// fun XScreen(
//     onItemClick: (NavKey) -> Unit,
//     modifier: Modifier = Modifier,
//     viewModel: XScreenViewModel = viewModel { XScreenViewModel(DefaultDataRepository()) },
// ) {
//     val state by viewModel.uiState.collectAsStateWithLifecycle()
//     when (state) {
//         XScreenUiState.Loading -> {
//             // Blank
//         }

//         is XScreenUiState.Success -> {
//             XScreen(data = (state as XScreenUiState.Success).data, modifier = modifier)
//         }

//         is XScreenUiState.Error -> {
//             Text("Error loading data: ${(state as XScreenUiState.Error).throwable.message}")
//         }
//     }
// }

// @Composable
// internal fun XCamScreen(
//     data: List<String>,
//     modifier: Modifier = Modifier,
// ) {
//     Column(modifier) { data.forEach { Greeting(it) } }
// }
