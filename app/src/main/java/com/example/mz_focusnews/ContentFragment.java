package com.example.mz_focusnews;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ContentFragment extends Fragment {

    private TextView tv_title;
    private TextView tv_time;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_content, container, false);

        tv_title = view.findViewById(R.id.news_title);
        tv_time = view.findViewById(R.id.news_date);

        // Bundle로부터 전달받은 데이터를 가져옴
        Bundle bundle = getArguments();
        if (bundle != null) {
            NewsItem newsItem = bundle.getParcelable("news_item");
            if (newsItem != null) {
                // 뉴스 아이템 정보를 TextView에 설정
                tv_title.setText(newsItem.getTitle());
                tv_time.setText(newsItem.getTime());
            } else {
                Log.e("ContentFragment", "Received null NewsItem");
            }
        }

        return view;
    }
}