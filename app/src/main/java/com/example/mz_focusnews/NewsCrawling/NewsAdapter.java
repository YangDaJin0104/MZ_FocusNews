package com.example.mz_focusnews.NewsCrawling;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mz_focusnews.MainActivity;
import com.example.mz_focusnews.R;

import java.util.ArrayList;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {
    private Context context;
    private ArrayList<NewsArticle> articles;

    public NewsAdapter(Context context, ArrayList<NewsArticle> articles) {
        this.context = context;
        this.articles = articles;
    }

    // 데이터를 업데이트하는 메서드, 기존 데이터를 삭제하고 새로운 데이터로 교체
    public void updateData(ArrayList<NewsArticle> newArticles) {
        articles.clear();
        articles.addAll(newArticles);
        notifyDataSetChanged();  // RecyclerView에게 데이터가 변경되었음을 알리고 뷰를 갱신하도록 함
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.news_item, parent, false);
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        NewsArticle article = articles.get(position);
        holder.title.setText(article.getTitle());
        holder.publicationDate.setText(article.getPublicationDate());
        holder.publisher.setText(article.getPublisher());

        Glide.with(context)
                .load(article.getImageUrl())
                .into(holder.imageView);
        holder.itemView.setOnClickListener(v -> {
            if (context instanceof MainActivity) {
                ((MainActivity) context).showArticleInWebView(article.getLink());
            }
        });    }

    @Override
    public int getItemCount() {
        return articles.size();
    }

    static class NewsViewHolder extends RecyclerView.ViewHolder {
        TextView title, publicationDate, publisher;
        ImageView imageView;

        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            publicationDate = itemView.findViewById(R.id.publicationDate);
            publisher = itemView.findViewById(R.id.publisher);
            imageView = itemView.findViewById(R.id.newsImageView);
        }
    }
}
