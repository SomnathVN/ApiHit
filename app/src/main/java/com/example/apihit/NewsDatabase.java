package com.example.apihit;

import android.content.Context;
import android.util.Log;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {NewsEntity.class}, version = 1, exportSchema = false)
public abstract class NewsDatabase extends RoomDatabase {
    private static volatile NewsDatabase INSTANCE;

    public abstract NewsDao newsDao();

    public static NewsDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (NewsDatabase.class) {
                if (INSTANCE == null) {
                    Log.d("ROOM_DB", "Creating Room Database instance");
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    NewsDatabase.class, "news_database")
                            .fallbackToDestructiveMigration()
                            .allowMainThreadQueries() // TEMPORARY: Check if Room works
                            .build();
                }
            }
        }
        return INSTANCE;
    }

}
