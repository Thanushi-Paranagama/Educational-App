package com.example.educationapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.educationapp.fragments.AssignStudentsFragment;
import com.example.educationapp.fragments.CourseMaterialsFragment;
import com.example.educationapp.fragments.ManageAccountsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AdminDashboardActivity extends AppCompatActivity {
    private static final String TAG = "AdminDashboard";

    private BottomNavigationView bottomNavigation;
    private MaterialButton btnLogout;
    private FirebaseAuth mAuth;

    // Fragment instances
    private ManageAccountsFragment manageAccountsFragment;
    private AssignStudentsFragment assignStudentsFragment;
    private CourseMaterialsFragment courseMaterialsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        Log.d(TAG, "onCreate called");

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Check if user is logged in
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            redirectToLogin();
            return;
        }

        initViews();
        setupBottomNavigation();
        setupClickListeners();

        // Show default fragment
        if (savedInstanceState == null) {
            showFragment(getManageAccountsFragment());
        }
    }

    private void initViews() {
        bottomNavigation = findViewById(R.id.bottomNavigation);
        btnLogout = findViewById(R.id.btnLogout);
    }

    private void setupBottomNavigation() {
        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_manage_accounts) {
                showFragment(getManageAccountsFragment());
                return true;
            } else if (itemId == R.id.nav_assign_students) {
                showFragment(getAssignStudentsFragment());
                return true;
            } else if (itemId == R.id.nav_course_materials) {
                showFragment(getCourseMaterialsFragment());
                return true;
            }

            return false;
        });
    }

    private void setupClickListeners() {
        btnLogout.setOnClickListener(v -> {
            Log.d(TAG, "Logout button clicked");
            logoutUser();
        });
    }

    private void showFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainer, fragment);
        fragmentTransaction.commit();
    }

    private ManageAccountsFragment getManageAccountsFragment() {
        if (manageAccountsFragment == null) {
            manageAccountsFragment = new ManageAccountsFragment();
        }
        return manageAccountsFragment;
    }

    private AssignStudentsFragment getAssignStudentsFragment() {
        if (assignStudentsFragment == null) {
            assignStudentsFragment = new AssignStudentsFragment();
        }
        return assignStudentsFragment;
    }

    private CourseMaterialsFragment getCourseMaterialsFragment() {
        if (courseMaterialsFragment == null) {
            courseMaterialsFragment = new CourseMaterialsFragment();
        }
        return courseMaterialsFragment;
    }

    private void logoutUser() {
        try {
            mAuth.signOut();
            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
            redirectToLogin();
        } catch (Exception e) {
            Log.e(TAG, "Error during logout: " + e.getMessage());
            Toast.makeText(this, "Error during logout", Toast.LENGTH_SHORT).show();
        }
    }

    private void redirectToLogin() {
        Intent intent = new Intent(AdminDashboardActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy called");
    }
}