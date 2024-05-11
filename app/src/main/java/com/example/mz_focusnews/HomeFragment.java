package com.example.mz_focusnews;

import android.icu.text.SimpleDateFormat;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Date;

public class HomeFragment extends Fragment {

    TextView userName;
    TextView nowDate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        userName = view.findViewById(R.id.user_name);

        // getArguments()로 전달된 Bundle을 가져옵니다.
        Bundle bundle = getArguments();
        if (bundle != null) {
            // Bundle에서 사용자 이름을 가져와서 TextView에 설정
            String s = bundle.getString("user_name");
            if (s != null) {
                userName.setText("Hi, " + s);
            }
        }

        nowDate = view.findViewById(R.id.now_date);

        // 현재 날짜 가져오기
        long now = System.currentTimeMillis();
        Date currentDate = new Date(now);

        // 날짜를 원하는 형식으로 포맷
        java.text.DateFormat formatter = new java.text.SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = formatter.format(currentDate);

        // TextView에 현재 날짜 설정
        nowDate.setText(formattedDate);

        return view;
    }

}