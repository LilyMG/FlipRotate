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
import android.graphics.PointF
import android.view.MotionEvent


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
    var temporaryRotationProgress = 0f
    var TAG = "FlipROtateView"
    // These matrices will be used to move and zoom image
    var savedMatrix = Matrix()

    // We can be in one of these 3 states
    val NONE = 0
    val DRAG = 1
    val ZOOM = 2
    var mode = NONE

    // Remember some things for zooming
    var start = PointF()
    var mid = PointF()
    var oldDist = 1f


    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val scale: Float

        // Handle touch events here...
        when (event!!.action and MotionEvent.ACTION_MASK) {

            MotionEvent.ACTION_DOWN //first finger down only
            -> {
                savedMatrix.set(bitmapMatrix)
                start.set(event.x, event.y)
                Log.d(TAG, "mode=DRAG")
                mode = DRAG
            }
            MotionEvent.ACTION_UP //first finger lifted
                , MotionEvent.ACTION_POINTER_UP //second finger lifted
            -> {
                mode = NONE
                Log.d(TAG, "mode=NONE")
            }
            MotionEvent.ACTION_POINTER_DOWN //second finger down
            -> {
                oldDist = spacing(event).toFloat() // calculates the distance between two points where user touched.
                Log.d(TAG, "oldDist=$oldDist")
                // minimal distance between both the fingers
                if (oldDist > 5f) {
                    savedMatrix.set(bitmapMatrix)
                    midPoint(mid, event) // sets the mid-point of the straight line between two points where user touched.
                    mode = ZOOM
                    Log.d(TAG, "mode=ZOOM")
                }
            }

            MotionEvent.ACTION_MOVE -> if (mode == DRAG) { //movement of first finger
                bitmapMatrix?.set(savedMatrix)
                if (left >= -392) {
                    bitmapMatrix?.postTranslate(event.x - start.x, event.y - start.y)
                }
            } else if (mode == ZOOM) { //pinch zooming
                val newDist = spacing(event)
                Log.d(TAG, "newDist=$newDist")
                if (newDist > 5f) {
                    bitmapMatrix?.set(savedMatrix)
                    scale = (newDist / oldDist).toFloat() //thinking I need to play around with this value to limit it**
                    bitmapMatrix?.postScale(scale, scale, mid.x, mid.y)
                }
            }
        }
        invalidate()
        return true
    }


    private fun spacing(event: MotionEvent): Double {
        val x = event.getX(0) - event.getX(1)
        val y = event.getY(0) - event.getY(1)
        return Math.sqrt((x * x + y * y).toDouble())
    }

    private fun midPoint(point: PointF, event: MotionEvent) {
        val x = event.getX(0) + event.getX(1)
        val y = event.getY(0) + event.getY(1)
        point.set(x / 2, y / 2)
    }
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
                temporaryRotationProgress = 0f
                if (direction == ROTATE_LEFT)
                    flipRotateItem.rotateAngle -= 90
                else flipRotateItem.rotateAngle += 90
            }
        })
        mTimerAnimator.start()
    }

    private fun startFlippingAnimation(secs: Long, direction: Int) {
        var mTimerAnimator = ValueAnimator()
        if (direction == FLIP_HORIZONTALLY) {
            mTimerAnimator = if (flipRotateItem.isFlippedHorizontally)
                ValueAnimator.ofFloat(-1f, 1f)
            else ValueAnimator.ofFloat(1f, -1f)
        } else if (direction == FLIP_VERTICALLY) {
            mTimerAnimator = if (flipRotateItem.isFlippedVertically)
                ValueAnimator.ofFloat(-1f, 1f)
            else ValueAnimator.ofFloat(1f, -1f)
        }
        mTimerAnimator.duration = secs
        mTimerAnimator.interpolator = LinearInterpolator()
        mTimerAnimator.addUpdateListener { animation -> flipWithAnimation(animation.animatedValue as Float, direction) }
        mTimerAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                if (direction == FLIP_HORIZONTALLY)
                    flipRotateItem.isFlippedHorizontally = !flipRotateItem.isFlippedHorizontally
                else if (direction == FLIP_VERTICALLY)
                    flipRotateItem.isFlippedVertically = !flipRotateItem.isFlippedVertically
            }
        })
        mTimerAnimator.start()
    }

    fun flipHorizontally() {
        startFlippingAnimation(100, FLIP_HORIZONTALLY)
    }

    fun flipVertically() {
        startFlippingAnimation(100, FLIP_VERTICALLY)
    }

    private fun rotateWithAnimation(progress: Int, direction: Int) {
        if (direction == ROTATE_LEFT)
            bitmapMatrix!!.postRotate(-(progress - temporaryRotationProgress), displayX / 2f, displayY / 2f)
        else bitmapMatrix!!.postRotate(progress - temporaryRotationProgress, displayX / 2f, displayY / 2f)
        temporaryRotationProgress = progress.toFloat()
        invalidate()
    }

    private fun flipWithAnimation(progress: Float, direction: Int) {
        if (direction == FLIP_HORIZONTALLY)
            bitmapMatrix!!.setScale(defaultScaleAmount * progress, 1f, displayX / 2f, displayY / 2f)
        else
            bitmapMatrix!!.setScale(1f, defaultScaleAmount * progress, displayX / 2f, displayY / 2f)
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


