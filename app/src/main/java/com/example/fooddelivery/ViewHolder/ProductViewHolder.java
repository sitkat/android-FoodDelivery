package com.example.fooddelivery.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.fooddelivery.Interface.ItemClickListner;
import com.example.fooddelivery.R;

public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView txtProductName, txtProductDescription, txtProductPrice, pid;
    public ImageView imageView;
    public ItemClickListner listner;

    public ProductViewHolder(View itemView) {
        super(itemView);

        pid = itemView.findViewById(R.id.pid);
        imageView = itemView.findViewById(R.id.product_image);
        txtProductName = itemView.findViewById(R.id.product_name);
        txtProductDescription = itemView.findViewById(R.id.product_description);
        txtProductPrice = itemView.findViewById(R.id.product_price);

    }

    public void setItemClickListner(ItemClickListner listner) {
        this.listner = listner;
    }

    @Override
    public void onClick(View view) {
        listner.onClick(view, getAdapterPosition(), false);
    }
}
