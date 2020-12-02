package com.example.project2development;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {

    Context context;
    private ArrayList title, description, rating, user_email;


    CustomAdapter(Context context, ArrayList title, ArrayList description, ArrayList rating, ArrayList user_email){
        this.context = context;
        this.title = title;
        this.description = description;
        this.rating = rating;
        this.user_email = user_email;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recyclerview_reviews, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.user_email.setText(String.valueOf(user_email.get(position)));
        holder.title.setText(String.valueOf(title.get(position)));
        holder.description.setText(String.valueOf(description.get(position)));
        holder.rating.setText(String.valueOf(rating.get(position)));
    }

    @Override
    public int getItemCount() {
        return user_email.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView title, description, rating, user_email;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            user_email = itemView.findViewById(R.id.textViewUser);
            title = itemView.findViewById(R.id.textViewTitle);
            description = itemView.findViewById(R.id.textViewDescription);
            rating = itemView.findViewById(R.id.textViewRating);

        }
    }
}
