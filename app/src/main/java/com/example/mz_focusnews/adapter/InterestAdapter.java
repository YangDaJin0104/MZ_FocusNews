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

import com.example.mz_focusnews.NewsItem;
import com.example.mz_focusnews.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class InterestAdapter extends RecyclerView.Adapter<InterestAdapter.ViewHolder> {

    private List<NewsItem> newsItemList;
    private Context context;
    private OnNewsItemClickListener listener;

    public interface OnNewsItemClickListener {
        void onNewsItemClick(NewsItem newsItem);
    }

    public InterestAdapter(Context context, List<NewsItem> newsItemList, OnNewsItemClickListener listener) {
        this.context = context;
        this.newsItemList = newsItemList;
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
        NewsItem item = newsItemList.get(position);
        holder.tv_title.setText(item.getTitle());
        holder.tv_description.setText(item.getSummary());
        holder.tv_category.setText(item.getCategory());

        // 날짜를 상대 시간으로 변환하여 설정
        String relativeTime = getRelativeTime(item.getDate());
        holder.tv_date.setText(relativeTime);

        // 디버깅 로그 추가
        Log.d("InterestAdapter", "Original date string: " + item.getDate());

        // NewsItem 객체를 태그로 설정
        holder.itemView.setTag(item);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    NewsItem clickedItem = newsItemList.get(position);
                    Log.d("InterestAdapter", "Item clicked: " + clickedItem.getTitle()); // 로그 추가
                    listener.onNewsItemClick(clickedItem); // 클릭된 아이템 정보 전달
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return newsItemList.size();
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
    }}
