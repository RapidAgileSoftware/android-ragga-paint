package com.example.raggapaint

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View
import androidx.core.content.res.ResourcesCompat

class MyCanvasView(context: Context): View(context) {

    //for caching what has been drawn before
    private lateinit var extraCanvas:Canvas
    private lateinit var extraBitmap: Bitmap

    private val backgroundColor = ResourcesCompat.getColor(resources, R.color.colorBackground, null)

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
}