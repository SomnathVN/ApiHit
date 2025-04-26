package com.example.apihit;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private NewsAdapter adapter;
    private List<NewsEntity> newsList;
    private ProgressBar progressBar;
    private NewsDatabase newsDatabase;

    private static final String API_KEY = "1a89e091b05f44239cda5614187b75cc";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        progressBar = findViewById(R.id.progressBar);

        newsList = new ArrayList<>();
        adapter = new NewsAdapter(this, newsList);
        recyclerView.setAdapter(adapter);

        newsDatabase = NewsDatabase.getDatabase(this);
        AsyncTask.execute(() -> {
            List<NewsEntity> cachedNews = newsDatabase.newsDao().getAllNews();
            Log.d("DATABASE_CHECK", "Total articles in DB: " + cachedNews.size());
        });


        loadCachedNews(); // Load stored news first
        fetchNews();      // Fetch new news from API
    }

    private void loadCachedNews() {
        AsyncTask.execute(() -> {
            List<NewsEntity> cachedNews = newsDatabase.newsDao().getAllNews();
            Log.d("DATABASE", "Cached news count: " + cachedNews.size());
            for (NewsEntity news : cachedNews) {
                Log.d("DATABASE_ENTRY", "Title: " + news.getTitle() + ", Source: " + news.getSource());
            }
            if (!cachedNews.isEmpty()) {
                runOnUiThread(() -> {
                    newsList.clear();
                    newsList.addAll(cachedNews);
                    adapter.notifyDataSetChanged();
                });
            }
        });
    }

    private void fetchNews() {
        progressBar.setVisibility(View.VISIBLE);

        ApiService apiService = ApiClient.getApiService();

        Log.d("API_CALL", "Making API request...");

        Call<NewsResponse> call = apiService.getNews("maharashtra", "en", "publishedAt", API_KEY);

        call.enqueue(new Callback<NewsResponse>() {
            @Override
            public void onResponse(Call<NewsResponse> call, Response<NewsResponse> response) {
                progressBar.setVisibility(View.GONE);
                Log.d("API_CALL", "API Response received");

                if (response.isSuccessful() && response.body() != null) {
                    Log.d("API_RESPONSE", "Articles count: " + response.body().getArticles().size());

                    List<NewsEntity> newNewsList = new ArrayList<>();
                    for (NewsArticle article : response.body().getArticles()) {
                        Log.d("ARTICLE", "Title: " + article.getTitle());

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
                } else {
                    Log.e("API_ERROR", "Error: " + response.message());
                    Toast.makeText(MainActivity.this, "Error: " + response.message(), Toast.LENGTH_LONG).show();
                }
            }


            @Override
            public void onFailure(Call<NewsResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void saveNewsToDatabase(List<NewsEntity> newNewsList) {
        AsyncTask.execute(() -> {
            newsDatabase.newsDao().deleteAllNews(); // Clear old data
            newsDatabase.newsDao().insertNews(newNewsList); // Insert new data

            List<NewsEntity> storedNews = newsDatabase.newsDao().getAllNews();
            Log.d("DATABASE", "Stored news count after insert: " + storedNews.size());

            runOnUiThread(() -> {
                newsList.clear();
                newsList.addAll(newNewsList);
                adapter.notifyDataSetChanged();
            });
        });
    }
}
