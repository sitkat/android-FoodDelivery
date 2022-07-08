package com.example.fooddelivery.UI.Admin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import io.paperdb.Paper;

public class AdminAddNewProductActivity extends AppCompatActivity {

    private String categoryName, saveCurrentDate, saveCurrentTime, productRandomKey;

    private ImageView select_product_image;
    String _description_input, _product_name_input, _product_price_input;
    private EditText product_name_input, description_input, product_price_input;
    private Button add_new_product_btn;
    private TextView clickableTxtClose;

    private static final int GALLERYPICK = 1;
    private Uri ImageUri;
    private StorageReference ProductImageRef;
    private DatabaseReference ProductRef;
    private ProgressDialog loadingBar;

    private StorageTask uploadTask;
    String myUrl;

    String pid;
    String checker = "noClicked";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_new_product);

        init();

        clickableTxtClose.setOnClickListener(v -> {
            Intent intent = new Intent(AdminAddNewProductActivity.this, AdminHomeActivity.class);
            startActivity(intent);
        });

        select_product_image.setOnClickListener(view -> {
            checker = "clicked";
            OpenGallery();
        });
        add_new_product_btn.setOnClickListener(view -> {
            ValidateProductData();
        });

    }

    private void updateOnlyProduct() {
        loadingBar.setTitle("Загрузка данных");
        loadingBar.setMessage("Пожалуйста подождите");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Products");

        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put("name", product_name_input.getText().toString());
        userMap.put("description", description_input.getText().toString());
        userMap.put("price", product_price_input.getText().toString());

        ref.child(pid).updateChildren(userMap);
        Paper.book().destroy();
        loadingBar.dismiss();
        startActivity(new Intent(AdminAddNewProductActivity.this, AdminHomeActivity.class));
        Toast.makeText(AdminAddNewProductActivity.this, "Изменения сохранены", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void ValidateProductData() {
        _description_input = description_input.getText().toString();
        _product_name_input = product_name_input.getText().toString();
        _product_price_input = product_price_input.getText().toString();

        if (checker.equals("noClicked")) {
            Toast.makeText(this, "Добавьте изображение товара", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(_product_name_input)) {
            Toast.makeText(this, "Добавьте наименование товара", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(_description_input)) {
            Toast.makeText(this, "Добавьте описание товара", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(_product_price_input)) {
            Toast.makeText(this, "Добавьте стоимость товара", Toast.LENGTH_SHORT).show();
        } else {
            if (pid != null && checker.equals("clicked")) {
                updateProductWithImg();
            } else if (pid != null && checker.equals("alreadyHas")) {
                updateOnlyProduct();
            } else {
                StoreProductInformation();
            }
        }

    }

    private void updateProductWithImg() {
        loadingBar.setTitle("Загрузка данных");
        loadingBar.setMessage("Пожалуйста подождите");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        final StorageReference filePath = ProductImageRef.child(ImageUri.getLastPathSegment() + pid + ".WebP");

        uploadTask = filePath.putFile(ImageUri);

        uploadTask.continueWithTask(new Continuation() {
            @Override
            public Object then(@NonNull Task task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                return filePath.getDownloadUrl();
            }
        })
                .addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri downloadUrl = task.getResult();
                            myUrl = downloadUrl.toString();

                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Products");

                            HashMap<String, Object> userMap = new HashMap<>();
                            userMap.put("name", product_name_input.getText().toString());
                            userMap.put("description", description_input.getText().toString());
                            userMap.put("price", product_price_input.getText().toString());
                            userMap.put("image", myUrl);

                            ref.child(pid).updateChildren(userMap);
                            Paper.book().destroy();
                            loadingBar.dismiss();
                            startActivity(new Intent(AdminAddNewProductActivity.this, AdminHomeActivity.class));
                            Toast.makeText(AdminAddNewProductActivity.this, "Изменения сохранены", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                });

    }

    private void productInfo() {
        DatabaseReference ProductInfoRef = FirebaseDatabase.getInstance().getReference().child("Products").child(pid);
        ProductInfoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    description_input.setText(dataSnapshot.child("description").getValue().toString());
                    product_name_input.setText(dataSnapshot.child("name").getValue().toString());
                    product_price_input.setText(dataSnapshot.child("price").getValue().toString());
                    checker = "alreadyHas";
                    Picasso.get().load(dataSnapshot.child("image").getValue().toString()).into(select_product_image);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void StoreProductInformation() {
        loadingBar.setTitle("Загрузка данных");
        loadingBar.setMessage("Пожалуйста подождите");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("ddMMyyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HHmmss");
        saveCurrentTime = currentTime.format(calendar.getTime());

        productRandomKey = saveCurrentDate + saveCurrentTime;
        final StorageReference filePath = ProductImageRef.child(ImageUri.getLastPathSegment() + productRandomKey + ".WebP");

        uploadTask = filePath.putFile(ImageUri);

        uploadTask.continueWithTask(new Continuation() {
            @Override
            public Object then(@NonNull Task task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                return filePath.getDownloadUrl();
            }
        })
                .addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri downloadUrl = task.getResult();
                            myUrl = downloadUrl.toString();

                            SaveProductInfoToDataBase();
                        }
                    }
                });

    }

    private void SaveProductInfoToDataBase() {
        ProductRef = FirebaseDatabase.getInstance().getReference();
        HashMap<String, Object> productMap = new HashMap<>();
        productMap.put("pid", productRandomKey);
        productMap.put("date", saveCurrentDate);
        productMap.put("time", saveCurrentTime);
        productMap.put("name", _product_name_input);
        productMap.put("description", _description_input);
        productMap.put("price", _product_price_input);
        productMap.put("image", myUrl);
        productMap.put("category", categoryName);

        ProductRef.child("Products").child(productRandomKey).updateChildren(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    loadingBar.dismiss();
                    Paper.book().destroy();
                    Toast.makeText(AdminAddNewProductActivity.this, "Товар добавлен", Toast.LENGTH_SHORT).show();

                    Intent categoryIntent = new Intent(AdminAddNewProductActivity.this, AdminCategoryActivity.class);
                    startActivity(categoryIntent);
                } else {
                    loadingBar.dismiss();
                    String message = task.getException().toString();
                    Toast.makeText(AdminAddNewProductActivity.this, "Ошибка: " + message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void OpenGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GALLERYPICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERYPICK && data != null) {
            ImageUri = data.getData();
            select_product_image.setImageURI(ImageUri);
        }
    }

    private void init() {
        Paper.init(this);
        Bundle arguments = getIntent().getExtras();
        if (arguments.get("pid").toString().equals("")) {
            pid = null;
        } else
            pid = Paper.book().read(Prevalent.ProductPID);

        categoryName = Paper.book().read(Prevalent.ProductCategory);

        select_product_image = findViewById(R.id.select_product_image);
        product_name_input = findViewById(R.id.product_name_input);
        description_input = findViewById(R.id.description_input);
        product_price_input = findViewById(R.id.product_price_input);
        add_new_product_btn = findViewById(R.id.add_new_product_btn);
        clickableTxtClose = findViewById(R.id.clickableTxtClose);

        ProductImageRef = FirebaseStorage.getInstance().getReference().child("Product Images");
        loadingBar = new ProgressDialog(this);

        if (pid != null) {
            add_new_product_btn.setText("Сохранить изменения");
            productInfo();
        }

    }
}