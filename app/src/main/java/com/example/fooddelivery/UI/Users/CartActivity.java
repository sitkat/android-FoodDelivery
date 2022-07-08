package com.example.fooddelivery.UI.Users;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fooddelivery.Adapter.MyAdapterForCard;
import com.example.fooddelivery.Model.Basket;
import com.example.fooddelivery.Prevalent.Prevalent;
import com.example.fooddelivery.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import io.paperdb.Paper;

public class CartActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    MyAdapterForCard myAdapterForCard;
    ArrayList<Basket> list = new ArrayList<>();

    DatabaseReference BasketRef;

    String pid;
    String phone;

    Button btnArrangeDelivery;
    TextView txtClose;

    Double arrangeDelivery = 0.0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        init();

        btnArrangeDelivery.setOnClickListener(v -> {
            if (btnArrangeDelivery.getText().equals("Корзина пуста"))
                Toast.makeText(this, "Пустая", Toast.LENGTH_SHORT).show();
            else {
                Intent intent = new Intent(this, ArrangeDeliveryActivity.class);
                intent.putExtra("sum", arrangeDelivery);
                startActivity(intent);
            }
        });
    }

    private void init() {
        Paper.init(this);

        phone = Paper.book().read(Prevalent.UserPhoneKey);
        if (phone == "" || phone == null) {
            phone = Paper.book().read(Prevalent.UserPhoneKeyFOT);
        }

        recyclerView = findViewById(R.id.recycler_cart);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        myAdapterForCard = new MyAdapterForCard(this, list);
        recyclerView.setAdapter(myAdapterForCard);

        btnArrangeDelivery = findViewById(R.id.btnArrangeDelivery);
        txtClose = findViewById(R.id.txtClose);


        txtClose.setOnClickListener(v -> {
            Intent homeIntent = new Intent(this, HomeActivity.class);
            startActivity(homeIntent);
        });

        BasketRef = FirebaseDatabase.getInstance().getReference().child("Basket");
        BasketInfo();
    }

    private void BasketInfo() {
        BasketRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    pid = dataSnapshot.child("pid").getValue(String.class);
                    if (phone.equals(dataSnapshot.child("phone").getValue(String.class))) {

                        DatabaseReference ProductsRef = FirebaseDatabase.getInstance().getReference().child("Products").child(pid);
                        ProductsRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {
                                double price = 0.0;
                                int count = Integer.parseInt(dataSnapshot.child("count").getValue(String.class));
                                price = Double.parseDouble(dataSnapshot1.child("price").getValue(String.class));
                                double priceMultiplyCount = count * price;

                                list.add(new Basket(dataSnapshot.child("count").getValue(String.class),
                                        dataSnapshot.child("pid").getValue(String.class),
                                        dataSnapshot.getKey(),
                                        dataSnapshot1.child("name").getValue(String.class),
                                        String.valueOf(priceMultiplyCount) + " ₽",
                                        dataSnapshot1.child("image").getValue(String.class)));

                                arrangeDelivery = arrangeDelivery + priceMultiplyCount;
                                String formattedDouble = String.format("%.2f", arrangeDelivery);
                                btnArrangeDelivery.setText("     Оформить доставку              " + formattedDouble + " ₽");
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        });
                    }
                }
                myAdapterForCard.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}