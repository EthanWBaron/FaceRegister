package com.ethanwbaron.faceregister.ui.face

import android.graphics.Rect
import android.graphics.RectF
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import com.google.mlkit.vision.face.Face

@Composable
fun FaceOverlay(
    results: List<Pair<Face, String?>>,
    imageWidth: Int,
    imageHeight: Int,
    isFrontCamera: Boolean = false
) {
    val textPaint = remember {
        android.graphics.Paint().apply {
            color = android.graphics.Color.GREEN
            textSize = 48f
        }
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        results.forEach { (face, label) ->
            val rect = mapRect(
                face.boundingBox,
                imageWidth.toFloat(),
                imageHeight.toFloat(),
                size.width,
                size.height,
                isFrontCamera
            )

            drawRect(
                color = Color.Green,
                topLeft = Offset(rect.left, rect.top),
                size = Size(rect.width(), rect.height()),
                style = Stroke(width = 4f)
            )

            // Draw name label above the box
            if (label != null) {
                drawContext.canvas.nativeCanvas.drawText(
                    label,
                    rect.left,
                    rect.top - 10f,
                    textPaint
                )
            }
        }
    }
}


fun mapRect(
    boundingBox: Rect,
    imageWidth: Float,
    imageHeight: Float,
    previewWidth: Float,
    previewHeight: Float,
    isFrontCamera: Boolean = false
): RectF {
    val scaleX = previewWidth / imageWidth
    val scaleY = previewHeight / imageHeight

    val mappedLeft = boundingBox.left * scaleX
    val mappedRight = boundingBox.right * scaleX

    val finalLeft = if (isFrontCamera) previewWidth - mappedRight else mappedLeft
    val finalRight = if (isFrontCamera) previewWidth - mappedLeft else mappedRight

    return RectF(
        finalLeft,
        boundingBox.top * scaleY,
        finalRight,
        boundingBox.bottom * scaleY
    )
}