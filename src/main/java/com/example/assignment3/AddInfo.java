package com.example.assignment3;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddInfo extends AppCompatActivity {

    TextView tvAdd, tvCancel;
    EditText etUsername, etPassword, etUrl;
    int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_info);
        init();

        SharedPreferences sPref = getSharedPreferences("Login", MODE_PRIVATE);
        userId = sPref.getInt("userId", -1); // Assuming user ID is stored as an integer


        tvCancel.setOnClickListener(view -> finish());

        tvAdd.setOnClickListener(view -> {
            // Get the text from EditText fields
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String url = etUrl.getText().toString().trim();

            // Check if any field is empty
            if (username.isEmpty() || password.isEmpty() || url.isEmpty()) {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            } else {
                // Insert data into the database
                ItemsDB itemsDB = new ItemsDB(this);
                itemsDB.open();
                itemsDB.insert(userId, username, password, url);
                itemsDB.close();

                // Notify the database of the change
                Toast.makeText(this, "Data added successfully", Toast.LENGTH_SHORT).show();

                // Finish the activity
                finish();
            }
        });
    }

    private void init() {
        tvAdd = findViewById(R.id.tvAdd);
        tvCancel = findViewById(R.id.tvCancel);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etUrl = findViewById(R.id.etUrl);
    }
}