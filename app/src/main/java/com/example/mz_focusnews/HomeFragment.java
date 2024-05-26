package com.example.mz_focusnews;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.mz_focusnews.NewsDB.News;
import com.example.mz_focusnews.NewsDB.RetrofitClient;
import com.example.mz_focusnews.adapter.InterestAdapter;
import com.example.mz_focusnews.adapter.ViewPager2Adapter;
import com.example.mz_focusnews.request.FetchNewsRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;

public class HomeFragment extends Fragment {

    private String user_id;
    private String user_name;

    private RecyclerView recyclerView;
    private InterestAdapter interestAdapter;
    private List<News> newsList;
    private Button breakingNewsButton; // 속보 뉴스 버튼 참조를 위한 변수

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // 사용자 이름 및 현재 시간 설정
        TextView userName = view.findViewById(R.id.user_name);
        TextView nowDate = view.findViewById(R.id.current_date);
        breakingNewsButton = view.findViewById(R.id.breakingNews); // 속보 뉴스 버튼 참조

        // SharedPreferences로 데이터 받아오기: 아이디 , 이름
        SharedPreferences sp = getActivity().getSharedPreferences("UserData", Context.MODE_PRIVATE);
        user_id = sp.getString("user_id", null);
        user_name = sp.getString("user_name", null);

        userName.setText("Hi, " + user_name);

        setCurrentDate(nowDate);

        // ViewPager2 설정 (오늘, 이주, 이달의 뉴스)
        ViewPager2Adapter viewPager2Adapter = new ViewPager2Adapter(getActivity());
        ViewPager2 viewPager2 = view.findViewById(R.id.news_view_pager);
        viewPager2.setAdapter(viewPager2Adapter);

        // 리사이클러뷰 초기화
        recyclerView = view.findViewById(R.id.rv_interest_content);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        // 뉴스 아이템 리스트 초기화
        newsList = new ArrayList<>();

        // 어댑터 설정
        interestAdapter = new InterestAdapter(getActivity(), newsList, new InterestAdapter.OnNewsClickListener() {
            @Override
            public void onNewsClick(News news) {
                // 사용자 상호작용 기록 및 관심 카테고리 업데이트
                NewsUtils.logUserInteraction(getContext(), getUserSessions(), user_id, news);
                NavHostFragment.findNavController(HomeFragment.this)
                        .navigate(R.id.action_homeFragment_to_contentFragment);
            }
        });

        recyclerView.setAdapter(interestAdapter);

        fetchNewsData(); // 기존 뉴스 데이터 가져오는 메소드 호출
        fetchBreakingNewsData(); // 속보 뉴스 데이터 가져오는 메소드 호출 추가

        return view;
    }

    // 속보 뉴스 데이터를 가져오는 메소드 추가
    private void fetchBreakingNewsData() {
        String keyword = "[속보]";
        RetrofitClient.getInstance().getNewsApi().getBreakingNewsWithKeyword(1, keyword).enqueue(new Callback<List<News>>() {
            @Override
            public void onResponse(Call<List<News>> call, retrofit2.Response<List<News>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    newsList.clear();
                    newsList.addAll(response.body());
                    interestAdapter.notifyDataSetChanged();

                    if (!newsList.isEmpty()) {
                        News breakingNews = newsList.get(0);
                        String title = breakingNews.getTitle();
                        if (title.length() > 30) {
                            title = title.substring(0, 30) + "..."; // 제목이 30자를 초과할 경우 자르고 "..." 추가
                        }
                        breakingNewsButton.setText(title); // 속보 뉴스 제목을 버튼 텍스트로 설정
                    } else {
                        breakingNewsButton.setText("No breaking news available");
                    }
                } else {
                    int statusCode = response.code();
                    String errorMessage = response.message();
                    Log.e("HomeFragment", "Error: " + statusCode + ", " + errorMessage);
                    Toast.makeText(getActivity(), "Failed to fetch news: " + statusCode, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<News>> call, Throwable t) {
                Toast.makeText(getActivity(), "Network Error", Toast.LENGTH_SHORT).show();
            }
        });
    }


    // 뉴스 데이터 가져오는 메소드
    private void fetchNewsData() {
        Response.Listener<String> responseListener = response -> {
            try {
                Log.d("HomeFragment", "Server Response: " + response); // 서버 응답 로그 출력
                JSONObject jsonResponse = new JSONObject(response);
                if (jsonResponse.getBoolean("success")) {
                    JSONArray newsArray = jsonResponse.getJSONArray("news");
                    newsList.clear();
                    for (int i = 0; i < newsArray.length(); i++) {
                        JSONObject newsObject = newsArray.getJSONObject(i);
                        News news = new News(
                                newsObject.getInt("news_id"),
                                newsObject.getInt("view"),
                                newsObject.getString("link"),
                                newsObject.getString("summary"),
                                newsObject.getString("title"),
                                newsObject.getString("category"),
                                newsObject.getString("date"),
                                newsObject.optInt("related_news1", 0), // 기본값으로 0을 사용
                                newsObject.optInt("related_news2", 0)  // 기본값으로 0을 사용
                        );
                        newsList.add(news); // 뉴스 항목을 리스트에 추가
                    }
                    interestAdapter.notifyDataSetChanged(); // 어댑터에 데이터 변경 알림
                } else {
                    Toast.makeText(getActivity(), "Failed to fetch news", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "JSON Parsing Error", Toast.LENGTH_SHORT).show();
            }
        };

        Response.ErrorListener errorListener = error -> {
            Log.e("HomeFragment", "Error: ", error);
            Toast.makeText(getActivity(), "Network Error", Toast.LENGTH_SHORT).show();
        };

        FetchNewsRequest request = new FetchNewsRequest(user_id, responseListener, errorListener);
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        queue.add(request);
    }

    // 사용자 세션을 관리하는 Map을 가져오는 메소드
    private Map<String, UserSession> getUserSessions() {
        NewsApp app = (NewsApp) getActivity().getApplication();
        return app.getUserSessions();
    }

    // 현재 날짜 불러오는 메소드
    private static void setCurrentDate(TextView nowDate) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul")); // 한국 표준시로 설정
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        String formattedDate = dateFormat.format(calendar.getTime());
        nowDate.setText(formattedDate);
    }
}
