package com.example.apihit;

import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StudentRepository {
    private final StudentApiService apiService;
    private final FirebaseFirestore firestore;

    public StudentRepository() {
        this.apiService = StudentApiClient.getApiService();
        this.firestore = FirebaseFirestore.getInstance();
        Log.d("FirestoreInit", "Firestore instance: " + firestore);
    }

    public void addStudent(Student student, StudentAddCallback callback) {
        apiService.addStudent(student).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // Add to Firestore (auto-generated ID)
                    Map<String, Object> studentMap = new HashMap<>();
                    studentMap.put("name", student.getName());
                    studentMap.put("email", student.getEmail());
                    studentMap.put("age", student.getAge());
                    firestore.collection("students")
                            .add(studentMap)
                            .addOnSuccessListener(documentReference -> callback.onSuccess())
                            .addOnFailureListener(e -> {
                                Log.e("FirestoreError", "Failed to add student", e); // Stack trace
                                if (e != null) {
                                    Log.e("FirestoreError", "Exception class: " + e.getClass().getName());
                                    Log.e("FirestoreError", "Exception toString: " + e.toString());
                                    Log.e("FirestoreError", "Exception message: " + e.getMessage());
                                } else {
                                    Log.e("FirestoreError", "Exception is null!");
                                }
                                callback.onFailure(e);
                            });
                    Log.d("FirestoreData", "Student map: " + studentMap);
                } else {
                    callback.onFailure(new Exception("API error: " + response.code()));
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onFailure(new Exception(t));
            }
        });
    }

    public interface StudentAddCallback {
        void onSuccess();
        void onFailure(Exception e);
    }
} 