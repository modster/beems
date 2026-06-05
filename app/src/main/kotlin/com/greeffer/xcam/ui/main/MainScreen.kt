package com.greeffer.xcam.ui.main

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavKey
import com.greeffer.xcam.data.DefaultDataRepository
import com.greeffer.xcam.fx.x.XCamScreen
import com.greeffer.xcam.ui.common.ResourceUiState

@Composable
fun MainScreen(
    onItemClick: (NavKey) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MainScreenViewModel = viewModel { MainScreenViewModel(DefaultDataRepository()) },
)
{
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    when (state)
    {
        ResourceUiState.Loading    ->
        {
            Text("Loading...")
        }
        
        is ResourceUiState.Success ->
        {
            XCamScreen(
                modifier = modifier,
                onItemClick = onItemClick
            )
        }
        
        is ResourceUiState.Error   ->
        {
            Text("Error loading data: ${(state as ResourceUiState.Error).throwable.message}")
        }
    }
}

