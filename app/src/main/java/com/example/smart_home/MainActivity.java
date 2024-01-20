package com.example.smart_home;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        configureSignUpButton();
    }


    private void configureSignUpButton(){
        Button singUpViewButton =(Button) findViewById(R.id.button_register);
        singUpViewButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public  void onClick(View view){
                Intent intent = new Intent(MainActivity.this, SignUpViewActivity.class);
                startActivity(intent);
            }
        });
    }





}