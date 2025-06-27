package com.example.educationapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private TextInputEditText etEmail, etPassword;
    private MaterialButton btnLogin;
    private TextView tvRegister;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "onCreate called");

        // Initialize Firebase Auth and Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        initViews();
        setupClickListeners();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart called");

        // Check if user is already signed in
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Log.d(TAG, "User already logged in, redirecting to dashboard");
            // User is already logged in, redirect to dashboard
            redirectToDashboard();
        }
    }

    private void initViews() {
        Log.d(TAG, "Initializing views");

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);
        progressBar = findViewById(R.id.progressBar);

        // Add null checks with logging
        if (etEmail == null) {
            Log.e(TAG, "etEmail is null - check your layout file");
        }
        if (etPassword == null) {
            Log.e(TAG, "etPassword is null - check your layout file");
        }
        if (btnLogin == null) {
            Log.e(TAG, "btnLogin is null - check your layout file");
        }
        if (tvRegister == null) {
            Log.e(TAG, "tvRegister is null - check your layout file");
        } else {
            Log.d(TAG, "tvRegister found successfully");
        }
        if (progressBar == null) {
            Log.e(TAG, "progressBar is null - check your layout file");
        }
    }

    private void setupClickListeners() {
        Log.d(TAG, "Setting up click listeners");

        if (btnLogin != null) {
            btnLogin.setOnClickListener(v -> {
                Log.d(TAG, "Login button clicked");
                loginUser();
            });
        }

        if (tvRegister != null) {
            tvRegister.setOnClickListener(v -> {
                Log.d(TAG, "Register TextView clicked - attempting to start RegisterActivity");
                Toast.makeText(MainActivity.this, "Register clicked!", Toast.LENGTH_SHORT).show();

                try {
                    Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                    Log.d(TAG, "Intent created, starting RegisterActivity");
                    startActivity(intent);
                    Log.d(TAG, "RegisterActivity started successfully");
                } catch (Exception e) {
                    Log.e(TAG, "Error starting RegisterActivity: " + e.getMessage());
                    Toast.makeText(MainActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
            Log.d(TAG, "Register click listener set successfully");
        } else {
            Log.e(TAG, "tvRegister is null, cannot set click listener");
        }
    }

    private void loginUser() {
        Log.d(TAG, "loginUser called");

        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validate input
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email is required");
            etEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password is required");
            etPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            etPassword.setError("Password must be at least 6 characters");
            etPassword.requestFocus();
            return;
        }

        // Show progress bar
        showProgressBar(true);

        // Sign in with Firebase Auth
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    showProgressBar(false);

                    if (task.isSuccessful()) {
                        // Login successful
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            Toast.makeText(MainActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                            redirectToDashboard();
                        }
                    } else {
                        // Login failed
                        String errorMessage = "Authentication failed.";
                        if (task.getException() != null) {
                            errorMessage = task.getException().getMessage();
                        }
                        Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void redirectToDashboard() {
        Log.d(TAG, "redirectToDashboard called");

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // Get user role from Firestore to determine which dashboard to show
            db.collection("users")
                    .document(currentUser.getUid())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String role = documentSnapshot.getString("role");
                            Intent intent;

                            switch (role != null ? role : "student") {
                                case "admin":
                                    intent = new Intent(MainActivity.this, AdminDashboardActivity.class);
                                    break;
                                case "teacher":
                                    intent = new Intent(MainActivity.this, TeacherDashboardActivity.class);
                                    break;
                                default:
                                    intent = new Intent(MainActivity.this, StudentDashboardActivity.class);
                                    break;
                            }

                            startActivity(intent);
                            finish(); // Close login activity
                        } else {
                            Toast.makeText(this, "User data not found", Toast.LENGTH_SHORT).show();
                            mAuth.signOut(); // Sign out if user data doesn't exist
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error loading user data: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                        mAuth.signOut();
                    });
        }
    }

    private void showProgressBar(boolean show) {
        if (progressBar != null) {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
        if (btnLogin != null) {
            btnLogin.setEnabled(!show);
        }
    }
}