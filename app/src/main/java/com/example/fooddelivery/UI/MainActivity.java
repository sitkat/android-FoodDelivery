package com.example.fooddelivery.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.Toast;

import com.example.fooddelivery.Model.Users;
import com.example.fooddelivery.Prevalent.Prevalent;
import com.example.fooddelivery.R;
import com.example.fooddelivery.UI.Users.HomeActivity;
import com.example.fooddelivery.UI.Users.RegisterActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {

    private Button main_join_btn, main_login_btn;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        main_login_btn = findViewById(R.id.main_login_btn);
        main_join_btn = findViewById(R.id.main_join_btn);
        loadingBar = new ProgressDialog(this);

        Paper.init(this);

        main_login_btn.setOnClickListener(view -> {
            Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(loginIntent);
        });

        main_join_btn.setOnClickListener(view -> {
            Intent registerIntent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(registerIntent);
        });

        String UserPhoneKey = Paper.book().read(Prevalent.UserPhoneKey);
        String UserPasswordKey = Paper.book().read(Prevalent.UserPasswordKey);

        if (UserPhoneKey != "" && UserPasswordKey != "") {
            if (!TextUtils.isEmpty(UserPhoneKey) && !TextUtils.isEmpty(UserPasswordKey)) {
                ValidateUser(UserPhoneKey, UserPasswordKey);

                loadingBar.setTitle("Вход в приложение");
                loadingBar.setMessage("Пожалуйста подождите");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();
            }
        }
    }

    private void ValidateUser(final String phone, final String password) {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("Users").child(phone).exists()) {
                    String _name = snapshot.child("Users").child(phone).child("name").getValue(String.class);
                    String _password = snapshot.child("Users").child(phone).child("password").getValue(String.class);
                    String _phone = snapshot.child("Users").child(phone).child("phone").getValue(String.class);
                    Float _points = snapshot.child("Users").child(phone).child("points").getValue(Float.class);

                    Users user = new Users();
                    user.set_name(_name);
                    user.set_password(_password);
                    user.set_phone(_phone);
                    user.set_points(_points);

                    if (user.get_phone().equals(phone)) {
                        if (user.get_password().equals(password)) {
                            loadingBar.dismiss();
                            Toast.makeText(MainActivity.this, "Успешный вход!", Toast.LENGTH_SHORT).show();

                            Intent homeIntent = new Intent(MainActivity.this, HomeActivity.class);
                            startActivity(homeIntent);
                        } else {
                            loadingBar.dismiss();
                        }
                    }
                } else {
                    loadingBar.dismiss();
                    Toast.makeText(MainActivity.this, "Аккаунт с номером  " + phone + " не зарегистрирован", Toast.LENGTH_SHORT).show();
                    Intent registerIntent = new Intent(MainActivity.this, RegisterActivity.class);
                    startActivity(registerIntent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}