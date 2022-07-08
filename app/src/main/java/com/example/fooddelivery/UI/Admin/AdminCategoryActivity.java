package com.example.fooddelivery.UI.Admin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fooddelivery.Prevalent.Prevalent;
import com.example.fooddelivery.R;

import io.paperdb.Paper;

public class AdminCategoryActivity extends AppCompatActivity {

    private ImageView sushi, burger, pizza;
    private ImageView shayrma, fish, steak;
    private ImageView ramen, homeMeal, sweets;
    private ImageView breakfast, drinks, alcohol;
    private TextView clickableTxtClose;

    String pid = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_category);

        init();

        clickableTxtClose.setOnClickListener(v -> {
            Intent categoryIntent = new Intent(AdminCategoryActivity.this, AdminHomeActivity.class);
            startActivity(categoryIntent);
        });

        sushi.setOnClickListener(view -> {
            Intent intent = new Intent(this, AdminAddNewProductActivity.class);
            Paper.book().write(Prevalent.ProductCategory, "sushi");
            intent.putExtra("pid", pid);
            startActivity(intent);
        });
        burger.setOnClickListener(view -> {
            Intent intent = new Intent(this, AdminAddNewProductActivity.class);
            Paper.book().write(Prevalent.ProductCategory, "burger");
            intent.putExtra("pid", pid);
            startActivity(intent);
        });
        pizza.setOnClickListener(view -> {
            Intent intent = new Intent(this, AdminAddNewProductActivity.class);
            Paper.book().write(Prevalent.ProductCategory, "pizza");
            intent.putExtra("pid", pid);
            startActivity(intent);
        });

        shayrma.setOnClickListener(view -> {
            Intent intent = new Intent(this, AdminAddNewProductActivity.class);
            Paper.book().write(Prevalent.ProductCategory, "shayrma");
            intent.putExtra("pid", pid);
            startActivity(intent);
        });
        fish.setOnClickListener(view -> {
            Intent intent = new Intent(this, AdminAddNewProductActivity.class);
            Paper.book().write(Prevalent.ProductCategory, "fish");
            intent.putExtra("pid", pid);
            startActivity(intent);
        });
        steak.setOnClickListener(view -> {
            Intent intent = new Intent(this, AdminAddNewProductActivity.class);
            Paper.book().write(Prevalent.ProductCategory, "steak");
            intent.putExtra("pid", pid);
            startActivity(intent);
        });

        ramen.setOnClickListener(view -> {
            Intent intent = new Intent(this, AdminAddNewProductActivity.class);
            Paper.book().write(Prevalent.ProductCategory, "ramen");
            intent.putExtra("pid", pid);
            startActivity(intent);
        });
        homeMeal.setOnClickListener(view -> {
            Intent intent = new Intent(this, AdminAddNewProductActivity.class);
            Paper.book().write(Prevalent.ProductCategory, "homeMeal");
            intent.putExtra("pid", pid);
            startActivity(intent);
        });
        sweets.setOnClickListener(view -> {
            Intent intent = new Intent(this, AdminAddNewProductActivity.class);
            Paper.book().write(Prevalent.ProductCategory, "sweets");
            intent.putExtra("pid", pid);
            startActivity(intent);
        });

        breakfast.setOnClickListener(view -> {
            Intent intent = new Intent(this, AdminAddNewProductActivity.class);
            Paper.book().write(Prevalent.ProductCategory, "breakfast");
            intent.putExtra("pid", pid);
            startActivity(intent);
        });
        drinks.setOnClickListener(view -> {
            Intent intent = new Intent(this, AdminAddNewProductActivity.class);
            Paper.book().write(Prevalent.ProductCategory, "drinks");
            intent.putExtra("pid", pid);
            startActivity(intent);
        });
        alcohol.setOnClickListener(view -> {
            Intent intent = new Intent(this, AdminAddNewProductActivity.class);
            Paper.book().write(Prevalent.ProductCategory, "alcohol");
            intent.putExtra("pid", pid);
            startActivity(intent);
        });
    }

    private void init() {
        Paper.init(this);

        clickableTxtClose = findViewById(R.id.clickableTxtClose);

        sushi = findViewById(R.id.sushi);
        burger = findViewById(R.id.burger);
        pizza = findViewById(R.id.pizza);

        shayrma = findViewById(R.id.shayrma);
        fish = findViewById(R.id.fish);
        steak = findViewById(R.id.steak);

        ramen = findViewById(R.id.ramen);
        homeMeal = findViewById(R.id.homeMeal);
        sweets = findViewById(R.id.sweets);

        breakfast = findViewById(R.id.breakfast);
        drinks = findViewById(R.id.drinks);
        alcohol = findViewById(R.id.alcohol);
    }
}