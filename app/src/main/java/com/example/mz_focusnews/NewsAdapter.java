package com.example.mz_focusnews;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


/**
 * 뉴스 목록을 표시하는 데 사용되는 클래스 (리스트 스크롤 구현)
 */
public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {

    private Context context;
    private List<NewsItem> newsList;
    private NavController navController;

    public NewsAdapter(Context context, List<NewsItem> newsList, NavController navController) {
        this.context = context;
        this.newsList = newsList;
        this.navController = navController;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.news_item, parent, false);
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        NewsItem item = newsList.get(position);
        holder.newsTitle.setText(item.getTitle());
        holder.publisher.setText(item.getPublisher());
        holder.time.setText(item.getTime());
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    public class NewsViewHolder extends RecyclerView.ViewHolder{
        TextView newsTitle, publisher, time;

        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            newsTitle = itemView.findViewById(R.id.title);
            publisher = itemView.findViewById(R.id.publisher);
            time = itemView.findViewById(R.id.publicationDate);

            itemView.findViewById(R.id.title).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        NewsItem clickedItem = newsList.get(position);
                        navController.navigate(R.id.action_categoryFragment_to_contentFragment);
                    }

                }
            });

        }
    }
}

