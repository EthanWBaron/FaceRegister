package com.ethanwbaron.faceregister.camera

import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.ethanwbaron.faceregister.ml.FaceDetectorProcessor
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face

class CameraAnalyzer(
    private val processor: FaceDetectorProcessor,
    private val onFacesDetected: (List<Face>, Int, Int) -> Unit
) : ImageAnalysis.Analyzer {

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
            onFacesDetected(faces, imageWidth, imageHeight)
            imageProxy.close()
        }
    }
}