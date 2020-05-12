package com.example.raggapaint

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.view.View
import androidx.core.content.res.ResourcesCompat

private const val STROKE_WIDTH =12f

class MyCanvasView(context: Context): View(context) {

    //for caching what has been drawn before
    private lateinit var extraCanvas:Canvas
    private lateinit var extraBitmap: Bitmap

    private val backgroundColor = ResourcesCompat.getColor(resources, R.color.colorBackground, null)
    private val drawColor = ResourcesCompat.getColor(resources, R.color.colorPaint, null)

    // setup the paint
    private val paint = Paint().apply {
        color = drawColor
        // smooth edges
        isAntiAlias = true
        // Dithering affects how colors with higher-precision than the device are down-sampled
        isDither =true
        // we want a line = stroke
        style = Paint.Style.STROKE
        // how do the joinsof two line look
        strokeJoin = Paint.Join.ROUND
        // how does the end of a line look
        strokeCap = Paint.Cap.ROUND
        // how wide is the drawn line in px
        strokeWidth = STROKE_WIDTH
    }

    // the path a user draws
    private var path = Path()

    override fun onSizeChanged(width: Int, height: Int, oldwidth: Int, oldheight: Int) {
        super.onSizeChanged(width, height, oldwidth, oldheight)
        // to prevent memory leak on creating new Bitmaps, recycle an existing
        if(::extraBitmap.isInitialized) extraBitmap.recycle()
        // create new Bitmap with new height/width and the recommended color profile
        extraBitmap = Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888)
        // create new Canvas for this Bitmap
        extraCanvas = Canvas(extraBitmap)
        // draw the background color
        extraCanvas.drawColor(backgroundColor)
    }

    // note: this is a  different canvas then extraCanvas
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // draw the background from extraBitmap, offset 0 left, 0 top and set the paint when needed
        canvas.drawBitmap(extraBitmap, 0f,0f, null)
    }
}