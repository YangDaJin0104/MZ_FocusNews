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

import java.util.List;

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
        holder.tv_date.setText(item.getDate());
        //holder.이미지

        // 디버깅 로그 추가
        Log.d("InterestAdapter", "Binding news item at position " + position + ": " + item.getTitle());

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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_image = itemView.findViewById(R.id.interest_image);
            tv_title = itemView.findViewById(R.id.interest_title);
            tv_description = itemView.findViewById(R.id.interest_description);
            tv_date = itemView.findViewById(R.id.interest_time);
        }
    }
}
