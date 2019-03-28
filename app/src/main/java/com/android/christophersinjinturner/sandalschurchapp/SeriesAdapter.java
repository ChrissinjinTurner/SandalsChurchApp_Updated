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

public class SeriesAdapter extends RecyclerView.Adapter<SeriesAdapter.ViewHolder>{
    private ArrayList<Series> dataSet;
    private Context mContext;

    public SeriesAdapter(ArrayList<Series> dataSet, Context context) {
        this.dataSet = dataSet;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_series, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {
        Series series = dataSet.get(i);
        viewHolder.seriesTitle.setText(series.getTitle());
        viewHolder.seriesDate.setText(series.getDate());
        viewHolder.seriesNum.setText(series.getNum_sermons() + " Sermons");
        Picasso.get().load(series.getImage_sd()).into(viewHolder.seriesPic);

        viewHolder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Series listItem = dataSet.get(i);
                Intent intent = new Intent(mContext, SeriesListActivity.class);
                intent.putExtra("series", listItem);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView seriesTitle;
        TextView seriesDate;
        TextView seriesNum;
        ImageView seriesPic;
        ConstraintLayout parentLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            seriesTitle = itemView.findViewById(R.id.title);
            seriesDate = itemView.findViewById(R.id.date);
            seriesNum = itemView.findViewById(R.id.num);
            seriesPic = itemView.findViewById(R.id.pic);
            parentLayout = itemView.findViewById(R.id.parent_layout_series);
        }
    }
}
