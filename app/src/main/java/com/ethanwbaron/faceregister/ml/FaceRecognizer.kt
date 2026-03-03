package com.ethanwbaron.faceregister.ml

import kotlin.math.sqrt

class FaceRecognizer {
    private val registeredFaces = mutableMapOf<String, FloatArray>()

    fun register(name: String, embedding: FloatArray) {
        registeredFaces[name] = embedding
    }
    fun recognize(embedding: FloatArray, threshold: Float = 0.9f): Pair<String, Float>? {
        var bestMatch: String? = null
        var bestDistance = Float.MAX_VALUE

        registeredFaces.forEach { (name, registered) ->
            val distance = cosineDistance(embedding, registered)
            if (distance < bestDistance) {
                bestDistance = distance
                bestMatch = name
            }
        }

        return if (bestMatch != null && bestDistance < threshold) {
            Pair(bestMatch, bestDistance)
        } else null
    }

    private fun cosineDistance(a: FloatArray, b: FloatArray): Float {
        var dot = 0f
        var normA = 0f
        var normB = 0f
        for (i in a.indices) {
            dot += a[i] * b[i]
            normA += a[i] * a[i]
            normB += b[i] * b[i]
        }
        return 1f - (dot / (sqrt(normA) * sqrt(normB)))
    }
}