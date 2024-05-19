package com.example.mz_focusnews.newspager;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mz_focusnews.NewsUtils;
import com.example.mz_focusnews.R;
import com.example.mz_focusnews.UserSession;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DailyNewsFragment extends Fragment {

    private TextView daily_title;
    private TextView daily_content;
    private Map<String, UserSession> userSessions;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_daily_news, container, false);

        daily_title = view.findViewById(R.id.daily_title);
        daily_content = view.findViewById(R.id.daily_content);
        userSessions = new HashMap<>();

        daily_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewsUtils.handleNewsItemClick(DailyNewsFragment.this, daily_title, userSessions, "romi");
//                NavHostFragment.findNavController(DailyNewsFragment.this)
//                        .navigate(R.id.action_homeFragment_to_contentFragment);
            }
        });

        loadDailyNews();
        return view;
    }

    // 오늘의 뉴스를 로드하는 메소드
    private void loadDailyNews() {
        String todayDate = getCurrentDate();
        NewsUtils.loadNews(getContext(), todayDate, "daily", daily_title, daily_content, userSessions, this);
    }

    // 현재 날짜를 가져오는 메소드
    private String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date currentDate = new Date();
        return dateFormat.format(currentDate);
    }
}
