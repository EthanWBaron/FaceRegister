package com.ethanwbaron.faceregister.utils

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect

@Composable
fun CameraPermissionHandler(
    onPermissionGranted: @Composable () -> Unit,
    onPermissionDenied: @Composable () -> Unit
) {
    val hasPermissionState = cameraPermissionState()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasPermissionState.value = granted
    }

    LaunchedEffect(Unit) {
        if (!hasPermissionState.value) {
            launcher.launch(Manifest.permission.CAMERA)
        }
    }

    if (hasPermissionState.value) {
        onPermissionGranted()
    } else {
        onPermissionDenied()
    }
}