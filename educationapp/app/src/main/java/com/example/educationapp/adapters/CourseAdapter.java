package com.example.educationapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.educationapp.R;
import com.example.educationapp.models.Course;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder> {

    private List<Course> courseList;
    private OnCourseActionListener listener;

    public interface OnCourseActionListener {
        void onAssignStudents(Course course);
        void onViewStudents(Course course);
        void onEditCourse(Course course);
        void onDeleteCourse(Course course);
    }

    public CourseAdapter(List<Course> courseList, OnCourseActionListener listener) {
        this.courseList = courseList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_course, parent, false);
        return new CourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        Course course = courseList.get(position);
        holder.bind(course);
    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }

    class CourseViewHolder extends RecyclerView.ViewHolder {
        private TextView tvCourseName, tvCourseCode, tvDescription, tvTeacherName, tvStudentCount, tvCreatedAt;
        private ImageView ivAssignStudents, ivViewStudents, ivEdit, ivDelete;

        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCourseName = itemView.findViewById(R.id.tvCourseName);
            tvCourseCode = itemView.findViewById(R.id.tvCourseCode);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvTeacherName = itemView.findViewById(R.id.tvTeacherName);
            tvStudentCount = itemView.findViewById(R.id.tvStudentCount);
            tvCreatedAt = itemView.findViewById(R.id.tvCreatedAt);
            ivAssignStudents = itemView.findViewById(R.id.ivAssignStudents);
            ivViewStudents = itemView.findViewById(R.id.ivViewStudents);
            ivEdit = itemView.findViewById(R.id.ivEdit);
            ivDelete = itemView.findViewById(R.id.ivDelete);
        }

        public void bind(Course course) {
            tvCourseName.setText(course.getCourseName());
            tvCourseCode.setText(course.getCourseCode());
            tvDescription.setText(course.getDescription());
            tvTeacherName.setText("Teacher: " + course.getTeacherName());

            // Display student count
            int studentCount = course.getEnrolledStudents() != null ? course.getEnrolledStudents().size() : 0;
            tvStudentCount.setText("Students: " + studentCount);

            // Format and display created date
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
            String formattedDate = sdf.format(new Date(course.getCreatedAt()));
            tvCreatedAt.setText("Created: " + formattedDate);

            // Set click listeners
            ivAssignStudents.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onAssignStudents(course);
                }
            });

            ivViewStudents.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onViewStudents(course);
                }
            });

            ivEdit.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEditCourse(course);
                }
            });

            ivDelete.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteCourse(course);
                }
            });
        }
    }
}