package com.example.project2development;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ListUsers extends AppCompatActivity {


    RecyclerView recyclerView;
    ArrayList<String> arr_username, arr_email,arr_status,arr_role, arr_id;
    CustomAdapterListUser customAdapterUserList;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private static final String TAG = "ListUsers";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_users);

        Button btn1 = (Button) findViewById(R.id.Btnback);

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainPage.class);
                startActivity(intent);
            }
        });


        recyclerView = findViewById(R.id.recyclerViewListUsers);
        recyclerView.setNestedScrollingEnabled(false);




        displayDataRecyclerView();

    }


    private void displayDataRecyclerView(){

        arr_username = new ArrayList<>();
        arr_email = new ArrayList<>();
        arr_status = new ArrayList<>();
        arr_role = new ArrayList<>();
        arr_id = new ArrayList<>();

        db.collection("User").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        arr_username.add(document.getString("Username"));
                        arr_email.add(document.getString("Email"));
                        arr_status.add(document.getString("Status"));
                        arr_role.add(document.getString("Role"));
                        arr_id.add(document.getId());

                    }


                    if(arr_username.isEmpty() == false){
                        customAdapterUserList = new CustomAdapterListUser(getApplicationContext(), arr_username, arr_email, arr_status, arr_role, arr_id);
                        recyclerView.setAdapter(customAdapterUserList);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    }
                } else {
                    Log.d(TAG, "Error getting the users", task.getException());
                }
            }
        });




    }


}