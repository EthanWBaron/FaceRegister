package com.ethanwbaron.faceregister.ml
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions

class FaceDetectorProcessor {

    private val detector = FaceDetection.getClient(
        FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
            .setContourMode(FaceDetectorOptions.LANDMARK_MODE_NONE)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_NONE)
            .enableTracking()
            .build()
    )

    fun process(
        image: InputImage,
        onResult: (List<Face>) -> Unit
    ) {
        detector.process(image)
            .addOnSuccessListener { faces ->
                onResult(faces)
            }
            .addOnFailureListener {
                onResult(emptyList())
            }
    }
}