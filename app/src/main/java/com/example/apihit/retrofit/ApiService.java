package com.example.apihit.retrofit;

import com.example.apihit.domain.model.NewsResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {
        @GET("everything")
        Call<NewsResponse> getNews(
                @Query("q") String query,
                //@Query("from") String fromDate,
                @Query("language") String language,
                @Query("sortBy") String sortBy,
                //@Query("country") String country,
                @Query("apiKey") String apiKey
        );

}


