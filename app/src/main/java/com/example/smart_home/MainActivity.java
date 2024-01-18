package com.example.smart_home;

import androidx.appcompat.app.AppCompatActivity;

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
        Button myButton = findViewById(R.id.myButton);
        myButton.setText("Click me to Add 1");
        TextView myText = findViewById(R.id.myCounter);
        final int[] counter = {0};
        myButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                counter[0]++;
                String buffer = String.format("We are at %d",counter[0]);
                myText.setText(buffer);

            }
        });

    }

}