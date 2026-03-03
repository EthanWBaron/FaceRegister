package com.ethanwbaron.faceregister

import com.ethanwbaron.faceregister.ml.FaceRecognizer
import org.junit.Test
import org.junit.Assert.*
import org.junit.Before

class FaceRecognizerTest {

    private lateinit var recognizer: FaceRecognizer

    @Before
    fun setup() {
        recognizer = FaceRecognizer()
    }

    @Test
    fun `returns Unknown when no faces registered`() {
        val embedding = FloatArray(128) { 0.5f }
        val result = recognizer.recognize(embedding)
        assertEquals("Unknown",result!!.first)
    }

    @Test
    fun `recognizes exact same embedding`() {
        val embedding = FloatArray(128) { it.toFloat() }
        recognizer.register("Alice", embedding)

        val result = recognizer.recognize(embedding)
        assertNotNull(result)
        assertEquals("Alice", result?.first)
    }

    @Test
    fun `recognizes similar embedding within threshold`() {
        val embedding = FloatArray(128) { 1.0f }
        recognizer.register("Bob", embedding)

        // Slightly noisy version of the same embedding
        val similar = FloatArray(128) { 1.0f + (it % 3) * 0.001f }
        val result = recognizer.recognize(similar)

        assertEquals("Bob", result?.first)
    }

    @Test
    fun `returns Unknown for completely different embedding`() {
        val alice = FloatArray(128) { 1.0f }
        recognizer.register("Alice", alice)

        // Orthogonal vector — cosine distance will be high
        val different = FloatArray(128) { if (it % 2 == 0) 1.0f else -1.0f }
        val result = recognizer.recognize(different, threshold = 0.1f)

        assertEquals("Unknown",result!!.first)
    }

    @Test
    fun `picks closest match among multiple registered faces`() {
        val alice = FloatArray(128) { 1.0f }
        val bob   = FloatArray(128) { -1.0f }
        recognizer.register("Alice", alice)
        recognizer.register("Bob", bob)

        // Closer to Alice
        val query = FloatArray(128) { 0.9f }
        val result = recognizer.recognize(query)

        assertEquals("Alice", result?.first)
    }

    @Test
    fun `registering same name twice overwrites previous embedding`() {
        val old = FloatArray(128) { -1.0f }
        val new = FloatArray(128) { 1.0f }
        recognizer.register("Alice", old)
        recognizer.register("Alice", new)

        // Query close to new embedding should still match
        val query = FloatArray(128) { 0.99f }
        val result = recognizer.recognize(query)

        assertEquals("Alice", result?.first)
    }

    @Test
    fun `distance is zero for identical embeddings`() {
        val embedding = FloatArray(128) { it.toFloat() + 1f }
        recognizer.register("Test", embedding)

        val result = recognizer.recognize(embedding)
        assertNotNull(result)
        assertEquals(0f, result!!.second, 0.0001f)
    }
}