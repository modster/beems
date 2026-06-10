package com.greeffer.xcam.ui.components

import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CaptureRequest
import androidx.camera.camera2.interop.Camera2CameraControl
import androidx.camera.camera2.interop.Camera2CameraInfo
import androidx.camera.camera2.interop.CaptureRequestOptions
import androidx.camera.view.LifecycleCameraController
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import kotlin.math.pow
import kotlin.math.roundToInt

/**
 * Composable that applies camera settings (ISO, shutter, WB, EV) to the camera controller.
 *
 * Also tracks whether the device supports manual sensor controls via [manualControlSupportedState].
 * If manual controls are not available, settings gracefully fall back to exposure compensation + presets.
 *
 * @param controller The camera controller to apply settings to
 * @param iso Camera ISO/SensorSensitivity value
 * @param shutterSeconds Shutter duration in seconds
 * @param whiteBalanceKelvin Color temperature in Kelvin
 * @param evBias Exposure value bias (in stops)
 * @param cameraSessionKey Key to re-trigger application (e.g., incremented when switching cameras)
 * @param manualControlSupportedState Mutable state that reflects whether manual sensor controls are available on this device
 */
@Composable
fun BindCameraSettings(
  controller: LifecycleCameraController,
  iso: Float,
  shutterSeconds: Float,
  whiteBalanceKelvin: Float,
  evBias: Float,
  cameraSessionKey: Any? = Unit,
  manualControlSupportedState: MutableState<Boolean> = mutableStateOf(false),
)
{
    LaunchedEffect(
      controller,
      cameraSessionKey,
      iso,
      shutterSeconds,
      whiteBalanceKelvin,
      evBias,
    ) {
        runCatching {
            applyCameraSettings(
              controller = controller,
              iso = iso.roundToInt(),
              shutterSeconds = shutterSeconds,
              whiteBalanceKelvin = whiteBalanceKelvin.roundToInt(),
              evBias = evBias,
              manualControlSupportedState = manualControlSupportedState,
            )
        }
    }
}

private fun applyCameraSettings(
  controller: LifecycleCameraController,
  iso: Int,
  shutterSeconds: Float,
  whiteBalanceKelvin: Int,
  evBias: Float,
  manualControlSupportedState: MutableState<Boolean>,
)
{
    val cameraInfo = controller.cameraInfo
                     ?: return
    val cameraControl = controller.cameraControl
                        ?: return
    val camera2Info = Camera2CameraInfo.from(cameraInfo)
    val camera2Control = Camera2CameraControl.from(cameraControl)
    val requestOptions = CaptureRequestOptions.Builder()

    val sensitivityRange =
      camera2Info.getCameraCharacteristic(CameraCharacteristics.SENSOR_INFO_SENSITIVITY_RANGE)
    val exposureTimeRange =
      camera2Info.getCameraCharacteristic(CameraCharacteristics.SENSOR_INFO_EXPOSURE_TIME_RANGE)

    val manualSensorSupported = sensitivityRange != null && exposureTimeRange != null
    manualControlSupportedState.value = manualSensorSupported

    if (manualSensorSupported)
    {
        val clampedIso = iso.coerceIn(sensitivityRange.lower, sensitivityRange.upper)
        val requestedExposureNs =
          (shutterSeconds.coerceAtLeast(MIN_SHUTTER_SECONDS) * NANOS_PER_SECOND).toLong()
        val evAdjustedExposureNs =
          (requestedExposureNs.toDouble() * 2.0.pow(evBias.toDouble()))
            .toLong()
            .coerceIn(exposureTimeRange.lower, exposureTimeRange.upper)

        requestOptions.setCaptureRequestOption(
          CaptureRequest.CONTROL_AE_MODE,
          CaptureRequest.CONTROL_AE_MODE_OFF,
        )
        requestOptions.setCaptureRequestOption(
          CaptureRequest.SENSOR_SENSITIVITY,
          clampedIso,
        )
        requestOptions.setCaptureRequestOption(
          CaptureRequest.SENSOR_EXPOSURE_TIME,
          evAdjustedExposureNs,
        )

        cameraControl.setExposureCompensationIndex(0)
    }
    else
    {
        requestOptions.setCaptureRequestOption(
          CaptureRequest.CONTROL_AE_MODE,
          CaptureRequest.CONTROL_AE_MODE_ON,
        )
        applyExposureCompensation(controller, evBias)
    }

    requestOptions.setCaptureRequestOption(
      CaptureRequest.CONTROL_AWB_MODE,
      selectAwbMode(
        whiteBalanceKelvin = whiteBalanceKelvin,
        availableModes = camera2Info.getCameraCharacteristic(
          CameraCharacteristics.CONTROL_AWB_AVAILABLE_MODES,
        ),
      ),
    )

    camera2Control.setCaptureRequestOptions(requestOptions.build())
}

