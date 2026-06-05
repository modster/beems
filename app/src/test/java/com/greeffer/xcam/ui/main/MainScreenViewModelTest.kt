package com.greeffer.xcam.ui.main

import com.greeffer.xcam.data.DataRepository
import com.greeffer.xcam.ui.common.ResourceUiState
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Test

class MainScreenViewModelTest {
    @Test
    fun uiState_initiallyLoading() =
        runTest {
            val viewModel = MainScreenViewModel(FakeMyModelRepository())
            assertEquals(viewModel.uiState.first(), ResourceUiState.Loading)
        }

    @Test
    fun uiState_onItemSaved_isDisplayed() =
        runTest {
            val viewModel = MainScreenViewModel(FakeMyModelRepository())
            assertEquals(viewModel.uiState.first(), ResourceUiState.Loading)
        }
}

private class FakeMyModelRepository : DataRepository {
    override val data: Flow<List<String>> = flow { emit(listOf("Sample")) }
}
