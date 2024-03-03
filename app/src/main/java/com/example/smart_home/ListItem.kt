package com.example.smart_home

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
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
    private val trialNumber : TextView
    private val layout: LinearLayout
    private val expandButton: Button
    private val recordVideo : Button
    private lateinit var nextActivity: Activity

    var titleText : CharSequence
        get() = expandButton.text
        set(value) {
            this.expandButton.text=value
        }
    var recorded : CharSequence
        get() = trialNumber.text
        set(value){ // must be a number
            val temp = "Recorded Times :${value}"
            this.trialNumber.text = temp
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
        trialNumber = findViewById(R.id.CounterId)

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

    fun getNextActivityButton():Button{
        return this.recordVideo
    }
    private fun toggleVisibility(state:Int): Int {
        return if(state == VISIBLE) GONE else VISIBLE
    }
}