package com.greeffer.xcam.ui.main

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat

interface MainRepository
{


    val hasCameraPermission: MutableState<Boolean>
    val requestedPermission: MutableState<Boolean>

    fun refreshCameraPermission()
    fun onCameraPermissionResult(granted: Boolean)
}

class MainRepositoryImpl(
  private val context: Context,
): MainRepository
{


    override val hasCameraPermission: MutableState<Boolean> = mutableStateOf(false)
    override val requestedPermission: MutableState<Boolean> = mutableStateOf(false)

    init
    {
        refreshCameraPermission()
    }

    override fun refreshCameraPermission()
    {
        hasCameraPermission.value = ContextCompat.checkSelfPermission(
          context,
          Manifest.permission.CAMERA,
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCameraPermissionResult(granted: Boolean)
    {
        hasCameraPermission.value = granted
    }

}
