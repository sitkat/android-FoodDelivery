package com.example.fooddelivery.UI.Users;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fooddelivery.Prevalent.Prevalent;
import com.example.fooddelivery.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

public class SettingsActivity extends AppCompatActivity {

    private CircleImageView profileImageView;
    private EditText fullNameEditText, userPhoneEditText, passwordEditText;
    private TextView saveTextButton, closeTextBtn;
    private Uri imageUri;
    private String checker = "";
    private StorageReference storageProfilePictureRef;
    private StorageTask uploadTask;

    String phone = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        init();

        userInfoDisplay(profileImageView, fullNameEditText, userPhoneEditText, passwordEditText);

        closeTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent loginIntent = new Intent(SettingsActivity.this, HomeActivity.class);
                startActivity(loginIntent);
            }
        });

        saveTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checker.equals("clicked")) {
                    userInfoSaved();
                } else {
                    updateOnlyUserInfo();
                }
            }
        });

        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checker = "clicked";

                CropImage.activity(imageUri)
                        .setAspectRatio(1, 1)
                        .start(SettingsActivity.this);
            }
        });
    }

    private void userInfoDisplay(final CircleImageView profileImageView, final EditText fullNameEditText, final EditText userPhoneEditText, final EditText passwordEditText) {
        phone = Paper.book().read(Prevalent.UserPhoneKey);
        if (phone == "" || phone == null) {
            phone = Paper.book().read(Prevalent.UserPhoneKeyFOT);
        }
        DatabaseReference UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(phone);

        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String name = dataSnapshot.child("name").getValue().toString();
                    fullNameEditText.setText(name);
                    userPhoneEditText.setText(phone);
                    String password = dataSnapshot.child("password").getValue().toString();
                    passwordEditText.setText(password);
                    if (dataSnapshot.child("image").exists()) {
                        String image = dataSnapshot.child("image").getValue().toString();
                        Picasso.get().load(image).into(profileImageView);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();

            profileImageView.setImageURI(imageUri);
        } else {
            Toast.makeText(this, "Ошибка", Toast.LENGTH_SHORT).show();

            startActivity(new Intent(SettingsActivity.this, SettingsActivity.class));
            finish();
        }
    }

    private void userInfoSaved() {
        if (TextUtils.isEmpty(fullNameEditText.getText().toString())) {
            Toast.makeText(this, "Заполните имя.", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(userPhoneEditText.getText().toString())) {
            Toast.makeText(this, "Заполните номер.", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(passwordEditText.getText().toString())) {
            Toast.makeText(this, "Заполните пароль.", Toast.LENGTH_SHORT).show();
        } else if (checker.equals("clicked")) {
            uploadImage();
        }
    }

    private void uploadImage() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Обновляемся..");
        progressDialog.setMessage("Пожалуйста, подождите");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        if (imageUri != null) {
            final StorageReference fileRef = storageProfilePictureRef
                    .child(phone + ".WebP");

            uploadTask = fileRef.putFile(imageUri);

            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    return fileRef.getDownloadUrl();
                }
            })
                    .addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downloadUrl = task.getResult();
                                String myUrl = downloadUrl.toString();

                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");

                                HashMap<String, Object> userMap = new HashMap<>();
                                userMap.put("name", fullNameEditText.getText().toString());
                                userMap.put("password", passwordEditText.getText().toString());
                                userMap.put("image", myUrl);
                                phone = Paper.book().read(Prevalent.UserPhoneKey);
                                if (phone != "" || phone != null) {
                                    Paper.book().write(Prevalent.UserPasswordKey, passwordEditText.getText().toString());
                                }
                                ref.child(phone).updateChildren(userMap);

                                progressDialog.dismiss();

                                startActivity(new Intent(SettingsActivity.this, HomeActivity.class));
                                Toast.makeText(SettingsActivity.this, "Информация успешно сохранена", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(SettingsActivity.this, "Ошибка.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            Toast.makeText(this, "Изображение не выбрано.", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateOnlyUserInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");

        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put("name", fullNameEditText.getText().toString());
        userMap.put("password", passwordEditText.getText().toString());

        phone = Paper.book().read(Prevalent.UserPhoneKey);
        if (phone != "" || phone != null) {
            Paper.book().write(Prevalent.UserPasswordKey, passwordEditText.getText().toString());
        }
        ref.child(phone).updateChildren(userMap);

        startActivity(new Intent(SettingsActivity.this, HomeActivity.class));
        Toast.makeText(SettingsActivity.this, "Успешно сохранено", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void init() {
        Paper.init(this);

        profileImageView = findViewById(R.id.settings_account_image);
        fullNameEditText = findViewById(R.id.settings_fullname);
        userPhoneEditText = findViewById(R.id.settings_phone);
        passwordEditText = findViewById(R.id.settings_password);
        saveTextButton = findViewById(R.id.save_settings_tv);
        closeTextBtn = findViewById(R.id.close_settings_tv);

        storageProfilePictureRef = FirebaseStorage.getInstance().getReference().child("Profile pictures");
    }
}