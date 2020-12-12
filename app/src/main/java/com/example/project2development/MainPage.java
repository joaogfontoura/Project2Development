package com.example.project2development;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class MainPage extends AppCompatActivity {
    private TextView username;
    private Button Btn, BtnLocation, BtnAttractions, BtnListUsers;
    private FirebaseAuth mAuth;
    ProgressBar progressBar;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    // usado pra fazer log.d e debugar as variaveis
    private static final String TAG = "MainPage";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        // taking FirebaseAuth instance
        mAuth = FirebaseAuth.getInstance();

        username = findViewById(R.id.userEmail);
        Btn = findViewById(R.id.btnlogout);
        BtnLocation = findViewById(R.id.btnLocation);
        BtnAttractions = findViewById(R.id.btnAttractions);
        BtnListUsers = findViewById(R.id.btnListUsers);

        progressBar = findViewById(R.id.progressBar);


        // get current user information
        FirebaseUser currentUser = mAuth.getCurrentUser();

        // set user email
        username.setText(currentUser.getEmail());

        Btn.setOnClickListener(new LogoutBtnListener());
        BtnLocation.setOnClickListener(new LocationBtnListener());
        BtnAttractions.setOnClickListener(new AttractionsBtnListener());
        BtnListUsers.setOnClickListener(new ListUsersBtnListener());

        checkRole();

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

        }
    }
    class AttractionsBtnListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(getApplicationContext(), AttractionPage.class));

        }
    }
    class ListUsersBtnListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            startActivity(new Intent(getApplicationContext(), ListUsers.class));
        }
    }

    private void checkRole(){

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        String uid = user.getUid();

        // CHECK IF THE USER HAS RIGHTS OF ADMIN IN ORDER TO DISPLAY BUTTONS
        db.collection("User")
                .whereEqualTo("Uid", uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if(document.getString("Role").equals("admin")){
                                    BtnListUsers.setVisibility(View.VISIBLE);
                                }
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

}