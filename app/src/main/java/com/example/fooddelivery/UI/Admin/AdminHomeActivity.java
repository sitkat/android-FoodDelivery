package com.example.fooddelivery.UI.Admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fooddelivery.Model.Products;
import com.example.fooddelivery.Prevalent.Prevalent;
import com.example.fooddelivery.R;
import com.example.fooddelivery.UI.MainActivity;
import com.example.fooddelivery.ViewHolder.ProductViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import io.paperdb.Paper;

public class AdminHomeActivity extends AppCompatActivity {

    DatabaseReference ProductsRef;
    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        Paper.init(this);
        ProductsRef = FirebaseDatabase.getInstance().getReference().child("Products");

        ImageView imgExit = findViewById(R.id.imgExit);
        imgExit.setOnClickListener(v -> {
            Intent mainIntent = new Intent(AdminHomeActivity.this, MainActivity.class);
            startActivity(mainIntent);
        });

        Button btnAddProduct = findViewById(R.id.btnAddProduct);
        btnAddProduct.setOnClickListener(v -> {
            Intent categoryIntent = new Intent(AdminHomeActivity.this, AdminCategoryActivity.class);
            startActivity(categoryIntent);
        });

        recyclerView = findViewById(R.id.recycler_products);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Products> options = new FirebaseRecyclerOptions.Builder<Products>()
                .setQuery(ProductsRef, Products.class).build();

        FirebaseRecyclerAdapter<Products, ProductViewHolder> adapter = new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull @NotNull ProductViewHolder holder, int i, @NonNull @NotNull Products model) {
                holder.pid.setText(model.getPid());
                holder.txtProductName.setText(model.getName());
                holder.txtProductDescription.setText(model.getDescription());
                holder.txtProductPrice.setText(model.getPrice() + " ₽");
                Picasso.get().load(model.getImage()).into(holder.imageView);

                holder.itemView.setOnClickListener(v -> {
                    Intent editProductIntent = new Intent(AdminHomeActivity.this, AdminAddNewProductActivity.class);
                    Paper.book().write(Prevalent.ProductPID, holder.pid.getText());
                    editProductIntent.putExtra("pid", holder.pid.getText().toString());
                    startActivity(editProductIntent);
                });
            }

            @NonNull
            @NotNull
            @Override
            public ProductViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_items_layout, parent, false);
                ProductViewHolder holder = new ProductViewHolder(view);
                return holder;
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }
}