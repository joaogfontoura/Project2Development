package com.example.project2development;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class ReviewRegistration extends AppCompatActivity {

    private TextView TextViewReview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_registration);

        TextViewReview = findViewById(R.id.textviewReview);
    }
}