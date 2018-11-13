package com.example.lilitmuradyan.fliprotate

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.animation.LinearInterpolator
import android.animation.Animator
import android.animation.AnimatorListenerAdapter


class FlipRotateView : View {

    private var paint: Paint? = null
    private var bitmapMatrix: Matrix? = null
    private var displayX = 0
    private var displayY = 0
    private var defaultScaleAmount = 1f
    private var flipRotateItem: FlipRotateItem = FlipRotateItem()
    private val ROTATE_LEFT = 0
    private val ROTATE_RIGHT = 1
    private val FLIP_HORIZONTALLY = 2
    private val FLIP_VERTICALLY = 3
    var temporaryProgress = 0f


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
        startRotatingAnimation(100, ROTATE_RIGHT)
    }

    fun rotateLeft() {
        startRotatingAnimation(100, ROTATE_LEFT)
    }

    //    direction 1 -> right, 0-> left
    fun startRotatingAnimation(secs: Long, direction: Int) {
        var mTimerAnimator: ValueAnimator = ValueAnimator.ofInt(0, 90)
        mTimerAnimator.duration = secs
        mTimerAnimator.interpolator = LinearInterpolator()
        mTimerAnimator.addUpdateListener { animation -> rotateWithAnimation(animation.animatedValue as Int, direction) }
        mTimerAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                temporaryProgress = 0f
                if (direction == ROTATE_LEFT)
                    flipRotateItem.rotateAngle -= 90
                else flipRotateItem.rotateAngle += 90
            }
        })
        mTimerAnimator.start()
    }

    fun startFlippingAnimation(secs: Long, direction: Int) {
        var mTimerAnimator: ValueAnimator = ValueAnimator.ofFloat(1f, -1f)
        mTimerAnimator.duration = secs
        mTimerAnimator.interpolator = LinearInterpolator()
        mTimerAnimator.addUpdateListener { animation -> flipWithAnimation(animation.animatedValue as Float, direction) }
        mTimerAnimator.start()
    }

    fun flipHorizontally() {
        bitmapMatrix!!.postScale(-1f, 1f, displayX / 2f, displayY / 2f)
        invalidate()
    }

    fun flipVertically() {
        bitmapMatrix!!.postScale(1f, -1f, displayX / 2f, displayY / 2f)
        invalidate()
    }

    private fun rotateWithAnimation(progress: Int, direction: Int) {
        if (direction == ROTATE_LEFT)
            bitmapMatrix!!.postRotate(-(progress - temporaryProgress), displayX / 2f, displayY / 2f)
        else bitmapMatrix!!.postRotate(progress - temporaryProgress, displayX / 2f, displayY / 2f)
        temporaryProgress = progress.toFloat()
        invalidate()
    }

    private fun flipWithAnimation(progress: Float, direction: Int) {
        bitmapMatrix!!.postScale(defaultScaleAmount, -1f, displayX / 2f, displayY / 2f)
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

    private fun centerImage() {
        bitmapMatrix!!.postTranslate(displayX / 2f - defaultScaleAmount * flipRotateItem.image!!.width / 2f, displayY / 2f - defaultScaleAmount * flipRotateItem.image!!.height / 2)
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


