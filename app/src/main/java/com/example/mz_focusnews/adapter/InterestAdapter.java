package com.example.mz_focusnews.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mz_focusnews.NewsDB.News;
import com.example.mz_focusnews.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class InterestAdapter extends RecyclerView.Adapter<InterestAdapter.ViewHolder> {

    private List<News> newsList;
    private Context context;
    private OnNewsClickListener listener;

    public interface OnNewsClickListener {
        void onNewsClick(News news);
    }

    public InterestAdapter(Context context, List<News> newsList, OnNewsClickListener listener) {
        this.context = context;
        this.newsList = newsList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.interest_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        News news = newsList.get(position);
        holder.tv_title.setText(news.getTitle());
        holder.tv_description.setText(news.getSummary());
        holder.tv_category.setText(news.getCategory());

        // 날짜를 상대 시간으로 변환하여 설정
        String relativeTime = getRelativeTime(news.getDate());
        holder.tv_date.setText(relativeTime);

        // 디버깅 로그 추가
        Log.d("InterestAdapter", "Original date string: " + news.getDate());

        // NewsItem 객체를 태그로 설정
        holder.itemView.setTag(news);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    News clickedItem = newsList.get(position);
                    Log.d("InterestAdapter", "Item clicked: " + clickedItem.getTitle()); // 로그 추가
                    listener.onNewsClick(clickedItem); // 클릭된 아이템 정보 전달
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_image;
        TextView tv_title;
        TextView tv_description;
        TextView tv_date;
        TextView tv_category;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_image = itemView.findViewById(R.id.interest_image);
            tv_title = itemView.findViewById(R.id.interest_title);
            tv_description = itemView.findViewById(R.id.interest_description);
            tv_date = itemView.findViewById(R.id.interest_time);
            tv_category = itemView.findViewById(R.id.interest_category);
        }
    }

    private String getRelativeTime(String dateString) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        try {
            Date date = sdf.parse(dateString);
            long currentTime = System.currentTimeMillis();
            long time = date != null ? date.getTime() : currentTime;
            long diff = currentTime - time;

            long seconds = diff / 1000;
            long minutes = seconds / 60;
            long hours = minutes / 60;
            long days = hours / 24;

            if (days > 0) {
                return days + " days ago";
            } else if (hours > 0) {
                return hours + " hours ago";
            } else if (minutes > 0) {
                return minutes + " minutes ago";
            } else {
                return "Just now";
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return dateString;
        }
    }
}
