package com.example.apihit;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface StudentApiService {
    @Headers("X-API-Key: my-secret-api-key-12345")
    @POST("students")
    Call<Void> addStudent(@Body Student student);
} 