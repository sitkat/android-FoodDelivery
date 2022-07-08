package com.example.fooddelivery.Adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fooddelivery.Model.Basket;
import com.example.fooddelivery.Prevalent.Prevalent;
import com.example.fooddelivery.R;
import com.example.fooddelivery.UI.Users.ProductViewActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

import io.paperdb.Paper;

public class MyAdapterForCard extends RecyclerView.Adapter<MyAdapterForCard.MyViewHolderForCard> {

    private final LayoutInflater inflater;
    private final List<Basket> list;

    private Context mcon;

    private ProgressDialog loadingBar;


    public MyAdapterForCard(Context context, List<Basket> list) {
        this.list = list;
        this.inflater = LayoutInflater.from(context);
        loadingBar = new ProgressDialog(context);
        mcon = context;
        Paper.init(context);
    }

    @NonNull
    @Override
    public MyViewHolderForCard onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.product_cart_items_layout, parent, false);
        return new MyViewHolderForCard(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolderForCard holder, int position) {
        loadingBar.setTitle("Загрузка корзины");
        loadingBar.setMessage("Пожалуйста подождите");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        Handler handler = new Handler();
        Runnable runnable = () -> {
            Basket basket = list.get(position);

            holder.txtProductCount.setText(basket.getCount());
            holder.txtPid.setText(basket.getPid());
            holder.txtProductName.setText(basket.getProduct_name());
            holder.txtProductPrice.setText(basket.getProduct_price());
            Picasso.get().load(basket.getProduct_image()).into(holder.imgProductImage);

            holder.itemView.setOnClickListener(v -> {
                Intent productIntent = new Intent(mcon, ProductViewActivity.class);
                Paper.book().write(Prevalent.ProductPK, basket.getPk());
                productIntent.putExtra("pid", holder.txtPid.getText().toString());
                mcon.startActivity(productIntent);
            });

            loadingBar.dismiss();
        };
        handler.postDelayed(runnable, 5000);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolderForCard extends RecyclerView.ViewHolder {
        final TextView txtProductName, txtProductPrice, txtPid, txtProductCount;
        final ImageView imgProductImage;


        public MyViewHolderForCard(@NonNull View itemView) {
            super(itemView);

            txtProductName = itemView.findViewById(R.id.txtProductName);
            txtProductPrice = itemView.findViewById(R.id.txtProductPrice);
            txtProductCount = itemView.findViewById(R.id.txtProductCount);
            imgProductImage = itemView.findViewById(R.id.imgProductImage);
            txtPid = itemView.findViewById(R.id.txtPid);
        }
    }
}
