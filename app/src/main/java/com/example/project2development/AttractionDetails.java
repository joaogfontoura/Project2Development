package com.example.project2development;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class AttractionDetails extends AppCompatActivity {

    ImageView AttImg;
    TextView AttTitle;
    TextView AttDescription;
    private Button BtnAddReview;


    public static String title = "";
    public static String description = "";
    public static String image = "";

    CustomAdapter customAdapter;
    RecyclerView recyclerView;
    ArrayList<String> arr_title, arr_description, arr_attraction_id, arr_user_email,arr_rating;


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


        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setNestedScrollingEnabled(false);

        final String id = getIntent().getStringExtra("Id");

        getDataFromFirebase(id);

        displayDataRecyclerView(id);

        BtnAddReview = findViewById(R.id.btnAddReview);

        BtnAddReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addreview(id);
            }
        });


    }

    private void getDataFromFirebase(String id) {

        // retrieve the data from firestore related to this collection
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

    private void displayDataRecyclerView(String id){

        arr_title = new ArrayList<>();
        arr_description = new ArrayList<>();
        arr_attraction_id = new ArrayList<>();
        arr_rating = new ArrayList<>();
        arr_user_email = new ArrayList<>();

        db.collection("Review").whereEqualTo("attraction_id", id).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        arr_title.add(document.getString("title"));
                        arr_description.add(document.getString("description"));
                        arr_attraction_id.add(document.getString("attraction_id"));
                        arr_rating.add(document.getString("rating"));
                        arr_user_email.add(document.getString("user_email"));
                        Log.d(TAG, "aquuiiiiii: "+document.getString("user_email"));

                    }


                    if(arr_title.isEmpty() == false){
                        customAdapter = new CustomAdapter(getApplicationContext(), arr_title, arr_description, arr_rating, arr_user_email);
                        recyclerView.setAdapter(customAdapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    }
                } else {
                    Log.d(TAG, "Error getting the reviews", task.getException());
                }
            }
        });



    }

    private void addreview(String id){

        Intent reviewReg = new Intent(getApplicationContext(), ReviewRegistration.class);

        reviewReg.putExtra("Id",id);
        startActivity(reviewReg);

    }

}