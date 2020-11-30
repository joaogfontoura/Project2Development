package com.example.project2development;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class AttractionDetails extends AppCompatActivity {

    ImageView AttImg;
    TextView AttTitle;
    TextView AttDescription;


    public static String title = "";
    public static String description = "";
    public static String image = "";


    // usado pra fazer log.d e debugar as variaveis
    private static final String TAG = "AttractionDetails";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attraction_details);

        AttImg = findViewById(R.id.attImg);
        AttTitle = findViewById(R.id.txtTitle);
        AttDescription = findViewById(R.id.txtDescription);


        String id = getIntent().getStringExtra("Id");

        getDataFromFirebase(id);


    }

    private void getDataFromFirebase(String id) {

        DocumentReference mDocRef = db.collection("Attraction Collection").document(id);

        mDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                AttTitle.setText(documentSnapshot.getString("Name"));
                AttDescription.setText(documentSnapshot.getString("Description"));

                Glide.with(getApplicationContext()).load(documentSnapshot.getString("Image")).into(AttImg);
            }
        });

    }
}