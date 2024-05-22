package com.example.mz_focusnews.adapter;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mz_focusnews.NewsDB.News;
import com.example.mz_focusnews.R;

import java.time.format.DateTimeFormatter;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.O)
public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {

    private Context context;
    private List<News> newsList;
    private OnNewsClickListener listener;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public interface OnNewsClickListener {
        void onNewsClick(News news);
    }

    public NewsAdapter(Context context, List<News> newsList, OnNewsClickListener listener) {
        this.context = context;
        this.newsList = newsList;
        this.listener = listener;
    }

    public void updateNews(List<News> newsList) {
        this.newsList = newsList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.news_item, parent, false);
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        News item = newsList.get(position);
        holder.newsTitle.setText(item.getTitle());
//        holder.publisher.setText(item.getPublisher());
        holder.time.setText(item.getDate());

        // 클릭 이벤트 처리
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    News clickedItem = newsList.get(position);
                    listener.onNewsClick(clickedItem); // 클릭된 아이템 정보 전달
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    public static class NewsViewHolder extends RecyclerView.ViewHolder {
        TextView newsTitle, publisher, time;

        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            newsTitle = itemView.findViewById(R.id.title);
//            publisher = itemView.findViewById(R.id.publisher);
            time = itemView.findViewById(R.id.publicationDate);
        }
    }
}
