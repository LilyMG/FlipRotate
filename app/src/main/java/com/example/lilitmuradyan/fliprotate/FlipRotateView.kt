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

   fun setBitmap(image: Bitmap?){
       this.image = image
       val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
       val display = wm.defaultDisplay
       val size = Point()
       display.getSize(size)
       val screenWidth = size.x
       val screenHeight = size.y
       bitmapMatrix?.postScale( screenWidth/(image!!.width).toFloat(), screenWidth/(image!!.width).toFloat())
//       bitmapMatrix?.postTranslate( width.toFloat()/2, height.toFloat()/2)
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


