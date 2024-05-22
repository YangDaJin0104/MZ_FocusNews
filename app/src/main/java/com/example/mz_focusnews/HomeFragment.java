package com.example.mz_focusnews;

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
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
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

public class HomeFragment extends Fragment {

    private String user_id;

    private RecyclerView recyclerView;
    private InterestAdapter interestAdapter;
    private List<NewsItem> newsItemList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // 사용자 이름 및 현재 시간 설정
        TextView userName = view.findViewById(R.id.user_name);
        TextView nowDate = view.findViewById(R.id.current_date);

        // 사용자 이름 설정
        Bundle bundle = getArguments();
        if (bundle != null) {
            String name = bundle.getString("user_name");
            if (name != null) {
                userName.setText("Hi, " + name);
                user_id = bundle.getString("user_id");
            }
        }

        // 현재 날짜 설정
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul")); // 한국 표준시로 설정
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        String formattedDate = dateFormat.format(calendar.getTime());
        nowDate.setText(formattedDate);

        // ViewPager2 설정 (오늘, 이주, 이달의 뉴스)
        ViewPager2Adapter viewPager2Adapter = new ViewPager2Adapter(getActivity());
        ViewPager2 viewPager2 = view.findViewById(R.id.news_view_pager);
        viewPager2.setAdapter(viewPager2Adapter);

        // 리사이클러뷰 초기화
        recyclerView = view.findViewById(R.id.rv_interest_content);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        // 뉴스 아이템 리스트 초기화
        newsItemList = new ArrayList<>();

        // 어댑터 설정
        interestAdapter = new InterestAdapter(getActivity(), newsItemList, new InterestAdapter.OnNewsItemClickListener() {
            @Override
            public void onNewsItemClick(NewsItem newsItem) {
                // 사용자 상호작용 기록 및 관심 카테고리 업데이트
                NewsUtils.logUserInteraction(getContext(), getUserSessions(), user_id, newsItem);
                NavHostFragment.findNavController(HomeFragment.this)
                        .navigate(R.id.action_homeFragment_to_contentFragment);
            }
        });
        recyclerView.setAdapter(interestAdapter);

        // 뉴스 데이터 가져오기
        fetchNewsData();

        return view;
    }

    private void fetchNewsData() {
        Response.Listener<String> responseListener = response -> {
            try {
                Log.d("HomeFragment", "Server Response: " + response); // 서버 응답 로그 출력
                JSONObject jsonResponse = new JSONObject(response);
                if (jsonResponse.getBoolean("success")) {
                    JSONArray newsArray = jsonResponse.getJSONArray("news");
                    newsItemList.clear();
                    for (int i = 0; i < newsArray.length(); i++) {
                        JSONObject newsObject = newsArray.getJSONObject(i);
                        NewsItem newsItem = new NewsItem(
                                newsObject.getInt("news_id"),
                                newsObject.getString("title"),
                                newsObject.getString("summary"),
                                newsObject.getString("category"),
                                newsObject.getString("date")
                        );
                        newsItemList.add(newsItem); // 뉴스 항목을 리스트에 추가
                    }
                    interestAdapter.notifyDataSetChanged(); // 어댑터에 데이터 변경 알림
                } else {
                    Toast.makeText(getActivity(), "Failed to fetch news", Toast.LENGTH_SHORT).show();
//                    Log.d("HomeFragment", "Server Error Message: " + jsonResponse.getString("message")); // 서버 에러 메시지 로그 출력
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


    // 사용자 세션을 관리하는 Map을 가져오는 메서드
    private Map<String, UserSession> getUserSessions() {
        NewsApp app = (NewsApp) getActivity().getApplication();
        return app.getUserSessions();
    }
}
