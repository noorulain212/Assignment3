package com.example.assignment3;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class VaultLogins extends AppCompatActivity {

    ImageButton btnBack;
    FloatingActionButton fabAdd;
    ListView listView;
    ItemAdapter adapter;
    ItemsDB itemsDB;
    ArrayList<DataItem> dataItems;
    TextView tvItemCount, tvUserName;
    int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vault_logins);
        init();

        SharedPreferences sPref = getSharedPreferences("Login", MODE_PRIVATE);
        userId = sPref.getInt("userId", -1); // Assuming user ID is stored as an integer

        String username = sPref.getString("username", null);
        if (username != null) {
            String firstLetter = username.substring(0, 1);
            tvUserName.setText(firstLetter.toUpperCase());
        }

        loadData();

        btnBack.setOnClickListener(view -> {
            Intent intent = new Intent(VaultLogins.this, Home.class);
            startActivity(intent);
            finish();
        });

        fabAdd.setOnClickListener(view -> {
            Intent intent = new Intent(VaultLogins.this, AddInfo.class);
            startActivity(intent);
        });

        // Register onBackPressedCallback
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(VaultLogins.this, Home.class);
                startActivity(intent);
                finish();
            }
        });
    }

    protected void onResume() {
        super.onResume();
        loadData(); // Call your method to reload data here
    }

    private void init()
    {
        btnBack = findViewById(R.id.btnBack);
        fabAdd = findViewById(R.id.fabAdd);
        itemsDB = new ItemsDB(this);
        listView = findViewById(R.id.listView);
        dataItems = new ArrayList<>();
        adapter = new ItemAdapter(this, dataItems);
        listView.setAdapter(adapter);
        tvItemCount = findViewById(R.id.tvItemCount);
        tvUserName = findViewById(R.id.tvUserName);
    }

    @SuppressLint("SetTextI18n")
    private void loadData() {
        ItemsDB itemsDB = new ItemsDB(this);
        itemsDB.open();
        dataItems.clear();
        dataItems.addAll(itemsDB.readAllItems(userId));
        itemsDB.close();
        adapter.notifyDataSetChanged();
        tvItemCount.setText(dataItems.size()+"");
    }
}