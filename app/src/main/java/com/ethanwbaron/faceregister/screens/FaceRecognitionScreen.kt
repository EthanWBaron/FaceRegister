package com.ethanwbaron.faceregister.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.ethanwbaron.faceregister.camera.CameraAnalyzer
import com.ethanwbaron.faceregister.camera.CameraPermissionDenied
import com.ethanwbaron.faceregister.camera.CameraPreview
import com.ethanwbaron.faceregister.ml.FaceDetectorProcessor
import com.ethanwbaron.faceregister.ml.FaceNetModel
import com.ethanwbaron.faceregister.ml.FaceRecognizer
import com.ethanwbaron.faceregister.ui.face.FaceOverlay
import com.ethanwbaron.faceregister.utils.CameraPermissionHandler
import com.google.mlkit.vision.face.Face

@Composable
fun FaceDetectionScreen() {

    val context = LocalContext.current

    // Detection state
    var results by remember { mutableStateOf(emptyList<Pair<Face, String?>>()) }
    var imageWidth by remember { mutableIntStateOf(480) }
    var imageHeight by remember { mutableIntStateOf(360) }

    // Registration state
    var nameInput by remember { mutableStateOf("") }
    var statusMessage by remember { mutableStateOf("") }
    val processor = remember { FaceDetectorProcessor() }
    val faceNetModel = remember { FaceNetModel(context) }
    val recognizer = remember { FaceRecognizer() }

    val analyzer = remember {
        CameraAnalyzer(
            processor = processor,
            faceNetModel = faceNetModel,
            recognizer = recognizer,
            onResult = { detectedResults, imgWidth, imgHeight ->
                results = detectedResults
                imageWidth = imgWidth
                imageHeight = imgHeight
            }
        )
    }

    CameraPermissionHandler(
        onPermissionGranted = {
            Box(modifier = Modifier.fillMaxSize()) {

                CameraPreview(analyzer)
                FaceOverlay(
                    results = results,
                    imageWidth = imageWidth,
                    imageHeight = imageHeight,
                    isFrontCamera = true
                )

                // Registration UI at the TOP
                Column(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = nameInput,
                            onValueChange = { nameInput = it },
                            label = { Text("Name") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )

                        Button(
                            onClick = {
                                if (nameInput.isBlank()) {
                                    statusMessage = "Enter a name first"
                                    return@Button
                                }
                                analyzer.registerNextFace(nameInput.trim()) { success ->
                                    statusMessage = if (success)
                                        "Registered: ${nameInput.trim()}"
                                    else
                                        "No face detected, try again"
                                    nameInput = ""
                                }
                            }
                        ) {
                            Text("Register")
                        }
                    }

                    if (statusMessage.isNotBlank()) {
                        Text(
                            text = statusMessage,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }
                }
            }
        },
        onPermissionDenied = {
            CameraPermissionDenied()
        }
    )
}