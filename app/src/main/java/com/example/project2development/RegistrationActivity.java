package com.example.project2development;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;

import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.AuthResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegistrationActivity extends AppCompatActivity {

    private EditText emailTextView, passwordTextView;
    private Button Btn;
    ProgressBar progressBar;

    private FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        // taking FirebaseAuth instance
        mAuth = FirebaseAuth.getInstance();

        // initialising all views through id defined above
        emailTextView = findViewById(R.id.email);
        passwordTextView = findViewById(R.id.passwd);
        Btn = findViewById(R.id.btnregister);
        progressBar = findViewById(R.id.progressBar);

        Btn.setOnClickListener(new RegistrationBtnListener());

        // check if user is already logged in
        if(mAuth.getCurrentUser() != null){
            // if the user created intent to login activity
            startActivity(new Intent(getApplicationContext(), MainPage.class));
            finish();
        }

    }



    class RegistrationBtnListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            String email= emailTextView.getText().toString().trim();
            String password = passwordTextView.getText().toString().trim();

            // Validations for input email and password
            if (TextUtils.isEmpty(email)) {
                emailTextView.setError("Email is Required.");
                return;
            }
            if (TextUtils.isEmpty(password)) {
                passwordTextView.setError("Password is Required.");
                return;
            }

            if (password.length() < 6) {
                passwordTextView.setError("Password must be >= 6 characters.");
                return;
            }

            progressBar.setVisibility(View.VISIBLE);


            // FIREBASE
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task)
                {
                    if (task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "Registration successful!", Toast.LENGTH_LONG).show();

                        // CREATE THE USER INFORMATION ON FIRESTORE
                        FirebaseUser user = mAuth.getCurrentUser();
                        String uid = user.getUid();

                        CollectionReference cref = db.collection("User");

                        Map<String,Object> data = new HashMap<>();
                        data.put("Uid",uid);
                        data.put("Role","user");

                        cref.add(data);

                        // if the user created intent to login activity
                        startActivity(new Intent(getApplicationContext(), MainPage.class));
                        finish();
                    }
                    else {

                        // Registration failed
                        Toast.makeText(getApplicationContext(), "Registration failed!!" + " Please try again later", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }


}



