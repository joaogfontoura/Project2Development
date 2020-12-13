package com.example.project2development;

import android.content.Context;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class CustomAdapterAttList extends RecyclerView.Adapter<CustomAdapterAttList.MyViewHolder> {

    Context context;
    private ArrayList title, description, img, att_id;
    private Double avg_rating = 0.0;


    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;


    // usado pra fazer log.d e debugar as variaveis
    private static final String TAG = "CustomAdapterAttList";

    CustomAdapterAttList(Context context, ArrayList title, ArrayList description, ArrayList img, ArrayList att_id){
        this.context = context;
        this.title = title;
        this.description = description;
        this.img = img;
        this.att_id = att_id;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recyclerview_att_list, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {

        holder.title.setText(String.valueOf(title.get(position)));
        holder.description.setText(String.valueOf(description.get(position)));

        // HOLDER DA IMAGE
        Glide.with(context.getApplicationContext()).load(img.get(position)).into(holder.img);

        // RATING FOR THE ATTRACTION
        CollectionReference mColRef = db.collection("Review");

        mColRef.whereEqualTo("attraction_id",att_id.get(position)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    Double sum_rating = 0.0;

                    int counter = 0;
                    for(QueryDocumentSnapshot document : task.getResult()){
                        sum_rating = sum_rating + Double.parseDouble(document.getString("rating"));
                        counter = counter + 1;

                    }
                    if(sum_rating != 0){
                        avg_rating = sum_rating / counter;
                        DecimalFormat df = new DecimalFormat("0.00");
                        holder.rating.setText(df.format(avg_rating).toString());
                    }else{
                        holder.rating.setVisibility(View.INVISIBLE);
                        holder.imgRating.setVisibility(View.INVISIBLE);
                    }

                }else{
                    Log.d(TAG, "Error getting the reviews for this attraction", task.getException());
                }
            }
        });

        // HOLDER CLICK LISTENER DO RECYCLER VIEW LIST
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String attId = String.valueOf(att_id.get(position));
                Intent intent = new Intent(context, AttractionDetails.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("Id", attId);
                context.startActivity(intent);
            }
        });

    }



    @Override
    public int getItemCount() {
        return title.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView title, description, rating;
        ImageView img,imgRating;



        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.textViewTitle);
            description = itemView.findViewById(R.id.textViewDescription);
            img = itemView.findViewById(R.id.attImgView);
            rating = itemView.findViewById(R.id.textViewRating);
            imgRating = itemView.findViewById(R.id.imageViewRating);

        }
    }

}

