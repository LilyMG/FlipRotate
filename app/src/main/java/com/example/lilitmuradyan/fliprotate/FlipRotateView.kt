package com.example.lilitmuradyan.fliprotate

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.util.Log
import android.view.WindowManager


class FlipRotateView : View {

    var image: Bitmap? = null
    private var paint: Paint? = null
    private var bitmapMatrix: Matrix? = null


    constructor(ctx: Context, attributeSet: AttributeSet) : super(ctx, attributeSet) {
        setBackgroundColor(Color.GRAY)
        paint = Paint(Paint.ANTI_ALIAS_FLAG)
        bitmapMatrix = Matrix()
    }

    fun rotateRight() {
        bitmapMatrix?.postRotate(25f)
        invalidate()
    }

    fun setBitmap(image: Bitmap?) {
        this.image = image
        calculateDefaultScale()
    }

    fun calculateDefaultScale() {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = wm.defaultDisplay
        val size = Point()
        display.getSize(size)
        var scaleAmount: Float
        scaleAmount = if (image!!.width > image!!.height) {
            size.x / (image!!.width).toFloat()
        } else {
            size.y / (image!!.height).toFloat()
        }
        bitmapMatrix?.postScale(scaleAmount, scaleAmount)
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawBitmap(image, bitmapMatrix, paint)

        val info = String.format("Info: size = %s x %s, bytes = %s (%s), config = %s",
                image?.width,
                image?.height,
                image?.byteCount,
                image?.rowBytes,
                image?.config)
        Log.d("FlipRotateView", info)
    }
}


