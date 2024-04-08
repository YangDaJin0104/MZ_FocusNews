package com.example.mz_focusnews;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class CategoryFragment extends Fragment {

    private RecyclerView recyclerView;
    private NewsAdapter adapter;
    private List<NewsItem> newsItemList;

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

        // 예시 데이터 추가
        newsItemList.add(new NewsItem("AI가 새로운 의료 기술을 개발", "한국일보", "2024-04-01"));
        newsItemList.add(new NewsItem("로켓 발사 성공, 새로운 우주 시대의 시작", "우주뉴스", "2024-04-02"));
        newsItemList.add(new NewsItem("미국에서 새로운 화성 탐사 임무 발표", "NASA News", "2024-04-03"));
        newsItemList.add(new NewsItem("세계 최대의 로봇 전시회 개최", "로보틱스 뉴스", "2024-04-04"));
        newsItemList.add(new NewsItem("인공지능 기술을 활용한 자율 주행 자동차 출시", "자동차 뉴스", "2024-04-05"));
        newsItemList.add(new NewsItem("인공지능 기술을 활용한 음악 작곡 경연 대회 개최", "음악뉴스", "2024-04-06"));
        newsItemList.add(new NewsItem("세계 최초로 인간과 로봇이 협업하는 공연 성공", "로봇엔터테인먼트", "2024-04-07"));
        // ...

        // 어댑터 초기화 및 RecyclerView에 설정
        adapter = new NewsAdapter(getActivity(), newsItemList);
        recyclerView.setAdapter(adapter);

        return view;
    }
}