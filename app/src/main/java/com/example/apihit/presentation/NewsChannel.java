package com.example.apihit.presentation;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.apihit.R;
import com.example.apihit.data.NewsRepository;
import com.example.apihit.database.NewsEntity;
import com.example.apihit.preferences.SharedPrefs;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;
import android.graphics.Color;
import android.os.Build;

public class NewsChannel extends AppCompatActivity {

    private RecyclerView recyclerView;
    private NewsAdapter adapter;
    private List<NewsEntity> newsList;
    private ProgressBar progressBar;
    private NewsRepository newsRepository;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_news_channel);

        // Make system bars transparent for edge-to-edge
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.TRANSPARENT);
            getWindow().setNavigationBarColor(Color.TRANSPARENT);
        }

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Add top padding to toolbar for system bars
        ViewCompat.setOnApplyWindowInsetsListener(toolbar, (v, insets) -> {
            int topInset = insets.getInsets(WindowInsetsCompat.Type.systemBars()).top;
            v.setPadding(0, topInset, 0, 0);
            return insets;
        });

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        progressBar = findViewById(R.id.progressBar);

        newsList = new ArrayList<>();
        adapter = new NewsAdapter(this, newsList);
        recyclerView.setAdapter(adapter);

        newsRepository = new NewsRepository(getApplicationContext());

        // Set up navigation drawer item selection
        navigationView.setNavigationItemSelectedListener(this::onNavigationItemSelected);

        // Set username in drawer header (replace with real username if available)
        View headerView = navigationView.getHeaderView(0);
        TextView usernameTextView = headerView.findViewById(R.id.textViewUsername);
        String userName = SharedPrefs.getUsername(getApplicationContext());
        usernameTextView.setText(userName);

        loadCachedNews(); // Load stored news first
        fetchNews();      // Fetch new news from API
    }

    private boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_info) {
            replaceFragment(new InfoFragment());
        } else if (id == R.id.nav_add_news) {
            replaceFragment(new AddNewsFragment());
        } else if (id == R.id.nav_about_us) {
            replaceFragment(new AboutUsFragment());
        } else if (id == R.id.nav_logout) {
            showLogoutDialog();
        } else if (id == R.id.nav_copyright) {
            // Do nothing for now
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Log Out")
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    SharedPrefs.clearToken(getApplicationContext());
                    SharedPrefs.clearUsername(getApplicationContext());
                    Intent intent = new Intent(NewsChannel.this, Loginscreen.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                    Log.d("SharedePreferences",null + SharedPrefs.getUsername(getApplicationContext()));
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void loadCachedNews() {
        newsRepository.getCachedNews(new NewsRepository.NewsCallback() {
            @Override
            public void onSuccess(List<NewsEntity> cachedNews) {
                runOnUiThread(() -> {
                    newsList.clear();
                    newsList.addAll(cachedNews);
                    adapter.notifyDataSetChanged();
                    Log.d("NewsChannel", "Loaded cached news: " + cachedNews.size());
                });
            }
            @Override
            public void onFailure(Exception e) {
                runOnUiThread(() -> {
                    Log.e("NewsChannel", "Failed to load cached news", e);
                    Toast.makeText(NewsChannel.this, "Failed to load cached news", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void fetchNews() {
        progressBar.setVisibility(View.VISIBLE);
        newsRepository.fetchNewsFromApi("maharashtra", "en", "publishedAt", new NewsRepository.NewsCallback() {
            @Override
            public void onSuccess(List<NewsEntity> newNewsList) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    newsList.clear();
                    newsList.addAll(newNewsList);
                    adapter.notifyDataSetChanged();
                    Log.d("NewsChannel", "Fetched news from API: " + newNewsList.size());
                });
            }
            @Override
            public void onFailure(Exception e) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    Log.e("NewsChannel", "Failed to fetch news from API", e);
                    Toast.makeText(NewsChannel.this, "Failed to fetch news from API", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
}