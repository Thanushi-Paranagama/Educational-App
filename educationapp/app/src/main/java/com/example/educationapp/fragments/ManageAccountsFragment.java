package com.example.educationapp.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.educationapp.R;
import com.example.educationapp.adapters.UserAccountAdapter;
import com.example.educationapp.models.User;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ManageAccountsFragment extends Fragment implements UserAccountAdapter.OnUserActionListener {

    private RecyclerView recyclerViewUsers;
    private UserAccountAdapter userAdapter;
    private List<User> userList;
    private ProgressBar progressBar;
    private FloatingActionButton fabAddUser;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage_accounts, container, false);

        initViews(view);
        setupRecyclerView();
        setupClickListeners();
        loadUsers();

        return view;
    }

    private void initViews(View view) {
        recyclerViewUsers = view.findViewById(R.id.recyclerViewUsers);
        progressBar = view.findViewById(R.id.progressBar);
        fabAddUser = view.findViewById(R.id.fabAddUser);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Initialize user list
        userList = new ArrayList<>();
    }

    private void setupRecyclerView() {
        userAdapter = new UserAccountAdapter(userList, this);
        recyclerViewUsers.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewUsers.setAdapter(userAdapter);
    }

    private void setupClickListeners() {
        fabAddUser.setOnClickListener(v -> showAddUserDialog());
    }

    private void loadUsers() {
        showProgressBar(true);

        db.collection("users")
                .get()
                .addOnCompleteListener(task -> {
                    showProgressBar(false);
                    if (task.isSuccessful()) {
                        userList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            User user = document.toObject(User.class);
                            userList.add(user);
                        }
                        userAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getContext(), "Error loading users: " + task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showAddUserDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_user, null);

        EditText etFullName = dialogView.findViewById(R.id.etFullName);
        EditText etEmail = dialogView.findViewById(R.id.etEmail);
        EditText etPhoneNumber = dialogView.findViewById(R.id.etPhoneNumber);
        EditText etPassword = dialogView.findViewById(R.id.etPassword);
        RadioGroup rgRole = dialogView.findViewById(R.id.rgRole);

        builder.setView(dialogView)
                .setTitle("Add New User")
                .setPositiveButton("Add", null)
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();

        // Override positive button to prevent auto-dismiss on validation failure
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String fullName = etFullName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String phoneNumber = etPhoneNumber.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            int selectedRoleId = rgRole.getCheckedRadioButtonId();
            if (selectedRoleId == -1) {
                Toast.makeText(getContext(), "Please select a role", Toast.LENGTH_SHORT).show();
                return;
            }

            RadioButton selectedRole = dialogView.findViewById(selectedRoleId);
            String role = selectedRole.getText().toString().toLowerCase();

            if (validateUserInput(fullName, email, phoneNumber, password)) {
                addNewUser(fullName, email, phoneNumber, password, role);
                dialog.dismiss();
            }
        });
    }

    private boolean validateUserInput(String fullName, String email, String phoneNumber, String password) {
        if (TextUtils.isEmpty(fullName) || fullName.length() < 2) {
            Toast.makeText(getContext(), "Full name must be at least 2 characters", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(getContext(), "Please enter a valid email address", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(phoneNumber) || phoneNumber.length() < 10) {
            Toast.makeText(getContext(), "Please enter a valid phone number", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(password) || password.length() < 6) {
            Toast.makeText(getContext(), "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void addNewUser(String fullName, String email, String phoneNumber, String password, String role) {
        showProgressBar(true);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String userId = task.getResult().getUser().getUid();
                        User newUser = new User(userId, email, fullName, role, phoneNumber);

                        db.collection("users")
                                .document(userId)
                                .set(newUser)
                                .addOnSuccessListener(aVoid -> {
                                    showProgressBar(false);
                                    Toast.makeText(getContext(), "User added successfully", Toast.LENGTH_SHORT).show();
                                    loadUsers(); // Refresh the list
                                })
                                .addOnFailureListener(e -> {
                                    showProgressBar(false);
                                    Toast.makeText(getContext(), "Error saving user: " + e.getMessage(),
                                            Toast.LENGTH_SHORT).show();
                                    // Delete the Firebase Auth user if Firestore save failed
                                    task.getResult().getUser().delete();
                                });
                    } else {
                        showProgressBar(false);
                        Toast.makeText(getContext(), "Error creating user: " + task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onEditUser(User user) {
        showEditUserDialog(user);
    }

    @Override
    public void onDeleteUser(User user) {
        new AlertDialog.Builder(getContext())
                .setTitle("Delete User")
                .setMessage("Are you sure you want to delete " + user.getFullName() + "?")
                .setPositiveButton("Delete", (dialog, which) -> deleteUser(user))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showEditUserDialog(User user) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_edit_user, null);

        EditText etFullName = dialogView.findViewById(R.id.etFullName);
        EditText etPhoneNumber = dialogView.findViewById(R.id.etPhoneNumber);
        RadioGroup rgRole = dialogView.findViewById(R.id.rgRole);

        // Pre-fill with current data
        etFullName.setText(user.getFullName());
        etPhoneNumber.setText(user.getPhoneNumber());

        // Set current role
        if ("student".equals(user.getRole())) {
            rgRole.check(R.id.rbStudent);
        } else if ("teacher".equals(user.getRole())) {
            rgRole.check(R.id.rbTeacher);
        }

        builder.setView(dialogView)
                .setTitle("Edit User")
                .setPositiveButton("Update", null)
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String fullName = etFullName.getText().toString().trim();
            String phoneNumber = etPhoneNumber.getText().toString().trim();

            int selectedRoleId = rgRole.getCheckedRadioButtonId();
            if (selectedRoleId == -1) {
                Toast.makeText(getContext(), "Please select a role", Toast.LENGTH_SHORT).show();
                return;
            }

            RadioButton selectedRole = dialogView.findViewById(selectedRoleId);
            String role = selectedRole.getText().toString().toLowerCase();

            if (validateEditUserInput(fullName, phoneNumber)) {
                updateUser(user.getUserId(), fullName, phoneNumber, role);
                dialog.dismiss();
            }
        });
    }

    private boolean validateEditUserInput(String fullName, String phoneNumber) {
        if (TextUtils.isEmpty(fullName) || fullName.length() < 2) {
            Toast.makeText(getContext(), "Full name must be at least 2 characters", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(phoneNumber) || phoneNumber.length() < 10) {
            Toast.makeText(getContext(), "Please enter a valid phone number", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void updateUser(String userId, String fullName, String phoneNumber, String role) {
        showProgressBar(true);

        db.collection("users")
                .document(userId)
                .update("fullName", fullName, "phoneNumber", phoneNumber, "role", role)
                .addOnSuccessListener(aVoid -> {
                    showProgressBar(false);
                    Toast.makeText(getContext(), "User updated successfully", Toast.LENGTH_SHORT).show();
                    loadUsers(); // Refresh the list
                })
                .addOnFailureListener(e -> {
                    showProgressBar(false);
                    Toast.makeText(getContext(), "Error updating user: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void deleteUser(User user) {
        showProgressBar(true);

        db.collection("users")
                .document(user.getUserId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    showProgressBar(false);
                    Toast.makeText(getContext(), "User deleted successfully", Toast.LENGTH_SHORT).show();
                    loadUsers(); // Refresh the list
                })
                .addOnFailureListener(e -> {
                    showProgressBar(false);
                    Toast.makeText(getContext(), "Error deleting user: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void showProgressBar(boolean show) {
        if (progressBar != null) {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }
}