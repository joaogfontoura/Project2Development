package com.example.project2development;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class LoginActivity extends AppCompatActivity {

    private EditText emailTextView, passwordTextView;
    private Button Btn;
    private TextView warningTextView;
    ProgressBar progressBar;

    private static final String TAG = "LoginActivity";

    private FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // taking instance of FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        // initialising all views through id defined above
        emailTextView = findViewById(R.id.email);
        passwordTextView = findViewById(R.id.password);
        Btn = findViewById(R.id.login);
        progressBar = findViewById(R.id.progressBar);
        warningTextView = findViewById(R.id.textViewWarning);

        Btn.setOnClickListener(new LoginBtnListener());

        // check if user is already logged in
        if(mAuth.getCurrentUser() != null){
            // if the user created intent to login activity
            startActivity(new Intent(getApplicationContext(), MainPage.class));
            finish();
        }

    }

    class LoginBtnListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            final String email= emailTextView.getText().toString().trim();
            final String password = passwordTextView.getText().toString().trim();

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

            CollectionReference mColRef = db.collection("User");

            mColRef.whereEqualTo("Email",email).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    Log.d(TAG, "ONCOMPLETE");
                    if(task.isSuccessful()){
                        Log.d(TAG, "IS SUCCESSFUL");
                        for(QueryDocumentSnapshot document : task.getResult()){
                            Log.d(TAG, "ENTROU FOR");
                            if(document.getString("Status").equals("blocked")){
                                Log.d(TAG, "ENTROU IF: ");
                                warningTextView.setVisibility(View.VISIBLE);
                            }else{
                                progressBar.setVisibility(View.VISIBLE);

                                // signin existing user
                                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task)
                                    {
                                        progressBar.setVisibility(View.INVISIBLE);
                                        if (task.isSuccessful()) {
                                            Toast.makeText(getApplicationContext(), "Login successful!!", Toast.LENGTH_LONG).show();

                                            startActivity(new Intent(getApplicationContext(), MainPage.class));
                                            finish();
                                        }
                                        else {
                                            // sign-in failed
                                            Toast.makeText(getApplicationContext(), "Login failed!!", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            }
                            return;
                        }
                    }
                }
            });


        }
    }

}
