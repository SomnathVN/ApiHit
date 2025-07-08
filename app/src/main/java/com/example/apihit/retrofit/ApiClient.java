    package com.example.apihit.retrofit;

    import retrofit2.Retrofit;
    import retrofit2.converter.gson.GsonConverterFactory;

    public class ApiClient {
        private static final String BASE_URL = "https://newsapi.org/v2/"; // Change this to your API base URL
        private static Retrofit retrofit = null;

        public static ApiService getApiService() {
            if (retrofit == null) {
                retrofit = new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create()) // Convert JSON
                        .build();
            }
            return retrofit.create(ApiService.class);
        }
    }

