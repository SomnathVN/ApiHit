package com.example.apihit.presentation;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apihit.R;
import com.example.apihit.database.NewsEntity;
import com.squareup.picasso.Picasso;
import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {
    private Context context;
    private List<NewsEntity> newsList;

    public NewsAdapter(Context context, List<NewsEntity> newsList) {
        this.context = context;
        this.newsList = newsList;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_news, parent, false);
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        NewsEntity news = newsList.get(position);
        holder.title.setText(news.getTitle());
        holder.source.setText(news.getSource());
        holder.author.setText(news.getAuthor());
        holder.publishedAt.setText(news.getPublishedAt());
        holder.description.setText(news.getDescription());

        Picasso.get().load(news.getUrlToImage()).into(holder.image);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(news.getUrl()));
            context.startActivity(intent);
        });

        Animation animation = AnimationUtils.loadAnimation(context, R.anim.slide_in);
        holder.itemView.startAnimation(animation);
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    public static class NewsViewHolder extends RecyclerView.ViewHolder {
        TextView title, source, author, description, publishedAt;
        ImageView image;

        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.newsTitle);
            source = itemView.findViewById(R.id.newsSource);
            author = itemView.findViewById(R.id.newsAuthor);
            description = itemView.findViewById(R.id.newsDescription);
            publishedAt = itemView.findViewById(R.id.newsPublishedAt);
            image = itemView.findViewById(R.id.newsImage);
        }
    }
}
