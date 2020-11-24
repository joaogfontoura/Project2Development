package com.example.project2development;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

public class MainPage extends AppCompatActivity {
    private TextView username;
    private Button Btn;
    private Button BtnLocation;
    private FirebaseAuth mAuth;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        // taking FirebaseAuth instance
        mAuth = FirebaseAuth.getInstance();

        username = findViewById(R.id.userEmail);
        Btn = findViewById(R.id.btnlogout);
        BtnLocation = findViewById(R.id.btnLocation);
        progressBar = findViewById(R.id.progressBar);

        // get current user information
        FirebaseUser currentUser = mAuth.getCurrentUser();

        // set user email
        username.setText(currentUser.getEmail());

        Btn.setOnClickListener(new LogoutBtnListener());
        BtnLocation.setOnClickListener(new LocationBtnListener());
    }

    class LogoutBtnListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }
    }
    class LocationBtnListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(getApplicationContext(), AttractionMenu.class));
            finish();
        }
    }
}