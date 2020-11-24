package com.example.project2development;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Button;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public Button BtnLogin;
    public Button BtnRegister;
    private LoginButton BtnFaceLogin;
    private FirebaseAuth mAuth;

    private FirebaseAuth.AuthStateListener authStateListener;
    private CallbackManager mCallbackManager;

    private static final int RC_SIGN_IN = 123;
    private AccessTokenTracker accessTokenTracker;
    private static final String TAG = "FacebookAuthentication";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // taking FirebaseAuth instance
        mAuth = FirebaseAuth.getInstance();

        //facebook sdk
        FacebookSdk.sdkInitialize(getApplicationContext());

        // Set on Click Listener on Registration button
        BtnLogin = (Button) findViewById(R.id.btnlogin);
        BtnRegister = (Button) findViewById(R.id.btnregister);
        BtnFaceLogin = (LoginButton) findViewById(R.id.btFacebook);

        BtnFaceLogin.setReadPermissions("email","public_profile");
        mCallbackManager = CallbackManager.Factory.create();
        BtnFaceLogin.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG,"onSuccess"+ loginResult);
                handleFacebookToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG,"onCancel");

            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG,"onError"+ error);
            }
        });

        // check if user is already logged in
        if(mAuth.getCurrentUser() != null){
            // if the user created intent to login activity
            startActivity(new Intent(getApplicationContext(), MainPage.class));
            finish();
        }

        // Set on Click Listener on Registration button
        BtnLogin.setOnClickListener(new callLogin());
        BtnRegister.setOnClickListener(new callRegistration());

    }

    class callLogin implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }
    }
    class callRegistration implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            startActivity(new Intent(MainActivity.this, RegistrationActivity.class));
            finish();
        }
    }

    private void handleFacebookToken(AccessToken token) {
        Log.d(TAG,"handleFacebookToken" + token);
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Log.d(TAG, "Sign in Successful");
                    FirebaseUser user = mAuth.getCurrentUser();
                    startActivity(new Intent(getApplicationContext(), MainPage.class));
                }else{
                    Log.d(TAG, "Sign in fail", task.getException());
                    Toast.makeText(MainActivity.this,"Authentication Failed", Toast.LENGTH_SHORT).show();
                    // add here in case of fail
                }
            }
        });

    }



}