package com.example.project2development;

import android.app.Activity;
import android.content.Context;

import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class CustomAdapterListUser extends RecyclerView.Adapter<CustomAdapterListUser.MyViewHolder> {

    Context context;
    private ArrayList username, email, status, role, id;


    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;


    // usado pra fazer log.d e debugar as variaveis
    private static final String TAG = "CustomAdapterListUser";

    CustomAdapterListUser(Context context, ArrayList username, ArrayList email, ArrayList status, ArrayList role, ArrayList id){
        this.context = context;
        this.username = username;
        this.email = email;
        this.status = status;
        this.role = role;
        this.id = id;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recyclerview_user_list, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {

        holder.username.setText(String.valueOf(username.get(position)));
        holder.email.setText(String.valueOf(email.get(position)));
        holder.role.setText(String.valueOf(role.get(position)));

        final String action;
        if(status.get(position).equals("active")){
            holder.btnStatus.setText("Block User");
            holder.btnStatus.setBackground(context.getResources().getDrawable(R.drawable.buttonred));
            action = "blocked";
        }else{
            holder.btnStatus.setText("UnBlock User");
            holder.btnStatus.setBackground(context.getResources().getDrawable(R.drawable.buttongreen));
            action = "active";
        }

        Log.d(TAG, "ID: "+id.get(position));

        // HOLDER CLICK LISTENER DO RECYCLER VIEW LIST
        holder.btnStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // RATING FOR THE ATTRACTION
                DocumentReference mDocRef = db.collection("User").document((String) id.get(position));

                mDocRef.update("Status", action)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Intent intent = new Intent(context, ListUsers.class);
                                context.startActivity(intent);

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error updating document", e);
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


        TextView username, email, role;
        Button btnStatus;



        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.txtViewUsername);
            email = itemView.findViewById(R.id.txtViewEmail);
            role = itemView.findViewById(R.id.txtViewRole);
            btnStatus = itemView.findViewById(R.id.BtnAction);

        }
    }

}

