package com.ethanwbaron.faceregister.camera

import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.ethanwbaron.faceregister.ml.FaceDetectorProcessor
import com.ethanwbaron.faceregister.ml.FaceNetModel
import com.ethanwbaron.faceregister.ml.FaceRecognizer
import com.ethanwbaron.faceregister.utils.cropFace
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face

class CameraAnalyzer(
    private val processor: FaceDetectorProcessor,
    private val faceNetModel: FaceNetModel,
    private val recognizer: FaceRecognizer,
    private val onResult: (List<Pair<Face, String?>>, Int, Int) -> Unit
) : ImageAnalysis.Analyzer {

    // Pending registration: name + callback
    private var pendingName: String? = null
    private var pendingCallback: ((Boolean) -> Unit)? = null

    fun registerNextFace(name: String, onDone: (Boolean) -> Unit) {
        pendingName = name
        pendingCallback = onDone
    }

    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image ?: return
        val rotation = imageProxy.imageInfo.rotationDegrees

        val imageWidth = if (rotation == 90 || rotation == 270)
            imageProxy.height else imageProxy.width
        val imageHeight = if (rotation == 90 || rotation == 270)
            imageProxy.width else imageProxy.height

        val inputImage = InputImage.fromMediaImage(mediaImage, rotation)

        processor.process(inputImage) { faces ->
            val bitmap = imageProxy.toBitmap()

            // Handle pending registration
            val nameToRegister = pendingName
            val callback = pendingCallback
            if (nameToRegister != null && callback != null) {
                val firstFace = faces.firstOrNull()
                if (firstFace != null) {
                    val cropped = cropFace(bitmap, firstFace.boundingBox)
                    if (cropped != null) {
                        val embedding = faceNetModel.getEmbedding(cropped)
                        recognizer.register(nameToRegister, embedding)
                        callback(true)
                    } else {
                        callback(false)
                    }
                } else {
                    callback(false)
                }
                pendingName = null
                pendingCallback = null
            }

            // Normal recognition pass
            val results = faces.map { face ->
                val cropped = cropFace(bitmap, face.boundingBox)
                val label = if (cropped != null) {
                    val embedding = faceNetModel.getEmbedding(cropped)
                    recognizer.recognize(embedding)?.first
                } else null
                Pair(face, label)
            }

            onResult(results, imageWidth, imageHeight)
            imageProxy.close()
        }
    }
}