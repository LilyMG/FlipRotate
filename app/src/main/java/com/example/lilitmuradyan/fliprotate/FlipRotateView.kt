package com.example.lilitmuradyan.fliprotate

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.util.Log
import android.view.WindowManager
import android.graphics.Bitmap
import android.view.animation.LinearInterpolator


class FlipRotateView : View {

    private var paint: Paint? = null
    private var bitmapMatrix: Matrix? = null
    private var displayX = 0
    private var displayY = 0
    private var defaultScaleAmount = 1f
    var flipRotateItem : FlipRotateItem = FlipRotateItem()

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
        start(100)
        flipRotateItem.rotateAngle += 90
    }

    fun start(secs: Long) {
        var mTimerAnimator = ValueAnimator.ofInt(0, 90)
        mTimerAnimator.duration = secs
        mTimerAnimator.interpolator = LinearInterpolator()
        mTimerAnimator.addUpdateListener { animation -> animateView(animation.animatedValue as Int) }
        mTimerAnimator.start()
    }

    private fun animateView(progress: Int) {
        bitmapMatrix!!.setRotate(flipRotateItem.rotateAngle + progress.toFloat(), displayX/2f, displayY/2f)
        invalidate()
    }

    fun setBitmap(image: Bitmap?) {
        flipRotateItem.image = image
        calculateDefaultScale()
        centerImage()
    }

    private fun calculateDefaultScale() {
        defaultScaleAmount = if (flipRotateItem.image!!.width > flipRotateItem.image!!.height) {
            (displayX / (flipRotateItem.image!!.width).toFloat())
        } else {
            (displayY / (flipRotateItem.image!!.height).toFloat())
        }
        bitmapMatrix?.postScale(defaultScaleAmount, defaultScaleAmount)
    }

    private fun centerImage(){
        bitmapMatrix!!.postTranslate(displayX/2f - defaultScaleAmount * flipRotateItem.image!!.width/2f, displayY/2f - defaultScaleAmount * flipRotateItem.image!!.height/2 )
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawBitmap(flipRotateItem.image, bitmapMatrix, paint)

        val info = String.format("Info: size = %s x %s, bytes = %s (%s), config = %s",
                flipRotateItem.image?.width,
                flipRotateItem.image?.height,
                flipRotateItem.image?.byteCount,
                flipRotateItem.image?.rowBytes,
                flipRotateItem.image?.config)
        Log.d("FlipRotateView", info)
    }
}


