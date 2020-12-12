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
import android.widget.EditText;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;


public class AttractionPage extends AppCompatActivity {

    private Button BtnAddAttraction, BtnSearchAttraction;
    private EditText searchFilter;
    RecyclerView recyclerView;
    ArrayList<String> arr_title, arr_description,arr_image,arr_att_id;
    CustomAdapterAttList customAdapterAttList;
    private String m_Text = "";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private static final String TAG = "AttractionPage";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attraction_page);

        BtnAddAttraction = findViewById(R.id.btnAddAttraction);
        BtnSearchAttraction = findViewById(R.id.searchButton);
        searchFilter = findViewById(R.id.searchAttractionFilter);

        BtnAddAttraction.setOnClickListener(new AttractionPage.AddAttractionsBtnListener());
        BtnSearchAttraction.setOnClickListener(new AttractionPage.SearchAttractionListener());


        recyclerView = findViewById(R.id.rvAttraction);
        recyclerView.setNestedScrollingEnabled(false);



        String id = getIntent().getStringExtra("Id");
        displayDataRecyclerView(id);


    }

    class SearchAttractionListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            recyclerView.setAdapter(null);
            String filter = searchFilter.getText().toString();

            arr_title = new ArrayList<>();
            arr_description = new ArrayList<>();
            arr_image = new ArrayList<>();
            arr_att_id = new ArrayList<>();

            if(filter != null && !filter.trim().isEmpty()){
                db.collection("Attraction Collection").whereEqualTo("Name",filter)
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                arr_title.add(document.getString("Name"));
                                arr_description.add(document.getString("Description"));
                                arr_image.add(document.getString("Image"));
                                arr_att_id.add(document.getId());

                            }


                            if(arr_title.isEmpty() == false){
                                customAdapterAttList = new CustomAdapterAttList(getApplicationContext(), arr_title, arr_description, arr_image, arr_att_id);
                                recyclerView.setAdapter(customAdapterAttList);
                                recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                            }
                        } else {
                            Log.d(TAG, "Error getting the attractions", task.getException());
                        }
                    }
                });
            }else{
                db.collection("Attraction Collection").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                arr_title.add(document.getString("Name"));
                                arr_description.add(document.getString("Description"));
                                arr_image.add(document.getString("Image"));
                                arr_att_id.add(document.getId());

                            }


                            if(arr_title.isEmpty() == false){
                                customAdapterAttList = new CustomAdapterAttList(getApplicationContext(), arr_title, arr_description, arr_image, arr_att_id);
                                recyclerView.setAdapter(customAdapterAttList);
                                recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                            }
                        } else {
                            Log.d(TAG, "Error getting the attractions", task.getException());
                        }
                    }
                });
            }

        }
    }


    class AddAttractionsBtnListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(getApplicationContext(), AttractionRegistration.class));
        }
    }



    private void displayDataRecyclerView(String id){

        arr_title = new ArrayList<>();
        arr_description = new ArrayList<>();
        arr_image = new ArrayList<>();
        arr_att_id = new ArrayList<>();

        db.collection("Attraction Collection").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        arr_title.add(document.getString("Name"));
                        arr_description.add(document.getString("Description"));
                        arr_image.add(document.getString("Image"));
                        arr_att_id.add(document.getId());

                    }


                    if(arr_title.isEmpty() == false){
                        customAdapterAttList = new CustomAdapterAttList(getApplicationContext(), arr_title, arr_description, arr_image, arr_att_id);
                        recyclerView.setAdapter(customAdapterAttList);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    }
                } else {
                    Log.d(TAG, "Error getting the attractions", task.getException());
                }
            }
        });




    }



}