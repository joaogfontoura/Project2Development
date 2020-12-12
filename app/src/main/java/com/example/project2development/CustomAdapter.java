package com.example.project2development;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {

    Context context;
    private ArrayList title, description, rating, username;
    private Button btnRemove;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;

    // usado pra fazer log.d e debugar as variaveis
    private static final String TAG = "CustomAdapter";

    CustomAdapter(Context context, ArrayList title, ArrayList description, ArrayList rating, ArrayList username){
        this.context = context;
        this.title = title;
        this.description = description;
        this.rating = rating;
        this.username = username;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recyclerview_reviews, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        holder.username.setText(String.valueOf(username.get(position)));
        holder.title.setText(String.valueOf(title.get(position)));
        holder.description.setText(String.valueOf(description.get(position)));
        holder.rating.setText(String.valueOf(rating.get(position)));

        holder.btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String dUser_email = String.valueOf(username.get(position));
                String dDescription = String.valueOf(description.get(position));
                String dRating = String.valueOf(rating.get(position));

                db.collection("Review")
                        .whereEqualTo("username",dUser_email)
                        .whereEqualTo("description",dDescription)
                        .whereEqualTo("rating",dRating)

                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String documentid = document.getId();

                                final String attId = document.getString("attraction_id");

                                db.collection("Review").document(documentid).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "Review Successfully Deleted!");
                                        Toast.makeText(context.getApplicationContext(), "Review Removed Successfully!", Toast.LENGTH_LONG).show();
                                        Intent attractionDetail = new Intent(context.getApplicationContext(), AttractionDetails.class);
                                        attractionDetail.putExtra("Id",attId);
                                        context.startActivity(attractionDetail);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d(TAG, "Error Deleting Review!");
                                    }
                                });

                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });



            }
        });
    }

    @Override
    public int getItemCount() {
        return username.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView title, description, rating, username;
        Button btnRemove, btnRemoveAtt;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.textViewUser);
            title = itemView.findViewById(R.id.textViewTitle);
            description = itemView.findViewById(R.id.textViewDescription);
            rating = itemView.findViewById(R.id.textViewRating);
            btnRemove = itemView.findViewById(R.id.btnRemoveReview);

            // BLOCK TO ID THE USER
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
                                        btnRemove.setVisibility(View.VISIBLE);

                                    }
                                }
                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });


        }
    }

}
