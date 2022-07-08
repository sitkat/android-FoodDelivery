package com.example.fooddelivery.UI;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fooddelivery.Model.Users;
import com.example.fooddelivery.Prevalent.Prevalent;
import com.example.fooddelivery.R;
import com.example.fooddelivery.UI.Admin.AdminHomeActivity;
import com.example.fooddelivery.UI.Users.HomeActivity;
import com.example.fooddelivery.UI.Users.RegisterActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rey.material.widget.CheckBox;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {

    private Button login_btn;
    private EditText login_phone_input, login_password_input;
    private ProgressDialog loadingBar;

    private TextView login_admin, login_client;

    private String parentDbName = "Users";
    private CheckBox login_checkBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        login_btn = findViewById(R.id.login_btn);
        login_phone_input = findViewById(R.id.login_phone_input);
        login_password_input = findViewById(R.id.login_password_input);
        loadingBar = new ProgressDialog(this);
        login_checkBox = findViewById(R.id.login_checkBox);
        Paper.init(this);

        login_admin = findViewById(R.id.login_admin);
        login_client = findViewById(R.id.login_client);

        login_btn.setOnClickListener(view -> {
            LoginUser();
        });

        login_admin.setOnClickListener(view -> {
            login_admin.setVisibility(View.INVISIBLE);
            login_client.setVisibility(View.VISIBLE);
            login_checkBox.setVisibility(View.INVISIBLE);

            login_btn.setText("Вход для админа");
            parentDbName = "Admins";
        });

        login_client.setOnClickListener(view -> {
            login_admin.setVisibility(View.VISIBLE);
            login_client.setVisibility(View.INVISIBLE);
            login_checkBox.setVisibility(View.VISIBLE);

            login_btn.setText("Войти");
            parentDbName = "Users";
        });
    }

    private void LoginUser() {
        String _phone = login_phone_input.getText().toString();
        String _password = login_password_input.getText().toString();

        if (TextUtils.isEmpty(_phone))
            Toast.makeText(this, "Введите номер", Toast.LENGTH_SHORT).show();
        else if (TextUtils.isEmpty(_password))
            Toast.makeText(this, "Введите пароль", Toast.LENGTH_SHORT).show();
        else {
            loadingBar.setTitle("Вход в приложение");
            loadingBar.setMessage("Пожалуйста подождите");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            ValidateUser(_phone, _password);
        }
    }

    private void ValidateUser(String phone, String password) {

        if (login_checkBox.isChecked()) {
            Paper.book().write(Prevalent.UserPhoneKey, phone);
            Paper.book().write(Prevalent.UserPasswordKey, password);
        }

        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(parentDbName).child(phone).exists()) {
                    String _name = snapshot.child(parentDbName).child(phone).child("name").getValue(String.class);
                    String _password = snapshot.child(parentDbName).child(phone).child("password").getValue(String.class);
                    String _phone = snapshot.child(parentDbName).child(phone).child("phone").getValue(String.class);
                    Float _points = snapshot.child(parentDbName).child(phone).child("points").getValue(Float.class);

                    Users user = new Users();
                    user.set_name(_name);
                    user.set_password(_password);
                    user.set_phone(_phone);
                    user.set_points(_points);

                    if (user.get_phone().equals(phone)) {
                        if (user.get_password().equals(password)) {
                            if (parentDbName.equals("Users")) {
                                loadingBar.dismiss();
                                Toast.makeText(LoginActivity.this, "Успешный вход!", Toast.LENGTH_SHORT).show();

                                Paper.book().write(Prevalent.UserPhoneKeyFOT, phone);

                                Intent homeIntent = new Intent(LoginActivity.this, HomeActivity.class);
                                startActivity(homeIntent);
                            } else if (parentDbName.equals("Admins")) {
                                loadingBar.dismiss();
                                Toast.makeText(LoginActivity.this, "Успешный вход!", Toast.LENGTH_SHORT).show();


                                Intent adminIntent = new Intent(LoginActivity.this, AdminHomeActivity.class);
                                startActivity(adminIntent);
                            }
                        } else {
                            loadingBar.dismiss();
                            Toast.makeText(LoginActivity.this, "Неверный пароль", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    loadingBar.dismiss();
                    Toast.makeText(LoginActivity.this, "Аккаунт с номером  " + phone + " не зарегистрирован", Toast.LENGTH_SHORT).show();
                    Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                    startActivity(registerIntent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
