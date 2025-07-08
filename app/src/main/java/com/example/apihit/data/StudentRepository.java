package com.example.apihit.data;

import android.content.Context;
import android.util.Log;

import com.example.apihit.preferences.SharedPrefs;
import com.example.apihit.domain.model.Student;
import com.example.apihit.retrofit.StudentApiClient;
import com.example.apihit.retrofit.StudentApiService;
import com.example.apihit.domain.model.StudentDataModel;
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
    private final Context context;

    public StudentRepository(Context context) {
        this.apiService = StudentApiClient.getApiService();
        this.firestore = FirebaseFirestore.getInstance();
        this.context = context.getApplicationContext();
        Log.d("FirestoreInit", "Firestore instance: " + firestore);
    }
    

    public void addStudent(Student student, StudentAddCallback callback) {
        String token = SharedPrefs.getToken(context);
        Map<String, String> headers = new HashMap<>();
        if (token != null && !token.isEmpty()) {
            headers.put("Authorization", "Bearer " + token);
        }
        headers.put("X-API-Key", "my-secret-api-key-12345");
        Log.d("AddStudent", "Headers: " + headers);
        apiService.addStudent(headers, student).enqueue(new Callback<>() {
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
                    Log.e("AddStudent", "API error: " + response.code());
                    callback.onFailure(new Exception("API error: " + response.code()));
                }
            }
            @okhttp3.internal.annotations.EverythingIsNonNull
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("AddStudent", "API call failed", t);
                callback.onFailure(new Exception(t));
            }
        });
    }

    public void login(String username, String password, LoginCallback callback) {
        Log.d("username,password",username + password);
        Map<String, String> headers = new HashMap<>();
        headers.put("X-API-Key", "my-secret-api-key-12345");
        apiService.login(headers, new StudentDataModel.LoginRequest(username, password)).enqueue(new Callback<StudentDataModel.LoginResponse>() {
            @Override
            public void onResponse(Call<StudentDataModel.LoginResponse> call, Response<StudentDataModel.LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String token = response.body().getToken();
                    SharedPrefs.saveToken(context, token);
                    callback.onSuccess();
                } else {
                    callback.onFailure(new Exception("Login failed: " + response.code()));
                }
            }
            @Override
            public void onFailure(Call<StudentDataModel.LoginResponse> call, Throwable t) {
                callback.onFailure(new Exception(t));
            }
        });
    }

    public interface StudentAddCallback {
        void onSuccess();
        void onFailure(Exception e);
    }

    public interface LoginCallback {
        void onSuccess();
        void onFailure(Exception e);
    }
} 