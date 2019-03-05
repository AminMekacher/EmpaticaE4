package com.example.android.empaticae4;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by aminmekacher on 16.07.18.
 */

public class DashAdapter extends RecyclerView.Adapter<DashAdapter.ViewHolder> {


    ArrayList<DashModel> dashModelArrayList;

    public DashAdapter(ArrayList<DashModel> dashModelArrayList) {
        this.dashModelArrayList = dashModelArrayList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_items, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        String ret_head = dashModelArrayList.get(position).getHead();
        holder.setHeader(ret_head);

        String ret_sub = dashModelArrayList.get(position).getSub();
        holder.setSub(ret_sub);

        int ret_image = dashModelArrayList.get(position).getImage();
        holder.setImage(ret_image);

    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemCount() {
        return dashModelArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView header, sub_header;
        ImageView images;
        View myView;

        public ViewHolder(View itemView) {
            super(itemView);
            myView = itemView;
        }

        public void setHeader(String h) {
            header = myView.findViewById(R.id.text1);
            header.setText(h);
        }

        public void setSub(String s) {
            sub_header = myView.findViewById(R.id.text2);
            sub_header.setText(s);
        }

        public void setImage(int i) {
            images = myView.findViewById(R.id.first);
            images.setImageResource(i);
        }
    }
}
