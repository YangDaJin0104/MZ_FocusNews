package com.example.mz_focusnews.NewsDB;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mz_focusnews.R;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.O)
public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {

    private List<News> newsList;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public NewsAdapter(List<News> newsList) {
        this.newsList = newsList;
    }

    public void updateNews(List<News> newsList) {
        this.newsList = newsList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_item, parent, false);
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        News news = newsList.get(position);
        // 수정된 제목을 설정
        holder.title.setText(getModifiedTitle(news.getTitle()));
        // 날짜 포맷팅
        holder.date.setText(formatDate(news.getDate()));
        holder.publish.setText(news.getPublish());
        // 이미지 로딩은 Glide 또는 Picasso와 같은 라이브러리를 사용할 수 있습니다.
        Glide.with(holder.itemView.getContext())
                .load(news.getImgUrl())
                .placeholder(R.drawable.ic_launcher_foreground)
                .fallback(R.drawable.character)
                .into(holder.imageView);
    }

    private String getModifiedTitle(String originalTitle) {
        if (originalTitle == null || originalTitle.isEmpty()) {
            return originalTitle;
        }
        int index = originalTitle.lastIndexOf("-");
        String modifiedTitle;
        if (index != -1) {
            modifiedTitle = originalTitle.substring(0, index).trim();
        } else {
            modifiedTitle = originalTitle;
        }

        // 제목이 26자를 넘어가면 "..."으로 표시
        if (modifiedTitle.length() > 33) {
            modifiedTitle = modifiedTitle.substring(0, 33) + "...";
        }

        return modifiedTitle;
    }

    private String formatDate(String dateString) {
        if (dateString == null || dateString.isEmpty()) {
            return dateString;
        }
        LocalDateTime dateTime = LocalDateTime.parse(dateString, DateTimeFormatter.ISO_DATE_TIME);
        return dateTime.format(formatter);
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    static class NewsViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView date;
        TextView publish;
        ImageView imageView;

        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            publish = itemView.findViewById(R.id.publish);
            date = itemView.findViewById(R.id.publicationDate);
            imageView = itemView.findViewById(R.id.newsImageView);
        }
    }
}
