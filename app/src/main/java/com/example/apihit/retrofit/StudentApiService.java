package com.example.apihit.retrofit;

import com.example.apihit.domain.model.Student;
import com.example.apihit.domain.model.StudentDataModel;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import java.util.Map;
import retrofit2.http.HeaderMap;

public interface StudentApiService {

    @POST("api/students")
    Call<Void> addStudent(
        @HeaderMap Map<String, String> headers,
        @Body Student student
    );

    @POST("login")
    Call<StudentDataModel.LoginResponse> login(
        @HeaderMap Map<String, String> headers,
        @Body StudentDataModel.LoginRequest request
    );
} 