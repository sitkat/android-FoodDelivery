package com.example.fooddelivery.UI.Users;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fooddelivery.Prevalent.Prevalent;
import com.example.fooddelivery.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import io.paperdb.Paper;

public class ArrangeDeliveryActivity extends AppCompatActivity {

    TextView clickableTxtClose;
    EditText edtAddress, edtNumber, edtSum;
    Button btnArrangeDelivery;

    DatabaseReference BasketRef, HistoryRef, UserRef;
    String phone;
    Double sum;

    int points;
    int getPoints;
    boolean checker = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arrange_delivery);

        init();

        btnArrangeDelivery.setOnClickListener(v -> {
            String address = edtAddress.getText().toString();
            if (TextUtils.isEmpty(address))
                Toast.makeText(this, "Введите адрес", Toast.LENGTH_SHORT).show();
            else {
                BasketRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            if (phone.equals(dataSnapshot.child("phone").getValue(String.class))) {
                                DatabaseReference BasketRefRemove = FirebaseDatabase.getInstance().getReference().child("Basket").child(dataSnapshot.getKey());
                                BasketRefRemove.removeValue();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                addToHistory();
                accruePoints();

                Intent intent = new Intent(this, HomeActivity.class);
                startActivity(intent);
            }
        });
    }

    private void accruePoints() {
        UserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                points = Integer.parseInt(dataSnapshot.child("points").getValue().toString());
                getPoints = (int) (points + ((sum * 3) / 100));
                if (checker == false) {
                    HashMap<String, Object> userMap = new HashMap<>();
                    userMap.put("points", getPoints);
                    UserRef.updateChildren(userMap);
                    checker = true;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void addToHistory() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd/MM/yyyy");
        String saveCurrentDate = currentDate.format(calendar.getTime());

        HashMap<String, Object> historyDataMap = new HashMap<>();
        historyDataMap.put("phone", phone);
        historyDataMap.put("date", saveCurrentDate);
        historyDataMap.put("address", edtAddress.getText().toString());
        historyDataMap.put("sum", sum);

        HistoryRef.push().setValue(historyDataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(ArrangeDeliveryActivity.this, "Доставка оформлена", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ArrangeDeliveryActivity.this, "Ошибка!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void init() {
        Paper.init(this);

        phone = Paper.book().read(Prevalent.UserPhoneKey);
        if (phone == "" || phone == null) {
            phone = Paper.book().read(Prevalent.UserPhoneKeyFOT);
        }

        Bundle arguments = getIntent().getExtras();
        sum = arguments.getDouble("sum");

        clickableTxtClose = findViewById(R.id.clickableTxtClose);
        edtAddress = findViewById(R.id.edtAddress);
        edtNumber = findViewById(R.id.edtNumber);
        edtSum = findViewById(R.id.edtSum);
        btnArrangeDelivery = findViewById(R.id.btnArrangeDelivery);
        BasketRef = FirebaseDatabase.getInstance().getReference().child("Basket");
        HistoryRef = FirebaseDatabase.getInstance().getReference().child("History");
        UserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(phone);

        edtNumber.setText(phone);
        edtSum.setText(sum.toString());

        clickableTxtClose.setOnClickListener(v -> {
            Intent homeIntent = new Intent(this, HomeActivity.class);
            startActivity(homeIntent);
        });


    }
}