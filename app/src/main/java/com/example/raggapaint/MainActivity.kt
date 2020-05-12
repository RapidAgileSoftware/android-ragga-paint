package com.example.raggapaint

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View.SYSTEM_UI_FLAG_FULLSCREEN

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val myCanvasView = MyCanvasView(this)
        // request full screen
        myCanvasView.systemUiVisibility = SYSTEM_UI_FLAG_FULLSCREEN
        // set description
        myCanvasView.contentDescription =  getString(R.string.canvasContentDescription)
        setContentView(myCanvasView)
    }
}
