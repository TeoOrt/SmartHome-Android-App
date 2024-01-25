package com.example.smart_home

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        configureSignUpButton()
    }

    private fun configureSignUpButton() {
        val singUpViewButton = findViewById<View>(R.id.button_register) as Button
        singUpViewButton.setOnClickListener {
            val intent = Intent(/* packageContext = */ this@MainActivity, /* cls = */ SignUpViewActivity::class.java)
            startActivity(intent)
        }
    }
}