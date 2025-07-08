package com.example.apihit.data;

import android.content.Context;
import android.util.Log;

import com.example.apihit.retrofit.ApiClient;
import com.example.apihit.retrofit.ApiService;
import com.example.apihit.domain.model.NewsArticle;
import com.example.apihit.database.NewsDao;
import com.example.apihit.database.NewsDatabase;
import com.example.apihit.database.NewsEntity;
import com.example.apihit.domain.model.NewsResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewsRepository {
    private final ApiService apiService;
    private final NewsDao newsDao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private static final String API_KEY = "1a89e091b05f44239cda5614187b75cc";

    public NewsRepository(Context context) {
        this.apiService = ApiClient.getApiService();
        this.newsDao = NewsDatabase.getDatabase(context).newsDao();
    }

    public interface NewsCallback {
        void onSuccess(List<NewsEntity> newsList);
        void onFailure(Exception e);
    }

    public void fetchNewsFromApi(String query, String language, String sortBy, NewsCallback callback) {
        Log.d("NewsRepository", "Fetching news from API...");
        apiService.getNews(query, language, sortBy, API_KEY).enqueue(new Callback<NewsResponse>() {
            @Override
            public void onResponse(Call<NewsResponse> call, Response<NewsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<NewsEntity> newNewsList = new ArrayList<>();
                    for (NewsArticle article : response.body().getArticles()) {
                        newNewsList.add(new NewsEntity(
                                article.getTitle(),
                                article.getSource().getName(),
                                article.getAuthor(),
                                article.getDescription(),
                                article.getUrl(),
                                article.getUrlToImage(),
                                article.getPublishedAt()
                        ));
                    }
                    saveNewsToDatabase(newNewsList);
                    callback.onSuccess(newNewsList);
                } else {
                    Log.e("NewsRepository", "API error: " + response.message());
                    callback.onFailure(new Exception("API error: " + response.message()));
                }
            }
            @Override
            public void onFailure(Call<NewsResponse> call, Throwable t) {
                Log.e("NewsRepository", "API call failed", t);
                callback.onFailure(new Exception(t));
            }
        });
    }

    public void getCachedNews(NewsCallback callback) {
        executor.execute(() -> {
            try {
                List<NewsEntity> cachedNews = newsDao.getAllNews();
                Log.d("NewsRepository", "Cached news count: " + cachedNews.size());
                callback.onSuccess(cachedNews);
            } catch (Exception e) {
                Log.e("NewsRepository", "Error fetching cached news", e);
                callback.onFailure(e);
            }
        });
    }

    public void saveNewsToDatabase(List<NewsEntity> newsList) {
        executor.execute(() -> {
            try {
                newsDao.deleteAllNews();
                newsDao.insertNews(newsList);
                Log.d("NewsRepository", "Saved news to database. Count: " + newsList.size());
            } catch (Exception e) {
                Log.e("NewsRepository", "Error saving news to database", e);
            }
        });
    }
} 