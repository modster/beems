package com.greeffer.xcam.ui.main

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat
import com.google.accompanist.permissions.PermissionState
import kotlin.contracts.contract


interface MainRepository
{
    
    var permission: PermissionState
    var hasPermission: Boolean
    val permissionLauncher:
}

class MainRepositoryImpl
{
    
    
    override hasCameraPermission = mutableStateOf(
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    )
}

var requestedPermission = { mutableStateOf(false) }
val permissionLauncher = MutableStateOf(
contract = ActivityResultContracts.RequestPermission () { granted ->
        hasCameraPermission.value = granted
    }
    
}
}

