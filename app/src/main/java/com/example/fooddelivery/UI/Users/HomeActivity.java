package com.example.fooddelivery.UI.Users;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fooddelivery.Model.Products;
import com.example.fooddelivery.Prevalent.Prevalent;
import com.example.fooddelivery.R;
import com.example.fooddelivery.UI.MainActivity;
import com.example.fooddelivery.ViewHolder.ProductViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;


public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DatabaseReference ProductsRef;
    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    CircleImageView profileImageView;
    TextView userNameTextView, pointsTextView;


    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);

        Paper.init(this);

        ProductsRef = FirebaseDatabase.getInstance().getReference().child("Products");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        loadingBar = new ProgressDialog(this);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadingBar.setTitle("Загрузка корзины");
                loadingBar.setMessage("Пожалуйста подождите");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();

                Handler handler = new Handler();
                Runnable runnable = () -> {
                    loadingBar.dismiss();
                    Intent cartIntent = new Intent(HomeActivity.this, CartActivity.class);
                    startActivity(cartIntent);
                };
                handler.postDelayed(runnable, 5000);
            }

        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
            }
        };
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener((NavigationView.OnNavigationItemSelectedListener) this);

        View headerView = navigationView.getHeaderView(0);
        userNameTextView = headerView.findViewById(R.id.user_profile_name);
        pointsTextView = headerView.findViewById(R.id.user_profile_points);
        profileImageView = headerView.findViewById(R.id.user_profile_image);

        userInfoDisplay();

        recyclerView = findViewById(R.id.recycler_menu);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

    }

    private void userInfoDisplay() {
        String phone = Paper.book().read(Prevalent.UserPhoneKey);
        if (phone == "" || phone == null) {
            phone = Paper.book().read(Prevalent.UserPhoneKeyFOT);
        }
        DatabaseReference UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(phone);

        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String name = dataSnapshot.child("name").getValue().toString();
                    String points = dataSnapshot.child("points").getValue().toString();
                    userNameTextView.setText(name);
                    pointsTextView.setText("Баллы: " + points);
                    if (dataSnapshot.child("image").exists()) {
                        String image = dataSnapshot.child("image").getValue().toString();
                        Picasso.get().load(image).placeholder(R.drawable.profile).into(profileImageView);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Products> options = new FirebaseRecyclerOptions.Builder<Products>()
                .setQuery(ProductsRef, Products.class).build();

        FirebaseRecyclerAdapter<Products, ProductViewHolder> adapter = new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull @NotNull ProductViewHolder holder, int i, @NonNull @NotNull Products model) {
                holder.pid.setText(model.getPid());
                holder.txtProductName.setText(model.getName());
                holder.txtProductDescription.setText(model.getDescription());
                holder.txtProductPrice.setText(model.getPrice() + " ₽");
                Picasso.get().load(model.getImage()).into(holder.imageView);

                holder.itemView.setOnClickListener(v -> {
                    Intent productIntent = new Intent(HomeActivity.this, ProductViewActivity.class);
                    productIntent.putExtra("pid", holder.pid.getText().toString());
                    startActivity(productIntent);
                });
            }

            @NonNull
            @NotNull
            @Override
            public ProductViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_items_layout, parent, false);
                ProductViewHolder holder = new ProductViewHolder(view);
                return holder;
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_cart) {
            Intent cartIntent = new Intent(HomeActivity.this, CartActivity.class);
            startActivity(cartIntent);
        } else if (id == R.id.nav_orders) {
            Intent historyIntent = new Intent(HomeActivity.this, HistoryActivity.class);
            startActivity(historyIntent);
        } else if (id == R.id.nav_settings) {
            Intent loginIntent = new Intent(HomeActivity.this, SettingsActivity.class);
            startActivity(loginIntent);
        } else if (id == R.id.nav_logout) {
            Paper.book().destroy();
            Intent mainIntent = new Intent(HomeActivity.this, MainActivity.class);
            startActivity(mainIntent);
        }
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);
        return false;
    }

}