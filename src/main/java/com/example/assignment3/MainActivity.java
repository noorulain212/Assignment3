package com.example.assignment3;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    FragmentManager manager;
    View loginView;
    Button btnLExit, btnLogin, btnLSignUp;
    EditText etLoginUsername, etLoginPassword;
    ImageButton btnToggleLoginPassword;

    View signupView;
    Button btnSExit, btnSignUp, btnSLogin;
    EditText etSignUpUsername, etSignUpPassword, etSConfirmPassword;
    TextView passwordMismatchError;
    ImageButton btnTogglePassword, btnToggleConfirmPassword;
    RelativeLayout rlConfirm;

    ItemsDB itemsDB; // Database helper
    SharedPreferences sPref; // Shared preferences for storing login data

    Fragment LoginFragment, SignUpFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

        itemsDB = new ItemsDB(this);
        sPref = getSharedPreferences("Login", MODE_PRIVATE);

//        boolean isLoggedIn = sPref.getBoolean("isLoggedIn", false);
//        if(isLoggedIn) {
//            startHomeActivity();
//            return;
//        }

        // Login button click listener
        btnLogin.setOnClickListener(view -> {
            String username = etLoginUsername.getText().toString();
            String password = etLoginPassword.getText().toString();

            if(validateLogin(username, password)) {
                saveLoginState(username); // Save login state
                startHomeActivity(); // Open home activity
            } else {
                Toast.makeText(MainActivity.this, "Incorrect Username or Password", Toast.LENGTH_SHORT).show();
            }
        });

        // SignUp button click listener
        btnLSignUp.setOnClickListener(view -> manager.beginTransaction()
                .hide(LoginFragment)
                .show(SignUpFragment)
                .commit());

        // Sign in from signup fragment
        btnSLogin.setOnClickListener(view -> manager.beginTransaction()
                .show(LoginFragment)
                .hide(SignUpFragment)
                .commit());

        // SignUp button click listener
        btnSignUp.setOnClickListener(view -> {
            String username = etSignUpUsername.getText().toString();
            String password = etSignUpPassword.getText().toString();
            String confirmPassword = etSConfirmPassword.getText().toString();

            if(username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(MainActivity.this, "Fill all fields", Toast.LENGTH_SHORT).show();
            } else if(!password.equals(confirmPassword)) {
                passwordMismatchError.setVisibility(View.VISIBLE);
                rlConfirm.setBackgroundResource(R.drawable.edit_text_border_error);
            } else {
                registerUser(username, password);
                manager.beginTransaction()
                        .show(LoginFragment)
                        .hide(SignUpFragment)
                        .commit();
            }
        });

        // Toggle visibility
        btnToggleLoginPassword.setOnClickListener(v -> togglePasswordVisibility(etLoginPassword));
        btnTogglePassword.setOnClickListener(v -> togglePasswordVisibility(etSignUpPassword));
        btnToggleConfirmPassword.setOnClickListener(v -> togglePasswordVisibility(etSConfirmPassword));

        // Exit button click listeners
        btnLExit.setOnClickListener(v -> finish());
        btnSExit.setOnClickListener(v -> finish());
    }

    private void init() {
        manager = getSupportFragmentManager();

        loginView = Objects.requireNonNull(manager.findFragmentById(R.id.fragLogin)).requireView();
        etLoginUsername = loginView.findViewById(R.id.etLoginUsername);
        etLoginPassword = loginView.findViewById(R.id.etLoginPassword);
        btnLExit = loginView.findViewById(R.id.btnLExit);
        btnLogin = loginView.findViewById(R.id.btnLogin);
        btnLSignUp = loginView.findViewById(R.id.btnLSignUp);
        btnToggleLoginPassword  = loginView.findViewById(R.id.btnToggleLoginPassword);
        LoginFragment = manager.findFragmentById(R.id.fragLogin);

        signupView = Objects.requireNonNull(manager.findFragmentById(R.id.fragSignup)).requireView();
        etSignUpUsername = signupView.findViewById(R.id.etSignUpUsername);
        etSignUpPassword = signupView.findViewById(R.id.etSignUpPassword);
        etSConfirmPassword = signupView.findViewById(R.id.etSConfirmPassword);
        btnSExit = signupView.findViewById(R.id.btnSExit);
        btnSignUp = signupView.findViewById(R.id.btnSignUp);
        btnSLogin = signupView.findViewById(R.id.btnSLogin);
        passwordMismatchError = signupView.findViewById(R.id.passwordMismatchError);
        btnTogglePassword  = signupView.findViewById(R.id.btnTogglePassword);
        btnToggleConfirmPassword  = signupView.findViewById(R.id.btnToggleConfirmPassword);
        rlConfirm = signupView.findViewById(R.id.rlConfirm);
        SignUpFragment = manager.findFragmentById(R.id.fragSignup);

        itemsDB = new ItemsDB(this);
    }

    private void togglePasswordVisibility(EditText editText) {
        int selection = editText.getSelectionEnd();
        int inputType = editText.getInputType();

        if ((inputType & InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        } else {
            editText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        }
        editText.setSelection(selection);
    }

    private void saveLoginState(String username) {
        // First, retrieve the user ID corresponding to the username from the database
        itemsDB.open();
        int userId = itemsDB.getUserIdByUsername(username);
        itemsDB.close();

        // Now, save the user ID in shared preferences
        SharedPreferences.Editor editor = sPref.edit();
        editor.putBoolean("isLoggedIn", true);
        editor.putInt("userId", userId);
        editor.putString("username", username);
        editor.putBoolean("tooltip", false);
        editor.apply();
    }

    private void startHomeActivity() {
        Intent intent = new Intent(MainActivity.this, Home.class);
        startActivity(intent);
        finish();
    }

    private boolean validateLogin(String username, String password) {
        itemsDB.open();
        int userId = itemsDB.loginUser(username, password);
        itemsDB.close();
        return userId != -1;
    }

    private void registerUser(String username, String password) {
        itemsDB.open();
        itemsDB.registerUser(username, password);
        itemsDB.close();
    }
}