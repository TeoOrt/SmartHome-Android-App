package com.example.smart_home

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout
import java.util.regex.Pattern

class SignUpViewActivity : AppCompatActivity() {
    private lateinit var emailTextInput: TextInputLayout
    private lateinit var passwordTextInput: TextInputLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sign_up_view)

        emailTextInput =  findViewById<TextInputLayout>(R.id.editEmail) as TextInputLayout
        passwordTextInput = findViewById<TextInputLayout>(R.id.editPassword) as TextInputLayout
        configureSignUpButton()
    }

//        OkHttpClient client = new OkHttpClient();
//        String url = "http://192.168.0.203:8080/get_expert/video/H-LightOn.mp4";






    private fun configureSignUpButton() {
        val singUpViewButton = findViewById<View>(R.id.button_login_screen) as Button

        singUpViewButton.setOnClickListener { finish() } // goes back to main screen

        val signUpAccountButton = findViewById<View>(R.id.button_submit_login) as Button

        signUpAccountButton.setOnClickListener {
            val serviceIntent: Intent = Intent(this@SignUpViewActivity,SignUpService::class.java)
//            val email: String = emailTextInput.editText?.text.toString()
//            val password  = passwordTextInput.editText
//                ?.text
//                .toString()
//            serviceIntent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
//            serviceIntent.putExtra("email",email)
//            serviceIntent.putExtra("password",password)
            startService(serviceIntent)
        }
    }

}