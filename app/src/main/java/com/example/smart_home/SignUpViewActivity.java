package com.example.smart_home;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import okhttp3.OkHttpClient;

public class SignUpViewActivity extends AppCompatActivity {

    private TextView mTextViewResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_view);
        configureSignUpButton();

        mTextViewResult = findViewById(R.id.jsonReturn);

    }


    private void sigup_request(){
        OkHttpClient client = new OkHttpClient();
        String url = "192.168.0.203:8080/";

    }


    private void configureSignUpButton(){
        Button singUpViewButton =(Button) findViewById(R.id.button_login_screen);
        singUpViewButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public  void onClick(View view){
                finish();
            }
        });
    }
}