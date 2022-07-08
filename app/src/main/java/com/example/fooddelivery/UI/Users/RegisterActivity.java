package com.example.fooddelivery.UI.Users;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.fooddelivery.R;
import com.example.fooddelivery.UI.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private Button register_btn;
    private EditText register_username_input, register_phone_input, register_password_input;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        register_btn = findViewById(R.id.register_btn);
        register_username_input = findViewById(R.id.register_username_input);
        register_phone_input = findViewById(R.id.register_phone_input);
        register_password_input = findViewById(R.id.register_password_input);
        loadingBar = new ProgressDialog(this);

        register_btn.setOnClickListener(view -> {
            CreateAccount();
        });
    }

    private void CreateAccount() {
        String _username = register_username_input.getText().toString();
        String _phone = register_phone_input.getText().toString();
        String _password = register_password_input.getText().toString();

        if (TextUtils.isEmpty(_username))
            Toast.makeText(this, "Введите имя", Toast.LENGTH_SHORT).show();
        else if (TextUtils.isEmpty(_phone))
            Toast.makeText(this, "Введите номер", Toast.LENGTH_SHORT).show();
        else if (_phone.length() != 11)
            Toast.makeText(this, "Введите корректный номер телефона.\nПример: 80000000000", Toast.LENGTH_SHORT).show();
        else if (TextUtils.isEmpty(_password))
            Toast.makeText(this, "Введите пароль", Toast.LENGTH_SHORT).show();
        else {
            loadingBar.setTitle("Создание аккаунта");
            loadingBar.setMessage("Пожалуйста подождите");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            ValidatePhoneUnique(_username, _phone, _password);
        }
    }

    private void ValidatePhoneUnique(String username, String phone, String password) {
        final DatabaseReference RootRef = FirebaseDatabase.getInstance().getReference();
        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.child("Users").child(phone).exists()) {
                    HashMap<String, Object> userDataMap = new HashMap<>();
                    userDataMap.put("phone", phone);
                    userDataMap.put("name", username);
                    userDataMap.put("password", password);
                    userDataMap.put("points", 0.0F);

                    RootRef.child("Users").child(phone).updateChildren(userDataMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        loadingBar.dismiss();
                                        Toast.makeText(RegisterActivity.this, "Регистрация прошла успешно", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                    } else {
                                        loadingBar.dismiss();
                                        Toast.makeText(RegisterActivity.this, "Ошибка!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                } else {
                    loadingBar.dismiss();
                    Toast.makeText(RegisterActivity.this, "Номер "+phone+" уже зарегистрирован", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}