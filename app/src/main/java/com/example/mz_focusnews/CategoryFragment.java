package com.example.mz_focusnews;

import static com.example.mz_focusnews.NewsUtils.*;
import static com.example.mz_focusnews.NewsUtils.logUserInteraction;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mz_focusnews.adapter.NewsAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategoryFragment extends Fragment {

    private String user_id;

    private RecyclerView recyclerView;
    private NewsAdapter adapter;
    private List<NewsItem> newsItemList;
    private NavController navController;
    private Map<String, UserSession> userSessions;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        userSessions = new HashMap<>();

        SharedPreferences sp = getActivity().getSharedPreferences("UserData", Context.MODE_PRIVATE);
        user_id = sp.getString("user_id", null);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // 프래그먼트 레이아웃 인플레이트
        View view = inflater.inflate(R.layout.fragment_category, container, false);

        // 레이아웃에서 RecyclerView 찾기
        recyclerView = view.findViewById(R.id.recyclerView);

        // LayoutManager 설정
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // 뉴스 데이터 생성 및 어댑터에 추가
        newsItemList = new ArrayList<>();

        // 예시 데이터
        newsItemList.add(new NewsItem(1, "AI가 새로운 의료 기술을 개발", "한국일보", "2002-11-08", "요약1", "sports"));
        newsItemList.add(new NewsItem(2, "로켓 발사 성공, 새로운 우주 시대의 시작", "우주뉴스", "2024-04-02", "요약2", "economy"));
        newsItemList.add(new NewsItem(3, "미국에서 새로운 화성 탐사 임무 발표", "NASA News", "2024-04-03", "요약3", "society"));
        newsItemList.add(new NewsItem(4, "미국에서 새로운 화성 탐사 임무 발표", "NASA News", "2024-04-03", "요약4", "tech"));
        // 추가 데이터...

        // 어댑터 초기화 및 RecyclerView에 설정
        adapter = new NewsAdapter(getActivity(), newsItemList, new NewsAdapter.OnNewsItemClickListener() {
            @Override
            public void onNewsItemClick(NewsItem newsItem) {
                // 클릭된 뉴스 아이템 정보를 서버에 전송 (조회수 증가)
                sendNewsItemToServer(getContext(), newsItem);

                // 클릭된 뉴스 아이템 정보를 사용자 세션에 추가
                logUserInteraction(getContext(), userSessions, user_id, newsItem);

                // 클릭된 뉴스 아이템 정보를 Bundle에 담아서 NavGraph로 전달 (뉴스 데이터 전달)
                // 지금은 그냥 간단하게 제목이랑 시간대만 전송
                Bundle bundle = new Bundle();
                bundle.putParcelable("news_item", newsItem);
                navController.navigate(R.id.action_categoryFragment_to_contentFragment, bundle);
            }
        });

        recyclerView.setAdapter(adapter);

        return view;
    }
}
