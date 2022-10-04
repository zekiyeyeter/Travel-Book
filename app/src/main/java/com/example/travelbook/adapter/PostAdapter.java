package com.example.travelbook.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travelbook.databinding.RecyclerRowBinding;
import com.example.travelbook.model.Post;
import com.example.travelbook.view.MainActivity;
import com.example.travelbook.view.MapsActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostHolder> {

     ArrayList<Post> postArrayList;

    public PostAdapter(ArrayList<Post> postArrayList) {
        this.postArrayList = postArrayList;
    }

    class PostHolder extends RecyclerView.ViewHolder {
        RecyclerRowBinding recyclerRowBinding;

        public PostHolder(@NonNull RecyclerRowBinding recyclerRowBinding) {
            super(recyclerRowBinding.getRoot());
            this.recyclerRowBinding = recyclerRowBinding;

        }
    }

    @NonNull
    @Override
    public PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerRowBinding recyclerRowBinding = RecyclerRowBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new PostHolder(recyclerRowBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull PostHolder holder, int position) {


        holder.recyclerRowBinding.recyclerviewRowPlaceName.setText(postArrayList.get(position).placeName);
        holder.recyclerRowBinding.recyclerviewRowContry.setText(postArrayList.get(position).country);
        holder.recyclerRowBinding.recyclerviewRowCommentText.setText(postArrayList.get(position).comment);
        holder.recyclerRowBinding.recyclerviewRowCity.setText(postArrayList.get(position).city);
        Picasso.get().load(postArrayList.get(position).downloadUrl).into(holder.recyclerRowBinding.recyclerviewRowImageview);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(holder.itemView.getContext(), MapsActivity.class);
                intent.putExtra("information", "old" );
                intent.putExtra("placetogo", postArrayList.get(position));
                holder.itemView.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {

        return postArrayList.size();
    }
}


