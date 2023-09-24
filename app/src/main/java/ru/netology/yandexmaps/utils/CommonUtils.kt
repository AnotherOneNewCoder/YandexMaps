package ru.netology.yandexmaps.utils


import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.widget.Toast
import androidx.core.content.ContextCompat


object CommonUtils {
//    typealias CommonColors = R.color
//    typealias CommonDrawables = R.drawable
//    typealias CommonId = R.id

    fun Context.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(this, message, duration).show()
    }
    fun createBitmapFromVector(context: Context, art: Int): Bitmap? {
        val drawble = ContextCompat.getDrawable(context, art) ?: return null
        val bitmap = Bitmap.createBitmap(
            drawble.intrinsicWidth,
            drawble.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        ) ?: return null
        val canvas = Canvas(bitmap)
        drawble.setBounds(0, 0, canvas.width, canvas.height)
        drawble.draw(canvas)
        return bitmap
    }


}