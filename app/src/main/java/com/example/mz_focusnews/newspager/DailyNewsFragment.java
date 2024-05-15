package com.example.mz_focusnews.newspager;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.mz_focusnews.R;
import com.example.mz_focusnews.request.NewsRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DailyNewsFragment extends Fragment {

    TextView daily_title;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_daily_news, container, false);

        daily_title = view.findViewById(R.id.daily_title);

        loadDailyNews();

        return view;
    }

    // 오늘의 뉴스를 조회하는 메소드
    private void loadDailyNews() {
        String todayDate = getCurrentDate();

        /**
         * 일단 날짜 기준 없이 조회수 많은 뉴스 불러와보기
         */

        // Volley 요청 큐 생성
        RequestQueue queue = Volley.newRequestQueue(getContext());

        // JSON 요청 생성
        NewsRequest newsRequest = new NewsRequest(
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean success = response.getBoolean("success");
                            if (success) {
                                JSONArray newsArray = response.getJSONArray("news");
                                JSONObject topNews = newsArray.getJSONObject(0); // 조회수가 가장 많은 뉴스 가져오기

                                String title = topNews.getString("title"); // 뉴스 제목 가져오기
                                daily_title.setText(title);
                            } else {
                                Toast.makeText(getActivity(), "뉴스를 가져오지 못했습니다.", Toast.LENGTH_SHORT).show();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // 오류 처리
                    }
                });

        // 요청 큐에 요청 추가
        queue.add(newsRequest);

    }

    // 현재 날짜를 가져오는 메소드
    private String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date currentDate = new Date();
        return dateFormat.format(currentDate);
    }


}
