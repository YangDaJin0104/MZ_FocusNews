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

import com.example.mz_focusnews.NewsDB.News;
import com.example.mz_focusnews.NewsUtils;
import com.example.mz_focusnews.R;
import com.example.mz_focusnews.UserSession;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class MonthlyNewsFragment extends Fragment {

    private String user_id;

    private TextView monthly_title;
    private TextView monthly_content;
    private TextView monthly_date;
    private Map<String, UserSession> userSessions;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_monthly_news, container, false);

        monthly_title = view.findViewById(R.id.monthly_title);
        monthly_content = view.findViewById(R.id.monthly_content);
        monthly_date = view.findViewById(R.id.monthly_date);
        userSessions = new HashMap<>();

        SharedPreferences sp = getActivity().getSharedPreferences("UserData", Context.MODE_PRIVATE);
        user_id = sp.getString("user_id", null);

        monthly_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                News news = (News) monthly_title.getTag();
                if (news != null) {
                    NewsUtils.handleNewsClick(MonthlyNewsFragment.this, news, userSessions, "romi");
                }
            }
        });

        loadMonthlyNews();
        return view;
    }

    private void loadMonthlyNews() {
        String startDate = getStartDateOfMonth();
        String previousStartDate = NewsUtils.getPreviousDate("monthly", TimeZone.getTimeZone("Asia/Seoul"));
        NewsUtils.loadNews(getContext(), startDate, "monthly", monthly_title, monthly_content, monthly_date, userSessions, this);
        NewsUtils.loadNews(getContext(), previousStartDate, "monthly", monthly_title, monthly_content, monthly_date, userSessions, this);
    }

    private String getStartDateOfMonth() {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul")); // 한국 표준시로 설정
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        return dateFormat.format(calendar.getTime());
    }
}
