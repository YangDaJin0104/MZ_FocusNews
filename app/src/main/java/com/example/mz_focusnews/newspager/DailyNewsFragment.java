package com.example.mz_focusnews.newspager;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mz_focusnews.NewsItem;
import com.example.mz_focusnews.NewsUtils;
import com.example.mz_focusnews.R;
import com.example.mz_focusnews.UserSession;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class DailyNewsFragment extends Fragment {

    private String user_id;

    private TextView daily_title;
    private TextView daily_content;
    private TextView daily_date;
    private Map<String, UserSession> userSessions;

    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_daily_news, container, false);

        daily_title = view.findViewById(R.id.daily_title);
        daily_content = view.findViewById(R.id.daily_content);
        daily_date = view.findViewById(R.id.daily_date);
        userSessions = new HashMap<>();

        SharedPreferences sp = getActivity().getSharedPreferences("UserData", Context.MODE_PRIVATE);
        user_id = sp.getString("user_id", null);

        daily_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewsItem newsItem = (NewsItem) daily_title.getTag();
                if (newsItem != null) {
                    NewsUtils.handleNewsItemClick(DailyNewsFragment.this, newsItem, userSessions, user_id);
                }
            }
        });

        loadDailyNews();
        return view;
    }

    // 오늘의 뉴스를 로드하는 메소드
    private void loadDailyNews() {
        String todayDate = getCurrentDate();
        Log.d("loadNews", "loadDailyNews: todayDate=" + todayDate);
        NewsUtils.loadNews(getContext(), todayDate, "daily", daily_title, daily_content, daily_date, userSessions, this);
    }

    // 현재 날짜를 가져오는 메소드
    private String getCurrentDate() {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul")); // 한국 표준시로 설정
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        return dateFormat.format(calendar.getTime());
    }
}
