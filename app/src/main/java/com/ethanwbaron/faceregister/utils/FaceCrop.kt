package com.ethanwbaron.faceregister.utils

import android.graphics.Bitmap
import android.graphics.Rect

fun cropFace(source: Bitmap, boundingBox: Rect): Bitmap? {
    val padding = 20
    val left = (boundingBox.left - padding).coerceAtLeast(0)
    val top = (boundingBox.top - padding).coerceAtLeast(0)
    val right = (boundingBox.right + padding).coerceAtMost(source.width)
    val bottom = (boundingBox.bottom + padding).coerceAtMost(source.height)

    val width = right - left
    val height = bottom - top

    if (width <= 0 || height <= 0) return null

    return Bitmap.createBitmap(source, left, top, width, height)
}