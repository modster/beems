package com.greeffer.xcam.fx.x

import android.R.attr.data
import androidx.annotation.OptIn
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.util.UnstableApi
import androidx.navigation3.runtime.NavKey
import com.greeffer.xcam.data.DefaultDataRepository
import com.greeffer.xcam.data.XCameraFilter
import com.greeffer.xcam.ui.common.ResourceUiState
import com.greeffer.xcam.ui.main.MainScreenViewModel

@Composable
fun XCamScreen(
    onItemClick: (NavKey) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MainScreenViewModel = viewModel { MainScreenViewModel(DefaultDataRepository()) },
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
            FilterSelectorCameraScreen()
            
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
        
        }
    }
}
