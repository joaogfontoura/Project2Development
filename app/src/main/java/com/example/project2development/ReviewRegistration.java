package com.example.project2development;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class ReviewRegistration extends AppCompatActivity {

    private TextView TextViewReview,TextViewName;
    private Button BtnAddReview;
    private RatingBar ratingBar;
    String msg = null;
    FirebaseUser userID;
    String email;
    FirebaseFirestore db = FirebaseFirestore.getInstance();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_registration);

        TextViewReview = findViewById(R.id.textviewReview);
        BtnAddReview = findViewById(R.id.btnAddReview);
        ratingBar = findViewById(R.id.ratingBar);
        TextViewName = findViewById(R.id.textViewName);

        final String id = getIntent().getStringExtra("Id");

        ratingBar.setNumStars(5);
        ratingBar.setRating((float) 2.5);

        userID = FirebaseAuth.getInstance().getCurrentUser();


        if (userID != null) {
            email = userID.getEmail();

        }
        DocumentReference df = db.collection("Attraction Collection").document(id);
        df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                TextViewName.setText(documentSnapshot.getString("Name"));

            }
        });


        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float f, boolean fromUser) {

                int i = (int) f;

                switch (i){

                    case 1:
                        msg = " Not Recommended";
                        break;
                    case 2:
                        msg = " Could be better";
                        break;
                    case 3:
                        msg = " Neutral";
                        break;
                    case 4:
                        msg = " Good!";
                        break;
                    case 5:
                        msg = " Excellent";
                        break;

                }

            }
        });
        BtnAddReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                uploadReview(id);
            }

            });

    }



    private void uploadReview(String id){
        // HERE WE ADD THE ATTRACTION TO FIRESTORE
        String description = TextViewReview.getText().toString();
        float ratingFloat = ratingBar.getRating();
        String ratingString = Float.toString(ratingFloat);


        CollectionReference cref = db.collection("Review");


        Map<String,Object> data = new HashMap<>();
        data.put("title",msg);
        data.put("description",description);
        data.put("rating",ratingString);
        data.put("user_email",email);
        data.put("attraction_id",id);
        cref.add(data);

        Intent attractionDetail = new Intent(getApplicationContext(), AttractionDetails.class);
        attractionDetail.putExtra("Id",id);
        startActivity(attractionDetail);
        finish();


    }




}


