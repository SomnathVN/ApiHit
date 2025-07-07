package com.example.apihit;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class StudentApiClient {
    private static final String BASE_URL = "https://4e44-49-36-97-66.ngrok-free.app/api/";
    private static Retrofit retrofit = null;

    public static StudentApiService getApiService() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(StudentApiService.class);
    }
} 