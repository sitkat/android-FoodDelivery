package com.example.fooddelivery.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fooddelivery.Model.History;
import com.example.fooddelivery.R;

import java.util.List;

public class MyAdapterForHistory extends RecyclerView.Adapter<MyAdapterForHistory.MyViewHolderForHistory> {
    private final LayoutInflater inflater;
    private final List<History> list;

    public MyAdapterForHistory(Context context, List<History> list) {
        this.list = list;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public MyAdapterForHistory.MyViewHolderForHistory onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.history_items_layout, parent, false);
        return new MyViewHolderForHistory(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolderForHistory holder, int position) {
        History history = list.get(position);
        holder.txtDate.setText(history.getDate());
        holder.txtAddress.setText(history.getAddress());
        holder.txtSum.setText(history.getSum().toString());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolderForHistory extends RecyclerView.ViewHolder {
        final TextView txtDate, txtAddress, txtSum;

        public MyViewHolderForHistory(@NonNull View itemView) {
            super(itemView);

            txtDate = itemView.findViewById(R.id.txtDate);
            txtAddress = itemView.findViewById(R.id.txtAddress);
            txtSum = itemView.findViewById(R.id.txtSum);
        }
    }
}
