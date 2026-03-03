package com.ethanwbaron.faceregister.ml

import android.content.Context
import android.graphics.Bitmap
import com.google.ai.edge.litert.CompiledModel
import com.google.ai.edge.litert.Accelerator
import com.google.ai.edge.litert.Environment
import androidx.core.graphics.scale

class FaceNetModel(context: Context) {

    private val env = Environment.create()

    private val model = CompiledModel.create(
        context.assets,
        "facenet.tflite",
        CompiledModel.Options(Accelerator.CPU),
        env
    )

    fun getEmbedding(faceBitmap: Bitmap): FloatArray {
        val resized = faceBitmap.scale(160, 160)

        // Flatten bitmap pixels to normalized floats
        val pixels = IntArray(160 * 160)
        resized.getPixels(pixels, 0, 160, 0, 0, 160, 160)

        val floatData = FloatArray(160 * 160 * 3)
        var idx = 0
        for (pixel in pixels) {
            floatData[idx++] = ((pixel shr 16 and 0xFF) - 127.5f) / 127.5f // R
            floatData[idx++] = ((pixel shr 8  and 0xFF) - 127.5f) / 127.5f // G
            floatData[idx++] = ((pixel        and 0xFF) - 127.5f) / 127.5f // B
        }

        val inputBuffers = model.createInputBuffers()
        val outputBuffers = model.createOutputBuffers()

        inputBuffers[0].writeFloat(floatData)

        model.run(inputBuffers, outputBuffers)

        return outputBuffers[0].readFloat()
    }

    fun close() {
        model.close()
        env.close()
    }
}