package com.example.fooddelivery.UI.Users;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.fooddelivery.Prevalent.Prevalent;
import com.example.fooddelivery.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import io.paperdb.Paper;

public class ProductViewActivity extends AppCompatActivity {

    TextView product_name, product_price, product_description, product_count, clickableTxtClose;
    Button btnMinus, btnPlus, btnAddToBasket;
    ImageView product_image;
    private ProgressDialog loadingBar;

    private String saveCurrentDate, saveCurrentTime, productRandomKey;

    double price;
    int count = 1;
    double priceMultiplyCount;

    String pid;
    String PKfromCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_view);

        init();
        productInfo();

        btnMinus.setOnClickListener(v -> {
            count = Integer.parseInt(product_count.getText().toString());
            if (PKfromCard != null) {
                if (count > 1) {
                    count--;
                    product_count.setText(String.valueOf(count));
                    priceMultiplyCount = count * price;
                    String formattedDouble = String.format("%.2f", priceMultiplyCount);
                    btnAddToBasket.setText("  Добавить   " + formattedDouble + " ₽");
                } else {
                    Toast.makeText(this, "-", Toast.LENGTH_SHORT).show();
                    DatabaseReference BasketRef = FirebaseDatabase.getInstance().getReference().child("Basket").child(PKfromCard);
                    BasketRef.removeValue();
                    Paper.book().delete(Prevalent.ProductPK);
                    Intent homeIntent = new Intent(ProductViewActivity.this, CartActivity.class);
                    startActivity(homeIntent);
                }
            } else {
                if (count > 1) {
                    count--;
                    product_count.setText(String.valueOf(count));
                    priceMultiplyCount = count * price;
                    String formattedDouble = String.format("%.2f", priceMultiplyCount);
                    btnAddToBasket.setText("  Добавить   " + formattedDouble + " ₽");
                }
            }
        });

        btnPlus.setOnClickListener(v -> {
            if (count < 100) {
                count++;
                product_count.setText(String.valueOf(count));
                priceMultiplyCount = count * price;
                String formattedDouble = String.format("%.2f", priceMultiplyCount);
                btnAddToBasket.setText("  Добавить   " + formattedDouble + " ₽");
            }
        });

        btnAddToBasket.setOnClickListener(v -> {
            Paper.book().delete(Prevalent.ProductPK);
            addToBasket();
        });

        clickableTxtClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Paper.book().delete(Prevalent.ProductPK);
                Intent homeIntent = new Intent(ProductViewActivity.this, HomeActivity.class);
                startActivity(homeIntent);
            }
        });
    }

    private void addToBasket() {
        loadingBar.setTitle("Добавление товара в корзину");
        loadingBar.setMessage("Пожалуйста подождите");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        String phone = Paper.book().read(Prevalent.UserPhoneKey);
        if (phone == "" || phone == null) {
            phone = Paper.book().read(Prevalent.UserPhoneKeyFOT);
        }
        final String _phone = phone;

        DatabaseReference BasketRef = FirebaseDatabase.getInstance().getReference().child("Basket");
        BasketRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat currentDate = new SimpleDateFormat("ddMMyyyy");
                saveCurrentDate = currentDate.format(calendar.getTime());

                SimpleDateFormat currentTime = new SimpleDateFormat("HHmmss");
                saveCurrentTime = currentTime.format(calendar.getTime());

                productRandomKey = saveCurrentDate + saveCurrentTime;

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if (pid.equals(dataSnapshot.child("pid").getValue(String.class)))
                        productRandomKey = dataSnapshot.child("pk").getValue(String.class);
                }

                HashMap<String, Object> basketDataMap = new HashMap<>();
                basketDataMap.put("phone", _phone);
                basketDataMap.put("pid", pid);
                basketDataMap.put("count", product_count.getText().toString());
                basketDataMap.put("pk", productRandomKey);

                BasketRef.child(productRandomKey).updateChildren(basketDataMap)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    loadingBar.dismiss();
                                    Toast.makeText(ProductViewActivity.this, "Товар добавлен в корзину", Toast.LENGTH_SHORT).show();

                                    Intent homeIntent = new Intent(ProductViewActivity.this, HomeActivity.class);
                                    startActivity(homeIntent);
                                } else {
                                    loadingBar.dismiss();
                                    Toast.makeText(ProductViewActivity.this, "Произошла ошибка", Toast.LENGTH_SHORT).show();

                                }
                            }
                        });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void productInfo() {
        DatabaseReference ProductRef = FirebaseDatabase.getInstance().getReference().child("Products").child(pid);

        ProductRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    price = Double.parseDouble(dataSnapshot.child("price").getValue().toString());
                    product_name.setText(dataSnapshot.child("name").getValue().toString());
                    product_price.setText(dataSnapshot.child("price").getValue().toString() + " ₽");
                    product_description.setText(dataSnapshot.child("description").getValue().toString());

                    btnAddToBasket.setText("  Добавить   " + dataSnapshot.child("price").getValue().toString() + " ₽");

                    Picasso.get().load(dataSnapshot.child("image").getValue().toString()).into(product_image);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        if (PKfromCard != null) {
            DatabaseReference BasketRef = FirebaseDatabase.getInstance().getReference().child("Basket").child(PKfromCard);
            BasketRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    count = Integer.parseInt(snapshot.child("count").getValue().toString());
                    product_count.setText(String.valueOf(snapshot.child("count").getValue().toString()));
                    priceMultiplyCount = count * price;
                    String formattedDouble = String.format("%.2f", priceMultiplyCount);
                    btnAddToBasket.setText("  Добавить   " + formattedDouble + " ₽");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
    }

    private void init() {
        Bundle arguments = getIntent().getExtras();
        pid = arguments.get("pid").toString();

        Paper.init(this);

        PKfromCard = Paper.book().read(Prevalent.ProductPK);

        product_name = findViewById(R.id.product_name);
        product_price = findViewById(R.id.product_price);
        product_description = findViewById(R.id.product_description);
        product_count = findViewById(R.id.product_count);
        btnMinus = findViewById(R.id.btnMinus);
        btnPlus = findViewById(R.id.btnPlus);
        btnAddToBasket = findViewById(R.id.btnAddToBasket);
        product_image = findViewById(R.id.product_image);
        clickableTxtClose = findViewById(R.id.clickableTxtClose);

        loadingBar = new ProgressDialog(this);
    }
}