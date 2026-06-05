package com.greeffer.xcam.ui.common

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

sealed interface ResourceUiState<out T>
{
    
    data object Loading: ResourceUiState<Nothing>
    data class Error(val throwable: Throwable): ResourceUiState<Nothing>
    data class Success<T>(val data: T): ResourceUiState<T>
}

fun <T> Flow<T>.asResourceState(
    scope: CoroutineScope,
    started: SharingStarted = SharingStarted.WhileSubscribed(5000),
): StateFlow<ResourceUiState<T>> =
  this
    .map<T, ResourceUiState<T>> { ResourceUiState.Success(it) }
    .catch { emit(ResourceUiState.Error(it)) }
    .stateIn(scope, started, ResourceUiState.Loading)
