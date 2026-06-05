package com.greeffer.xcam.fx.x
import androidx.camera.core.Preview
import androidx.camera.core.SurfaceRequest
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.greeffer.xcam.data.DefaultDataRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class XCamViewModel(
  dataRepository: DefaultDataRepository,
) : ViewModel() {
    val uiState: StateFlow<XCamUiState> =
      dataRepository.data
        .map<List<String>, XCamUiState>(XCamUiState::Success)
        .catch { emit(XCamUiState.Error(it)) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), XCamUiState.Loading)

    private val _surfaceRequests = MutableStateFlow<SurfaceRequest?>(null)

    val surfaceRequests: StateFlow<SurfaceRequest?>
        get() = _surfaceRequests.asStateFlow()

    private fun produceSurfaceRequests(previewUseCase: Preview) {
        // Always publish new SurfaceRequests from Preview
        previewUseCase.setSurfaceProvider { newSurfaceRequest ->
            _surfaceRequests.value = newSurfaceRequest
        }
    }

    fun onTap(offsetToSurface: Any)
    {
    }

    // fun focusOnPoint(surfaceBounds: Size, x: Float, y: Float) {
    //     // Create point for CameraX's CameraControl.startFocusAndMetering() and submit...
    // }

    // // ...
}

sealed interface XCamUiState {
    object Loading : XCamUiState

    data class Error(
      val throwable: Throwable,
    ) : XCamUiState

    data class Success(
      val data: List<String>,
    ) : XCamUiState
}
