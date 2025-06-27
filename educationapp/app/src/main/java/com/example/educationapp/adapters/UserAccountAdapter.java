package com.example.educationapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.educationapp.R;
import com.example.educationapp.models.User;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class UserAccountAdapter extends RecyclerView.Adapter<UserAccountAdapter.UserViewHolder> {

    private List<User> userList;
    private OnUserActionListener listener;

    public interface OnUserActionListener {
        void onEditUser(User user);
        void onDeleteUser(User user);
    }

    public UserAccountAdapter(List<User> userList, OnUserActionListener listener) {
        this.userList = userList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_account, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.bind(user);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    class UserViewHolder extends RecyclerView.ViewHolder {
        private TextView tvFullName, tvEmail, tvRole, tvPhoneNumber, tvCreatedAt;
        private ImageView ivEdit, ivDelete, ivRoleIcon;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFullName = itemView.findViewById(R.id.tvFullName);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvRole = itemView.findViewById(R.id.tvRole);
            tvPhoneNumber = itemView.findViewById(R.id.tvPhoneNumber);
            tvCreatedAt = itemView.findViewById(R.id.tvCreatedAt);
            ivEdit = itemView.findViewById(R.id.ivEdit);
            ivDelete = itemView.findViewById(R.id.ivDelete);
            ivRoleIcon = itemView.findViewById(R.id.ivRoleIcon);
        }

        public void bind(User user) {
            tvFullName.setText(user.getFullName());
            tvEmail.setText(user.getEmail());
            tvRole.setText(user.getRole().toUpperCase());
            tvPhoneNumber.setText(user.getPhoneNumber());

            // Format and display created date
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
            String formattedDate = sdf.format(new Date(user.getCreatedAt()));
            tvCreatedAt.setText("Created: " + formattedDate);

            // Set role icon
            switch (user.getRole().toLowerCase()) {
                case "admin":
                    ivRoleIcon.setImageResource(R.drawable.ic_admin);
                    break;
                case "teacher":
                    ivRoleIcon.setImageResource(R.drawable.ic_teacher);
                    break;
                case "student":
                    ivRoleIcon.setImageResource(R.drawable.ic_student);
                    break;
                default:
                    ivRoleIcon.setImageResource(R.drawable.ic_person);
                    break;
            }

            // Set click listeners
            ivEdit.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEditUser(user);
                }
            });

            ivDelete.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteUser(user);
                }
            });
        }
    }
}