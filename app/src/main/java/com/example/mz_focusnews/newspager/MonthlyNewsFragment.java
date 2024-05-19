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

public class MonthlyNewsFragment extends Fragment {

    private TextView monthly_title;
    private TextView monthly_content;
    private Map<String, UserSession> userSessions;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_monthly_news, container, false);

        monthly_title = view.findViewById(R.id.monthly_title);
        monthly_content = view.findViewById(R.id.monthly_content);
        userSessions = new HashMap<>();

        monthly_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewsUtils.handleNewsItemClick(MonthlyNewsFragment.this, monthly_title, userSessions, "romi");
            }
        });

        loadMonthlyNews();
        return view;
    }

    private void loadMonthlyNews() {
        String startDate = getStartDateOfMonth();
        NewsUtils.loadNews(getContext(), startDate, "monthly", monthly_title, monthly_content, userSessions, this);
    }

    private String getStartDateOfMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return dateFormat.format(calendar.getTime());
    }
}
