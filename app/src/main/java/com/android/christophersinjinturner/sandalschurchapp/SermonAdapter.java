package com.android.christophersinjinturner.sandalschurchapp;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SermonAdapter extends RecyclerView.Adapter<SermonAdapter.ViewHolder> {
    private ArrayList<Sermon> dataSet;
    private Context mContext;

    public SermonAdapter(ArrayList<Sermon> dataSet, Context context) {
        this.dataSet = dataSet;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_sermon, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {
        Sermon sermon = dataSet.get(i);
        viewHolder.sermonTitle.setText(sermon.getTitle());
        viewHolder.sermonDate.setText(sermon.getDate());
        viewHolder.sermonLength.setText(String.valueOf(sermon.getLength() + " minutes"));
        Picasso.get().load(sermon.getImage_sd()).into(viewHolder.sermonPic);

        viewHolder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Sermon listItem = dataSet.get(i);
                Intent intent = new Intent(mContext, SermonDetailActivity.class);
                intent.putExtra("sermon", listItem);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView sermonTitle;
        TextView sermonDate;
        TextView sermonLength;
        ImageView sermonPic;
        ConstraintLayout parentLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            sermonTitle = itemView.findViewById(R.id.title);
            sermonDate = itemView.findViewById(R.id.date);
            sermonLength = itemView.findViewById(R.id.length);
            sermonPic = itemView.findViewById(R.id.pic);
            parentLayout = itemView.findViewById(R.id.parent_layout_sermon);
        }
    }
}
