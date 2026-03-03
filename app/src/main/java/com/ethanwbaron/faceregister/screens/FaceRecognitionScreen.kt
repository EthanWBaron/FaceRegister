package com.ethanwbaron.faceregister.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.ethanwbaron.faceregister.camera.CameraAnalyzer
import com.ethanwbaron.faceregister.camera.CameraPermissionDenied
import com.ethanwbaron.faceregister.camera.CameraPreview
import com.ethanwbaron.faceregister.ml.FaceDetectorProcessor
import com.ethanwbaron.faceregister.ui.face.FaceOverlay
import com.ethanwbaron.faceregister.utils.CameraPermissionHandler
import com.google.mlkit.vision.face.Face

@Composable
fun FaceDetectionScreen() {

    var faces by remember { mutableStateOf(emptyList<Face>()) }
    var imageWidth by remember { mutableStateOf(480) }
    var imageHeight by remember { mutableStateOf(360) }

    val processor = remember { FaceDetectorProcessor() }

    val analyzer = remember {
        CameraAnalyzer(processor) { detectedFaces, imgWidth, imgHeight ->
            faces = detectedFaces
            imageWidth = imgWidth
            imageHeight = imgHeight
        }
    }

    CameraPermissionHandler(
        onPermissionGranted = {
            Box {
                CameraPreview(analyzer)
                FaceOverlay(
                    faces = faces,
                    imageWidth = imageWidth,
                    imageHeight = imageHeight,
                    isFrontCamera = true
                )
            }
        },
        onPermissionDenied = {
            CameraPermissionDenied()
        }
    )
}