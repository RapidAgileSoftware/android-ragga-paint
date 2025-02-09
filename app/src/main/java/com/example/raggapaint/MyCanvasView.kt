package com.example.raggapaint

import android.content.Context
import android.graphics.*
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
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
    private var motionTouchEventX = 0f
    private var motionTouchEventY = 0f
    // they are the startinng point for the next path after user lifts finger
    private var currentX = 0f
    private var currentY = 0f

    // performance tweak to reduce draw: if path moved less then this tolerance -> don't draw
    // this returns distance in Pixels
    private val touchTolerance = ViewConfiguration.get(context).scaledTouchSlop
    // Rectangle to frame the drawn picture
    private lateinit var frame:Rect

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

        //calculate the frame around the new pic
        val inset = 40
        frame = Rect(inset,inset, width - inset, height - inset)
    }

    // note: this is a  different canvas then extraCanvas
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // draw the background from extraBitmap, offset 0 left, 0 top and set the paint when needed
        canvas.drawBitmap(extraBitmap, 0f,0f, null)
        // draw the frame
        canvas.drawRect(frame, paint)
    }

    // needs to implement DOWN, MOVE and UP
    override fun onTouchEvent(event: MotionEvent): Boolean {
        motionTouchEventX = event.x
        motionTouchEventY = event.y

        when(event.action){
            MotionEvent.ACTION_DOWN ->touchStart()
            MotionEvent.ACTION_MOVE ->touchMove()
            MotionEvent.ACTION_UP ->touchUp()
        }
        return true
    }

    private fun touchStart(){
        path.reset()
        path.moveTo(motionTouchEventX, motionTouchEventY)
        currentX = motionTouchEventX
        currentY = motionTouchEventY
    }

    private fun touchMove(){
        // calculate the traveled distance
        val dx = Math.abs(motionTouchEventX - currentX)
        val dy = Math.abs(motionTouchEventY - currentY)

        if(dx >= touchTolerance || dy >= touchTolerance){
            // create curve between the tow points and store it in path = adds segment to the path
            // QuadTo() adds a quadratic bezier from the last point,
            // approaching control point (x1,y1), and ending at (x2,y2).
            path.quadTo(currentX, currentY, (motionTouchEventX + currentX) / 2, (motionTouchEventY + currentY) / 2)
            currentX = motionTouchEventX
            currentY = motionTouchEventY
            // draw the path in extra bitmap to cache it
            extraCanvas.drawPath(path, paint)
        }
        // force re-draw of screen
        invalidate()
    }

    private fun touchUp(){
        // Reset the path so it doesn't get drawn again.
        path.reset()
    }
}