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
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class WeeklyNewsFragment extends Fragment {

    private TextView weekly_title;
    private TextView weekly_content;
    private Map<String, UserSession> userSessions;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weekly_news, container, false);

        weekly_title = view.findViewById(R.id.weekly_title);
        weekly_content = view.findViewById(R.id.weekly_content);
        userSessions = new HashMap<>();

        weekly_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewsUtils.handleNewsItemClick(WeeklyNewsFragment.this, weekly_title, userSessions, "romi");
            }
        });

        loadWeeklyNews();
        return view;
    }

    private void loadWeeklyNews() {
        String startDate = getStartDateOfWeek();
        NewsUtils.loadNews(getContext(), startDate, "weekly", weekly_title, weekly_content, userSessions, this);
    }

    private String getStartDateOfWeek() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return dateFormat.format(calendar.getTime());
    }
}
