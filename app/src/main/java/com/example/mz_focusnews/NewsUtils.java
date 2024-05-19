package com.example.mz_focusnews;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.mz_focusnews.request.NewsRequest;
import com.example.mz_focusnews.request.NewsViewCountRequest;
import com.example.mz_focusnews.request.UpdateCategoryRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class NewsUtils {

    // 클릭된 뉴스 아이템의 정보를 서버에 전송하는 메소드
    public static void sendNewsItemToServer(Context context, NewsItem newsItem) {
        int newsId = newsItem.getNewsId();
        Log.d("NewsUtils", "sendNewsItemToServer: 뉴스 조회수 증가 요청 전송 시작, 뉴스 ID=" + newsId);

        NewsViewCountRequest request = new NewsViewCountRequest(newsId, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("NewsUtils", "sendNewsItemToServer: 뉴스 조회수 증가 요청 성공, 뉴스 ID=" + newsId);
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(request);
        Log.d("NewsUtils", "sendNewsItemToServer: 뉴스 조회수 증가 요청 큐에 추가됨, 뉴스 ID=" + newsId);
    }

    // 선호 카테고리 반영하는 메소드
    public static void logUserInteraction(Context context, Map<String, UserSession> userSessions, String userId, NewsItem newsItem) {
        Log.d("NewsUtils", "logUserInteraction: 사용자 상호작용 시작, 사용자 ID=" + userId + ", 뉴스 ID=" + newsItem.getNewsId());
        UserSession session = userSessions.computeIfAbsent(userId, id -> new UserSession());
        session.logInteraction(newsItem.getNewsId(), newsItem.getCategory());

        String preferredCategory = session.getPreferredCategory();
        updateUserInterestCategory(context, userId, preferredCategory);

        Log.d("NewsUtils", "logUserInteraction: 사용자 상호작용 완료, 사용자 ID=" + userId + ", 선호 카테고리=" + preferredCategory);
    }

    // 서버에 사용자의 선호 카테고리를 업데이트하는 메소드
    public static void updateUserInterestCategory(Context context, String userId, String category) {
        Log.d("NewsUtils", "updateUserInterestCategory: 사용자 선호 카테고리 업데이트 요청 시작, 사용자 ID=" + userId + ", 선호 카테고리=" + category);
        UpdateCategoryRequest request = new UpdateCategoryRequest(userId, category, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    if (success) {
                        Toast.makeText(context, "선호 카테고리가 업데이트되었습니다.", Toast.LENGTH_SHORT).show();
                        Log.d("NewsUtils", "updateUserInterestCategory: 선호 카테고리 업데이트 성공, 사용자 ID=" + userId + ", 선호 카테고리=" + category);
                    } else {
                        Toast.makeText(context, "선호 카테고리 업데이트에 실패했습니다.", Toast.LENGTH_SHORT).show();
                        Log.d("NewsUtils", "updateUserInterestCategory: 선호 카테고리 업데이트 실패, 사용자 ID=" + userId + ", 선호 카테고리=" + category);
                    }
                } catch (JSONException e) {
                    Log.d("NewsUtils", "updateUserInterestCategory: JSON 파싱 오류, 사용자 ID=" + userId + ", 선호 카테고리=" + category, e);
                }
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(request);
        Log.d("NewsUtils", "updateUserInterestCategory: 사용자 선호 카테고리 업데이트 요청 큐에 추가됨, 사용자 ID=" + userId + ", 선호 카테고리=" + category);
    }

    // 클릭된 뉴스 아이템을 처리하고 화면을 전환하는 메소드
    public static void handleNewsItemClick(Fragment fragment, TextView titleView, Map<String, UserSession> userSessions, String userId) {
        Log.d("NewsUtils", "handleNewsItemClick: 뉴스 아이템 클릭 처리 시작, 사용자 ID=" + userId);
        JSONObject topNews = (JSONObject) titleView.getTag();
        if (topNews != null) {
            try {
                int newsId = topNews.getInt("news_id");
                String category = topNews.getString("category");

                NewsItem newsItem = new NewsItem(newsId, topNews.getString("title"), topNews.getString("summary"), topNews.getString("time"));

                sendNewsItemToServer(fragment.getContext(), newsItem);
                logUserInteraction(fragment.getContext(), userSessions, userId, newsItem);

                Bundle bundle = new Bundle();
                bundle.putParcelable("news_item", newsItem);

                NavHostFragment.findNavController(fragment)
                        .navigate(R.id.action_homeFragment_to_contentFragment, bundle);

                Log.d("NewsUtils", "handleNewsItemClick: 뉴스 아이템 클릭 처리 완료, 사용자 ID=" + userId + ", 뉴스 ID=" + newsId);
            } catch (JSONException e) {
                Log.d("NewsUtils", "handleNewsItemClick: JSON 파싱 오류, 사용자 ID=" + userId, e);
            }
        } else {
            Log.d("NewsUtils", "handleNewsItemClick: topNews가 null입니다, 사용자 ID=" + userId);
        }
    }

    // 뉴스 데이터를 로드하는 공통 메서드
    public static void loadNews(Context context, String date, String type, TextView titleView, TextView contentView, Map<String, UserSession> userSessions, Fragment fragment) {
        Log.d("NewsUtils", "loadNews: 뉴스 로드 요청 시작, 날짜=" + date + ", 타입=" + type);
        RequestQueue queue = Volley.newRequestQueue(context);

        NewsRequest newsRequest = new NewsRequest(date, type, // type 파라미터 추가
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            if (success) {
                                JSONArray newsArray = jsonResponse.getJSONArray("news");
                                JSONObject topNews = newsArray.getJSONObject(0);

                                String title = topNews.getString("title");
                                String content = topNews.getString("summary");
                                String newsDate = topNews.getString("date"); // date 필드 추가

                                titleView.setText(title);
                                contentView.setText(content); // 뉴스 내용과 날짜를 함께 표시

                                titleView.setTag(topNews);

                                Log.d("NewsUtils", "loadNews: 뉴스 로드 성공, 제목=" + title);
                            } else {
                                Toast.makeText(context, "뉴스를 가져오지 못했습니다.", Toast.LENGTH_SHORT).show();
                                Log.d("NewsUtils", "loadNews: 뉴스 로드 실패, 성공 플래그 false");
                            }
                        } catch (JSONException e) {
                            Log.d("NewsUtils", "loadNews: JSON 파싱 오류, 날짜=" + date + ", 타입=" + type, e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("NewsUtils", "loadNews: 뉴스 로드 오류, 날짜=" + date + ", 타입=" + type, error);
                    }
                });

        queue.add(newsRequest);
        Log.d("NewsUtils", "loadNews: 뉴스 로드 요청 큐에 추가됨, 날짜=" + date + ", 타입=" + type);
    }
}
