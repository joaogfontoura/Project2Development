package com.example.project2development;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class AttractionPage extends AppCompatActivity {

    private Button BtnAddAttraction;
    RecyclerView recyclerView;
    ArrayList<String> arr_title, arr_description, arr_attraction_id, arr_user_email;
    ArrayList<Long> arr_rating;
    CustomAdapter customAdapter;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private static final String TAG = "AttractionDetails";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attraction_page);

        BtnAddAttraction = findViewById(R.id.btnAddAttraction);

        BtnAddAttraction.setOnClickListener(new AttractionPage.AddAttractionsBtnListener());
        recyclerView = findViewById(R.id.rvAttraction);
        recyclerView.setNestedScrollingEnabled(false);


        String id = getIntent().getStringExtra("Id");
        displayDataRecyclerView(id);


    }

    class AddAttractionsBtnListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(getApplicationContext(), AttractionRegistration.class));
            finish();
        }
    }
    private void displayDataRecyclerView(String id){

        arr_title = new ArrayList<>();
        arr_description = new ArrayList<>();
        arr_attraction_id = new ArrayList<>();
        arr_rating = new ArrayList<>();
        arr_user_email = new ArrayList<>();

        db.collection("Attraction Collection").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        arr_title.add(document.getString("Name"));
                        arr_description.add(document.getString("description"));
                        arr_attraction_id.add(document.getString("attraction_id"));
                        arr_rating.add(document.getLong("rating"));
                        arr_user_email.add(document.getString("user_email"));

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



}