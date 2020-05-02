package com.example.mlproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button textDetection, objectDetection, labelDetection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textDetection = findViewById(R.id.text_detection);
        objectDetection = findViewById(R.id.object_detection);
        labelDetection = findViewById(R.id.label_detection);

        textDetection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Text.class);
                startActivity(intent);

            }
        });

        objectDetection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Object.class);
                startActivity(intent);

            }
        });

        labelDetection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Label.class);
                startActivity(intent);

            }
        });




    }
}
