package com.example.apihit;

import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.internal.EverythingIsNonNull;

public class StudentRepository {
    private final StudentApiService apiService;
    private final FirebaseFirestore firestore;

    public StudentRepository() {
        this.apiService = StudentApiClient.getApiService();
        this.firestore = FirebaseFirestore.getInstance();
        Log.d("FirestoreInit", "Firestore instance: " + firestore);
    }

    public void addStudent(Student student, StudentAddCallback callback) {
        apiService.addStudent(student).enqueue(new Callback<>() {
            @EverythingIsNonNull
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
                                callback.onFailure(e);
                            });
                    Log.d("FirestoreData", "Student map: " + studentMap);
                } else {
                    callback.onFailure(new Exception("API error: " + response.code()));
                }
            }
            @okhttp3.internal.annotations.EverythingIsNonNull
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