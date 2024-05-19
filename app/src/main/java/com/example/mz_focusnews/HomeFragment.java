package com.example.mz_focusnews;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment {

    TextView userName;
    TextView nowDate;

    private RecyclerView recyclerView;
    private List<NewsItem> newsItemList;
    private InterestAdapter interestAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        /**
         * 사용자 이름 및 현재 시간 띄우기
         */
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

        nowDate = view.findViewById(R.id.current_date);

        // 현재 날짜 가져오기
        long now = System.currentTimeMillis();
        Date currentDate = new Date(now);

        // 날짜를 원하는 형식으로 포맷
        java.text.DateFormat formatter = new java.text.SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = formatter.format(currentDate);

        // TextView에 현재 날짜 설정
        nowDate.setText(formattedDate);

        ViewPager2Adapter viewPager2Adapter = new ViewPager2Adapter(getActivity());
        ViewPager2 viewPager2 = view.findViewById(R.id.news_view_pager);
        viewPager2.setAdapter(viewPager2Adapter);

        /**
         * 사용자 맞춤 뉴스 추천
         */

        // 리사이클러뷰 초기화
        recyclerView = view.findViewById(R.id.rv_interest_content);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        // 데이터 생성 (임의의 데이터로 대체)
        newsItemList = generateSampleData();

        // 어댑터 설정
        interestAdapter = new InterestAdapter(getActivity(), newsItemList, new InterestAdapter.OnNewsItemClickListener() {
            @Override
            public void onNewsItemClick(NewsItem newsItem) {
                // 사용자 상호작용 기록 및 관심 카테고리 업데이트
                String userId = "romi"; // 사용자 ID 가져오는 코드
                NewsUtils.logUserInteraction(getContext(), getUserSessions(), userId, newsItem);
                NavHostFragment.findNavController(HomeFragment.this)
                        .navigate(R.id.action_homeFragment_to_contentFragment);
            }
        });
        recyclerView.setAdapter(interestAdapter);

        return view;
    }

    // 임의의 데이터 생성 메서드
    private List<NewsItem> generateSampleData() {
        List<NewsItem> sampleData = new ArrayList<>();

        NewsItem sampleItem = new NewsItem(123, "중국어 열공 '푸바오 할부지'에… 갤S24 울트라 선물한 삼성",
                "삼성전자가 '푸바오 할부지'로 유명한 강철원 에버랜드 판다월드 사육사를 비롯한 사육사들에게 최신 인공지능...");
        sampleData.add(sampleItem);

        for (int i = 0; i < 10; i++) {
            NewsItem newsItem = new NewsItem(i, "제목 " + i, "설명 " + i);
            sampleData.add(newsItem);
        }

        return sampleData;
    }

    // 사용자 세션을 관리하는 Map을 가져오는 메서드
    private Map<String, UserSession> getUserSessions() {
        NewsApp app = (NewsApp) getActivity().getApplication();
        return app.getUserSessions();
    }
}