private fun applyExposureCompensation(
  controller: LifecycleCameraController,
  evBias: Float,
)
{
    val cameraInfo = controller.cameraInfo
                     ?: return
    val cameraControl = controller.cameraControl
                        ?: return
    val exposureState = cameraInfo.exposureState
    if (! exposureState.isExposureCompensationSupported) return

    val stepEv = exposureState.exposureCompensationStep.toFloat()
    if (stepEv <= 0f) return

    val compensationRange = exposureState.exposureCompensationRange
    val targetIndex = (evBias / stepEv)
      .roundToInt()
      .coerceIn(compensationRange.lower, compensationRange.upper)

    cameraControl.setExposureCompensationIndex(targetIndex)
}

private fun selectAwbMode(
  whiteBalanceKelvin: Int,
  availableModes: IntArray?,
): Int
{
    val supportedModes = availableModes?.toSet().orEmpty()
    val candidates = when
    {
        whiteBalanceKelvin <= 3200 -> listOf(
          CaptureRequest.CONTROL_AWB_MODE_INCANDESCENT,
          CaptureRequest.CONTROL_AWB_MODE_WARM_FLUORESCENT,
          CaptureRequest.CONTROL_AWB_MODE_AUTO,
        )

        whiteBalanceKelvin <= 4300 -> listOf(
          CaptureRequest.CONTROL_AWB_MODE_WARM_FLUORESCENT,
          CaptureRequest.CONTROL_AWB_MODE_FLUORESCENT,
          CaptureRequest.CONTROL_AWB_MODE_AUTO,
        )

        whiteBalanceKelvin <= 5600 -> listOf(
          CaptureRequest.CONTROL_AWB_MODE_DAYLIGHT,
          CaptureRequest.CONTROL_AWB_MODE_AUTO,
        )

        whiteBalanceKelvin <= 6800 -> listOf(
          CaptureRequest.CONTROL_AWB_MODE_CLOUDY_DAYLIGHT,
          CaptureRequest.CONTROL_AWB_MODE_DAYLIGHT,
          CaptureRequest.CONTROL_AWB_MODE_AUTO,
        )

        whiteBalanceKelvin <= 8000 -> listOf(
          CaptureRequest.CONTROL_AWB_MODE_TWILIGHT,
          CaptureRequest.CONTROL_AWB_MODE_CLOUDY_DAYLIGHT,
          CaptureRequest.CONTROL_AWB_MODE_AUTO,
        )

        else                       -> listOf(
          CaptureRequest.CONTROL_AWB_MODE_SHADE,
          CaptureRequest.CONTROL_AWB_MODE_CLOUDY_DAYLIGHT,
          CaptureRequest.CONTROL_AWB_MODE_AUTO,
        )
    }

    return candidates.firstOrNull { it in supportedModes }
           ?: CaptureRequest.CONTROL_AWB_MODE_AUTO
}

private const val MIN_SHUTTER_SECONDS = 0.001f
private const val NANOS_PER_SECOND = 1_000_000_000f


