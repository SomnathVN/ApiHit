package com.example.apihit.database;

import android.util.Log;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface NewsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    default void insertNews(List<NewsEntity> newsList) {
        Log.d("ROOM_DB", "Inserting " + newsList.size() + " articles");
    }

    @Query("SELECT * FROM news_table ORDER BY publishedAt DESC")
    List<NewsEntity> getAllNews();

    @Query("DELETE FROM news_table")
    void deleteAllNews();
}

