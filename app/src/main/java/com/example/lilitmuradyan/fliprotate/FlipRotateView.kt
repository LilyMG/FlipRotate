package com.example.lilitmuradyan.fliprotate

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.util.Log
import android.view.WindowManager
import android.graphics.Bitmap




class FlipRotateView : View {

    var image: Bitmap? = null
    private var paint: Paint? = null
    private var bitmapMatrix: Matrix? = null
    private var displayX = 0
    private var displayY = 0
    private var defaultScaleAmount = 1f


    constructor(ctx: Context, attributeSet: AttributeSet) : super(ctx, attributeSet) {
        setBackgroundColor(Color.GRAY)
        paint = Paint(Paint.ANTI_ALIAS_FLAG)
        bitmapMatrix = Matrix()
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = wm.defaultDisplay
        val size = Point()
        display.getSize(size)
        displayX = size.x
        displayY = size.y
    }

    fun rotateRight() {
        bitmapMatrix?.postRotate(5f, displayX/2f, displayY/2f)
        invalidate()
    }

    fun setBitmap(image: Bitmap?) {
        this.image = image
        calculateDefaultScale()
        centerImage()
    }

    private fun calculateDefaultScale() {
        defaultScaleAmount = if (image!!.width > image!!.height) {
            (displayX / (image!!.width).toFloat())
        } else {
            (displayY / (image!!.height).toFloat())
        }
        bitmapMatrix?.postScale(defaultScaleAmount, defaultScaleAmount)
    }

    private fun centerImage(){
        bitmapMatrix!!.postTranslate(displayX/2f - defaultScaleAmount * image!!.width/2f, displayY/2f - defaultScaleAmount * image!!.height/2 )
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


