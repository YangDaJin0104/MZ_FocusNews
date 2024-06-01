package com.example.mz_focusnews;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.mz_focusnews.NewsDB.News;
import com.example.mz_focusnews.request.NewsRequest;
import com.example.mz_focusnews.request.NewsViewCountRequest;
import com.example.mz_focusnews.request.UpdateInterestRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class NewsUtils {

    // 클릭된 뉴스 아이템의 정보를 서버에 전송하는 메소드
    public static void sendNewsItemToServer(Context context, News news) {
        int newsId = news.getNewsId();
//        Log.d("NewsUtils", "sendNewsItemToServer: 뉴스 조회수 증가 요청 전송 시작, 뉴스 ID=" + newsId);

        NewsViewCountRequest request = new NewsViewCountRequest(newsId, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("NewsUtils", "sendNewsItemToServer: 뉴스 조회수 증가 요청 성공, 뉴스 ID=" + newsId);
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(request);
//        Log.d("NewsUtils", "sendNewsItemToServer: 뉴스 조회수 증가 요청 큐에 추가됨, 뉴스 ID=" + newsId);
    }

    public static void logUserInteraction(Context context, Map<String, UserSession> userSessions, String userId, News news) {
//        Log.d("NewsUtils", "logUserInteraction: 사용자 상호작용 시작, 사용자 ID=" + userId + ", 뉴스 ID=" + news.getNewsId());
        UserSession session = userSessions.computeIfAbsent(userId, id -> new UserSession());
        session.logInteraction(news.getNewsId(), news.getCategory());

        // 사용자가 클릭한 카테고리를 서버로 전송
        updateUserInterestCategory(context, userId, news.getCategory());
    }

    public static void updateUserInterestCategory(Context context, String userId, String category) {
//        Log.d("NewsUtils", "updateUserInterestCategory: 사용자 선호 카테고리 업데이트 요청 시작, 카테고리=" + category);

        Response.Listener<String> responseListener = response -> {
            try {
                JSONObject jsonResponse = new JSONObject(response);
                if (jsonResponse.getBoolean("success")) {
//                    Toast.makeText(context, "선호 카테고리 업데이트 성공.", Toast.LENGTH_SHORT).show();
                } else {
//                    Toast.makeText(context, "선호 카테고리 업데이트 실패.", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                Toast.makeText(context, "JSON 파싱 오류.", Toast.LENGTH_SHORT).show();
            }
        };

        Response.ErrorListener errorListener = error -> {
            Log.e("NewsUtils", "네트워크 오류: ", error);
            Toast.makeText(context, "네트워크 오류.", Toast.LENGTH_SHORT).show();
        };

        UpdateInterestRequest request = new UpdateInterestRequest(userId, category, responseListener, errorListener);
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(request);
    }

    // 클릭된 뉴스 아이템을 처리하고 화면을 전환하는 메소드
    public static void handleNewsClick(Fragment fragment, News news, Map<String, UserSession> userSessions, String userId) {
//        Log.d("NewsUtils", "handleNewsItemClick: 뉴스 아이템 클릭 처리 시작, 사용자 ID=" + userId);
        if (news != null) {
            sendNewsItemToServer(fragment.getContext(), news);
            logUserInteraction(fragment.getContext(), userSessions, userId, news);

            Bundle bundle = new Bundle();
            bundle.putInt("newsId", news.getNewsId());

            NavHostFragment.findNavController(fragment)
                    .navigate(R.id.action_homeFragment_to_contentFragment, bundle);

            Log.d("NewsUtils", "handleNewsItemClick: 뉴스 아이템 클릭 처리 완료, 사용자 ID=" + userId + ", 뉴스 ID=" + news.getNewsId());
        } else {
            Log.d("NewsUtils", "handleNewsItemClick: newsItem이 null입니다, 사용자 ID=" + userId);
        }
    }

    public static void loadNews(Context context, String date, String type, TextView titleView, TextView contentView, TextView dateView, ImageView imageView, Map<String, UserSession> userSessions, Fragment fragment) {
//        Log.d("NewsUtils", "loadNews: 뉴스 로드 요청, 날짜=" + date + ", 타입=" + type);
        RequestQueue queue = Volley.newRequestQueue(context);

        NewsRequest newsRequest = new NewsRequest(date, type, response -> {

            Log.d("NewsResponse", "Response: " + response);
            try {
                JSONObject jsonResponse = new JSONObject(response);
                boolean success = jsonResponse.getBoolean("success");
                if (success) {
                    JSONArray newsArray = jsonResponse.getJSONArray("news");
                    if (newsArray.length() > 0) {
                        JSONObject topNews = newsArray.getJSONObject(0);
                        updateUIWithNewsData(context, topNews, type, titleView, contentView, dateView, imageView);
                    } else {
                        handleNoNewsForDay(context, date, type, titleView, contentView, dateView, imageView, userSessions, fragment);
                    }
                } else {
                    Toast.makeText(context, "뉴스를 가져오지 못했습니다.", Toast.LENGTH_SHORT).show();
                    Log.d("NewsUtils", "loadNews: 뉴스 로드 실패, 성공 플래그 false");
                }
            } catch (JSONException e) {
                Log.d("NewsUtils", "loadNews: JSON 파싱 오류, 날짜=" + date + ", 타입=" + type, e);
                Toast.makeText(context, "데이터 형식 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
            }

        }, error -> {
            Log.e("NewsUtils", "Error retrieving news: " + error.getMessage());
            Toast.makeText(context, "뉴스 가져오기 오류.", Toast.LENGTH_SHORT).show();
        });

        queue.add(newsRequest);
    }

    private static void updateUIWithNewsData(Context context, JSONObject newsData, String type, TextView titleView, TextView contentView, TextView dateView, ImageView imageView) throws JSONException {
        News news = News.parseNewsFromJSON(newsData);
        titleView.setText(news.getTitle());
        Glide.with(context).load(news.getImgUrl()).placeholder(R.drawable.ic_launcher_foreground).fallback(R.drawable.character).into(imageView);
        contentView.setText(truncateSummary(news.getSummary(), 30));

        dateView.setText(formatDateString(news.getDate()));
        titleView.setTag(news);

        Log.d("NewsUtils", "뉴스 업데이트 정보: 날짜=" + news.getDate() + ", 조회수=" + news.getView());

        // coddl: 머지하면서 SharedPreferences 없어져서 다시 추가했습니당
        // 퀴즈 - 오늘의 퀴즈에서 사용할 데이터 (오늘의 뉴스에 대한 데이터만 저장)
        if(type.equals("daily")){
            SharedPreferences sp = context.getSharedPreferences("NewsData", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("summary", news.getSummary());
            editor.putInt("newsId", news.getNewsId());
            editor.apply();

            Log.d("NewsUtils", "공유변수 저장\nsummary = "+news.getSummary()+"\nnewsId = "+news.getNewsId());
        }
    }

    private static void handleNoNewsForDay(Context context, String date, String type, TextView titleView, TextView contentView, TextView dateView, ImageView imageView, Map<String, UserSession> userSessions, Fragment fragment) {
        String previousDate = getPreviousDate(date, type, TimeZone.getTimeZone("Asia/Seoul"));
        if (!previousDate.equals(date)) {
            loadNews(context, previousDate, type, titleView, contentView, dateView, imageView, userSessions, fragment);
        } else {
            Toast.makeText(context, "최근 뉴스가 없습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    public static String getPreviousDate(String current, String type, TimeZone timeZone) {
        Calendar calendar = Calendar.getInstance(timeZone);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        format.setTimeZone(timeZone);

        try {
            Date currentDate = format.parse(current);
            calendar.setTime(currentDate);
        } catch (ParseException e) {
            Log.e("NewsUtils", "Error parsing date: " + current, e);
            return null; // 파싱 실패 시 null 반환
        }

        switch (type) {
            case "daily":
                calendar.add(Calendar.DATE, -1);
                break;
            case "weekly":
                calendar.add(Calendar.WEEK_OF_YEAR, -1);
                break;
            case "monthly":
                calendar.add(Calendar.MONTH, -1);
                break;
        }
        return format.format(calendar.getTime());
    }

    private static String truncateSummary(String summary, int maxLength) {
        if (summary.length() <= maxLength) {
            return summary;
        }

        // 내용을 maxLength 길이로 자르기
        String truncated = summary.substring(0, maxLength);

        // 역순으로 뒤집기
        String reversed = new StringBuilder(truncated).reverse().toString();

        // 첫 번째 공백의 위치 찾기
        int firstSpaceIndex = reversed.indexOf(' ');
        if (firstSpaceIndex != -1) {
            // 첫 번째 공백 뒤의 문자열 잘라내기
            truncated = reversed.substring(firstSpaceIndex + 1);

            // 다시 원래 순서로 뒤집기
            truncated = new StringBuilder(truncated).reverse().toString();
        } else {
            truncated = new StringBuilder(reversed).reverse().toString();
        }

        // "..." 붙이기
        return truncated + "...";
    }

    private static String formatDateString(String dateString) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            Date date = inputFormat.parse(dateString);
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return "반환 실패"; // 오류가 발생하면 원래 날짜 문자열 반환
        }

    }

}