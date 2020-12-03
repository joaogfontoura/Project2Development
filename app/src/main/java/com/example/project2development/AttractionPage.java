package com.example.project2development;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class AttractionPage extends AppCompatActivity {

    private Button BtnAddAttraction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attraction_page);

        BtnAddAttraction = findViewById(R.id.btnAddAttraction);

        BtnAddAttraction.setOnClickListener(new AttractionPage.AddAttractionsBtnListener());
    }

    class AddAttractionsBtnListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(getApplicationContext(), AttractionRegistration.class));
            finish();
        }
    }
}