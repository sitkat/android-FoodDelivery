package com.example.fooddelivery.UI.Users;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fooddelivery.Adapter.MyAdapterForHistory;
import com.example.fooddelivery.Model.History;
import com.example.fooddelivery.Prevalent.Prevalent;
import com.example.fooddelivery.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import io.paperdb.Paper;

public class HistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    MyAdapterForHistory myAdapterForHistory;
    ArrayList<History> list = new ArrayList<>();

    String phone;
    TextView txtClose;
    DatabaseReference HistoryRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        init();
        HistoryInfo();
    }

    private void HistoryInfo() {
        HistoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if (phone.equals(dataSnapshot.child("phone").getValue(String.class))) {
                        History history = dataSnapshot.getValue(History.class);
                        list.add(history);
                    }
                }
                myAdapterForHistory.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void init() {
        Paper.init(this);

        phone = Paper.book().read(Prevalent.UserPhoneKey);
        if (phone == "" || phone == null) {
            phone = Paper.book().read(Prevalent.UserPhoneKeyFOT);
        }

        recyclerView = findViewById(R.id.recycler_history);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        myAdapterForHistory = new MyAdapterForHistory(this, list);
        recyclerView.setAdapter(myAdapterForHistory);

        txtClose = findViewById(R.id.txtClose);

        txtClose.setOnClickListener(v -> {
            Intent homeIntent = new Intent(this, HomeActivity.class);
            startActivity(homeIntent);
        });
        HistoryRef = FirebaseDatabase.getInstance().getReference().child("History");
    }
}