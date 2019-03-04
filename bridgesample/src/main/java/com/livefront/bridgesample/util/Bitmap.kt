package com.livefront.bridgesample.util

import android.graphics.Bitmap
import android.graphics.Color

fun generateNoisyStripedBitmap(callback: (Bitmap) -> Unit) {
    SimpleTask(::generateNoisyStripedBitmapInternal, callback).execute()
}

private fun generateNoisyStripedBitmapInternal(): Bitmap {
    val randomTo255 = {
        Math.round(Math.random() * 255).toInt()
    }
    val randomColor = {
        Color.argb(
                randomTo255(),
                randomTo255(),
                randomTo255(),
                randomTo255()
        )
    }
    val size = 800
    val color1 = randomColor()
    val color2 = randomColor()
    var baseColor = color1
    val bandHeight = size / 20
    val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    (0 until size).forEach { height ->
        (0 until size).forEach { width ->
            if (width % bandHeight == 0) {
                baseColor = if (baseColor == color1) color2 else color1
            }
            val pixelColor = Color.argb(
                    randomTo255(),
                    Color.red(baseColor),
                    Color.green(baseColor),
                    Color.blue(baseColor)
            )
            bitmap.setPixel(height, width, pixelColor)
        }
    }
    return bitmap
}
