package com.example.smart_home

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.text.TextPaint
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager

/**
 * TODO: document your custom view class.
 */
class ListItem @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : CardView(context, attrs, defStyleAttr) {

    private val detailText: TextView
    private val layout: LinearLayout
    private val expandButton: Button
    private val recordVideo : Button

    var titleText : CharSequence
        get() = expandButton.text
        set(value) {
            this.expandButton.text=value
        }


    var inputText : CharSequence
        get() = detailText.text
        set(value) {
            this.detailText.text=value
        }

    init {
        LayoutInflater.from(context).inflate(R.layout.sample_list_item, this, true)

        detailText = findViewById(R.id.details)
        layout = findViewById(R.id.init_layout)
        expandButton = findViewById(R.id.open_list)
        recordVideo = findViewById(R.id.recordVideo)

        expandButton.setOnClickListener {
            expand()
        }
    }

    private fun expand() {
        val textState = detailText.visibility
        val buttonState = recordVideo.visibility
        TransitionManager.beginDelayedTransition(layout, AutoTransition())

        detailText.visibility = toggleVisibility(textState)
        recordVideo.visibility = toggleVisibility(buttonState)

    }

    private fun toggleVisibility(state:Int): Int {
        return if(state == VISIBLE) GONE else VISIBLE
    }
}