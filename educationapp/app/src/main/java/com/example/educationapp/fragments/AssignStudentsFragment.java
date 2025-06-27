package com.example.educationapp.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.educationapp.R;
import com.example.educationapp.adapters.CourseAdapter;
import com.example.educationapp.models.Course;
import com.example.educationapp.models.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AssignStudentsFragment extends Fragment implements CourseAdapter.OnCourseActionListener {

    private RecyclerView recyclerViewCourses;
    private CourseAdapter courseAdapter;
    private List<Course> courseList;
    private ProgressBar progressBar;
    private FloatingActionButton fabAddCourse;

    private FirebaseFirestore db;
    private List<User> teacherList;
    private List<User> studentList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_assign_students, container, false);

        initViews(view);
        setupRecyclerView();
        setupClickListeners();
        loadData();

        return view;
    }

    private void initViews(View view) {
        recyclerViewCourses = view.findViewById(R.id.recyclerViewCourses);
        progressBar = view.findViewById(R.id.progressBar);
        fabAddCourse = view.findViewById(R.id.fabAddCourse);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();

        // Initialize lists
        courseList = new ArrayList<>();
        teacherList = new ArrayList<>();
        studentList = new ArrayList<>();
    }

    private void setupRecyclerView() {
        courseAdapter = new CourseAdapter(courseList, this);
        recyclerViewCourses.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewCourses.setAdapter(courseAdapter);
    }

    private void setupClickListeners() {
        fabAddCourse.setOnClickListener(v -> showAddCourseDialog());
    }

    private void loadData() {
        loadTeachers();
        loadStudents();
        loadCourses();
    }

    private void loadTeachers() {
        db.collection("users")
                .whereEqualTo("role", "teacher")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        teacherList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            User teacher = document.toObject(User.class);
                            teacherList.add(teacher);
                        }
                    }
                });
    }

    private void loadStudents() {
        db.collection("users")
                .whereEqualTo("role", "student")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        studentList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            User student = document.toObject(User.class);
                            studentList.add(student);
                        }
                    }
                });
    }

    private void loadCourses() {
        showProgressBar(true);

        db.collection("courses")
                .get()
                .addOnCompleteListener(task -> {
                    showProgressBar(false);
                    if (task.isSuccessful()) {
                        courseList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Course course = document.toObject(Course.class);
                            courseList.add(course);
                        }
                        courseAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getContext(), "Error loading courses: " + task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showAddCourseDialog() {
        if (teacherList.isEmpty()) {
            Toast.makeText(getContext(), "No teachers available. Add teachers first.", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_course, null);

        EditText etCourseName = dialogView.findViewById(R.id.etCourseName);
        EditText etCourseCode = dialogView.findViewById(R.id.etCourseCode);
        EditText etDescription = dialogView.findViewById(R.id.etDescription);
        Spinner spinnerTeacher = dialogView.findViewById(R.id.spinnerTeacher);

        // Setup teacher spinner
        List<String> teacherNames = new ArrayList<>();
        for (User teacher : teacherList) {
            teacherNames.add(teacher.getFullName());
        }
        ArrayAdapter<String> teacherAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, teacherNames);
        teacherAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTeacher.setAdapter(teacherAdapter);

        builder.setView(dialogView)
                .setTitle("Add New Course")
                .setPositiveButton("Add", null)
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String courseName = etCourseName.getText().toString().trim();
            String courseCode = etCourseCode.getText().toString().trim();
            String description = etDescription.getText().toString().trim();

            if (validateCourseInput(courseName, courseCode, description)) {
                User selectedTeacher = teacherList.get(spinnerTeacher.getSelectedItemPosition());
                addNewCourse(courseName, courseCode, description, selectedTeacher);
                dialog.dismiss();
            }
        });
    }

    private boolean validateCourseInput(String courseName, String courseCode, String description) {
        if (TextUtils.isEmpty(courseName)) {
            Toast.makeText(getContext(), "Course name is required", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(courseCode)) {
            Toast.makeText(getContext(), "Course code is required", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(description)) {
            Toast.makeText(getContext(), "Description is required", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void addNewCourse(String courseName, String courseCode, String description, User teacher) {
        showProgressBar(true);

        String courseId = db.collection("courses").document().getId();
        Course newCourse = new Course(courseId, courseName, courseCode, description,
                teacher.getUserId(), teacher.getFullName());

        db.collection("courses")
                .document(courseId)
                .set(newCourse)
                .addOnSuccessListener(aVoid -> {
                    showProgressBar(false);
                    Toast.makeText(getContext(), "Course added successfully", Toast.LENGTH_SHORT).show();
                    loadCourses(); // Refresh the list
                })
                .addOnFailureListener(e -> {
                    showProgressBar(false);
                    Toast.makeText(getContext(), "Error adding course: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onAssignStudents(Course course) {
        showAssignStudentsDialog(course);
    }

    @Override
    public void onViewStudents(Course course) {
        showViewStudentsDialog(course);
    }

    @Override
    public void onEditCourse(Course course) {
        showEditCourseDialog(course);
    }

    @Override
    public void onDeleteCourse(Course course) {
        new AlertDialog.Builder(getContext())
                .setTitle("Delete Course")
                .setMessage("Are you sure you want to delete " + course.getCourseName() + "?")
                .setPositiveButton("Delete", (dialog, which) -> deleteCourse(course))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showAssignStudentsDialog(Course course) {
        if (studentList.isEmpty()) {
            Toast.makeText(getContext(), "No students available", Toast.LENGTH_SHORT).show();
            return;
        }

        List<String> studentNames = new ArrayList<>();
        boolean[] checkedStudents = new boolean[studentList.size()];

        for (int i = 0; i < studentList.size(); i++) {
            User student = studentList.get(i);
            studentNames.add(student.getFullName() + " (" + student.getEmail() + ")");

            // Check if student is already enrolled
            if (course.getEnrolledStudents() != null &&
                    course.getEnrolledStudents().contains(student.getUserId())) {
                checkedStudents[i] = true;
            }
        }

        new AlertDialog.Builder(getContext())
                .setTitle("Assign Students to " + course.getCourseName())
                .setMultiChoiceItems(studentNames.toArray(new String[0]), checkedStudents,
                        (dialog, which, isChecked) -> checkedStudents[which] = isChecked)
                .setPositiveButton("Assign", (dialog, which) -> {
                    List<String> selectedStudentIds = new ArrayList<>();
                    for (int i = 0; i < checkedStudents.length; i++) {
                        if (checkedStudents[i]) {
                            selectedStudentIds.add(studentList.get(i).getUserId());
                        }
                    }
                    updateCourseEnrollments(course, selectedStudentIds);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showViewStudentsDialog(Course course) {
        if (course.getEnrolledStudents() == null || course.getEnrolledStudents().isEmpty()) {
            Toast.makeText(getContext(), "No students enrolled in this course", Toast.LENGTH_SHORT).show();
            return;
        }

        StringBuilder studentsInfo = new StringBuilder();
        for (String studentId : course.getEnrolledStudents()) {
            // Find student by ID
            for (User student : studentList) {
                if (student.getUserId().equals(studentId)) {
                    studentsInfo.append("• ").append(student.getFullName())
                            .append(" (").append(student.getEmail()).append(")\n");
                    break;
                }
            }
        }

        new AlertDialog.Builder(getContext())
                .setTitle("Students in " + course.getCourseName())
                .setMessage(studentsInfo.toString())
                .setPositiveButton("OK", null)
                .show();
    }

    private void showEditCourseDialog(Course course) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_edit_course, null);

        EditText etCourseName = dialogView.findViewById(R.id.etCourseName);
        EditText etCourseCode = dialogView.findViewById(R.id.etCourseCode);
        EditText etDescription = dialogView.findViewById(R.id.etDescription);
        Spinner spinnerTeacher = dialogView.findViewById(R.id.spinnerTeacher);

        // Pre-fill with current data
        etCourseName.setText(course.getCourseName());
        etCourseCode.setText(course.getCourseCode());
        etDescription.setText(course.getDescription());

        // Setup teacher spinner
        List<String> teacherNames = new ArrayList<>();
        int selectedTeacherIndex = 0;
        for (int i = 0; i < teacherList.size(); i++) {
            User teacher = teacherList.get(i);
            teacherNames.add(teacher.getFullName());
            if (teacher.getUserId().equals(course.getTeacherId())) {
                selectedTeacherIndex = i;
            }
        }
        ArrayAdapter<String> teacherAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, teacherNames);
        teacherAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTeacher.setAdapter(teacherAdapter);
        spinnerTeacher.setSelection(selectedTeacherIndex);

        builder.setView(dialogView)
                .setTitle("Edit Course")
                .setPositiveButton("Update", null)
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String courseName = etCourseName.getText().toString().trim();
            String courseCode = etCourseCode.getText().toString().trim();
            String description = etDescription.getText().toString().trim();

            if (validateCourseInput(courseName, courseCode, description)) {
                User selectedTeacher = teacherList.get(spinnerTeacher.getSelectedItemPosition());
                updateCourse(course.getCourseId(), courseName, courseCode, description, selectedTeacher);
                dialog.dismiss();
            }
        });
    }

    private void updateCourseEnrollments(Course course, List<String> studentIds) {
        showProgressBar(true);

        db.collection("courses")
                .document(course.getCourseId())
                .update("enrolledStudents", studentIds)
                .addOnSuccessListener(aVoid -> {
                    showProgressBar(false);
                    Toast.makeText(getContext(), "Students assigned successfully", Toast.LENGTH_SHORT).show();
                    loadCourses(); // Refresh the list
                })
                .addOnFailureListener(e -> {
                    showProgressBar(false);
                    Toast.makeText(getContext(), "Error assigning students: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void updateCourse(String courseId, String courseName, String courseCode,
                              String description, User teacher) {
        showProgressBar(true);

        db.collection("courses")
                .document(courseId)
                .update("courseName", courseName, "courseCode", courseCode,
                        "description", description, "teacherId", teacher.getUserId(),
                        "teacherName", teacher.getFullName())
                .addOnSuccessListener(aVoid -> {
                    showProgressBar(false);
                    Toast.makeText(getContext(), "Course updated successfully", Toast.LENGTH_SHORT).show();
                    loadCourses(); // Refresh the list
                })
                .addOnFailureListener(e -> {
                    showProgressBar(false);
                    Toast.makeText(getContext(), "Error updating course: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void deleteCourse(Course course) {
        showProgressBar(true);

        db.collection("courses")
                .document(course.getCourseId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    showProgressBar(false);
                    Toast.makeText(getContext(), "Course deleted successfully", Toast.LENGTH_SHORT).show();
                    loadCourses(); // Refresh the list
                })
                .addOnFailureListener(e -> {
                    showProgressBar(false);
                    Toast.makeText(getContext(), "Error deleting course: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void showProgressBar(boolean show) {
        if (progressBar != null) {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }
}