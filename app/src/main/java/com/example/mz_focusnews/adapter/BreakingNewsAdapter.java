package com.example.mz_focusnews.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mz_focusnews.NewsDB.News;
import com.example.mz_focusnews.R;

import java.util.List;

public class BreakingNewsAdapter extends RecyclerView.Adapter<BreakingNewsAdapter.ViewHolder> {

    private Context context;
    private List<News> newsList;
    private OnNewsClickListener onNewsClickListener;

    public BreakingNewsAdapter(Context context, List<News> newsList, OnNewsClickListener onNewsClickListener) {
        this.context = context;
        this.newsList = newsList;
        this.onNewsClickListener = onNewsClickListener;
    }

    public interface OnNewsClickListener {
        void onNewsClick(News news);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_breaking_news, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        News news = newsList.get(position);
        holder.titleTextView.setText(news.getTitle());
        holder.itemView.setOnClickListener(v -> onNewsClickListener.onNewsClick(news));
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.breaking_news_title);
        }
    }
}
