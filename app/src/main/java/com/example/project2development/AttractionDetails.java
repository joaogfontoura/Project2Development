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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DecimalFormat;
import java.util.ArrayList;


public class AttractionDetails extends AppCompatActivity {

    ImageView AttImg, AttImgRating;
    TextView AttTitle, AttRating;
    TextView AttDescription;
    private Button BtnAddReview, BtnRemoveAtt;


    public static String title = "";
    public static String description = "";
    public static String image = "";

    CustomAdapter customAdapter;
    RecyclerView recyclerView;
    ArrayList<String> arr_title, arr_description, arr_attraction_id, arr_username,arr_rating;

    // usado pra fazer log.d e debugar as variaveis
    private static final String TAG = "AttractionDetails";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attraction_details);

        AttImg = findViewById(R.id.attImg);
        AttTitle = findViewById(R.id.txtTitle);
        AttDescription = findViewById(R.id.txtDescription);
        AttRating = findViewById(R.id.textViewRating);
        AttImgRating = findViewById(R.id.imageViewRating);

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

        BtnRemoveAtt = findViewById(R.id.btnRemoveAtt);

        BtnRemoveAtt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeAtt(id);
            }
        });

        checkRole();

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

        CollectionReference mColRef = db.collection("Review");

        mColRef.whereEqualTo("attraction_id",id).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    Double sum_rating = 0.0;
                    Double avg_rating = 0.0;
                    int counter = 0;
                    for(QueryDocumentSnapshot document : task.getResult()){
                        sum_rating = sum_rating + Double.parseDouble(document.getString("rating"));
                        counter = counter + 1;
                    }
                    if(sum_rating != 0){
                        avg_rating = sum_rating / counter;
                        DecimalFormat df = new DecimalFormat("0.00");
                        AttRating.setText(df.format(avg_rating).toString());
                    }else{
                        AttRating.setVisibility(View.INVISIBLE);
                        AttImgRating.setVisibility(View.INVISIBLE);
                    }

                }else{
                    Log.d(TAG, "Error getting the reviews for this attraction", task.getException());
                }
            }
        });


    }

    private void displayDataRecyclerView(String id){

        arr_title = new ArrayList<>();
        arr_description = new ArrayList<>();
        arr_attraction_id = new ArrayList<>();
        arr_rating = new ArrayList<>();
        arr_username = new ArrayList<>();

        db.collection("Review").whereEqualTo("attraction_id", id).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        arr_title.add(document.getString("title"));
                        arr_description.add(document.getString("description"));
                        arr_attraction_id.add(document.getString("attraction_id"));
                        arr_rating.add(document.getString("rating"));
                        arr_username.add(document.getString("username"));
                    }


                    if(arr_title.isEmpty() == false){
                        customAdapter = new CustomAdapter(getApplicationContext(), arr_title, arr_description, arr_rating, arr_username);
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
                                    BtnRemoveAtt.setVisibility(View.VISIBLE);
                                }
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

    private void removeAtt(String id){
        // HERE ALL THE REVIEWS RELATED TO THIS ATTRACTION WILL BE DELETED

        db.collection("Review")
                .whereEqualTo("attraction_id",id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            db.collection("Review").document(document.getId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "Review Successfully Deleted!");

                                }
                            });
                        }
                    }
                });


        // HERE THE ATTRACTION WILL BE DELETED
        db.collection("Attraction Collection").document(id).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "Attraction Successfully Deleted!");
            }
        });

        Toast.makeText(getApplicationContext(),"Attraction Successfully Deleted",Toast.LENGTH_LONG).show();
        startActivity(new Intent(getApplicationContext(), AttractionMenu.class));
        finish();


    }

}