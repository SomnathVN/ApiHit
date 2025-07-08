package com.example.apihit.retrofit;

import android.content.Context;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class StudentApiClient {
    private static final String BASE_URL = "https://d38df13bd80c.ngrok-free.app/";
    private static Retrofit retrofit = null;
    static Context appContext;

    public static void init(Context context) {
        appContext = context.getApplicationContext();
    }

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