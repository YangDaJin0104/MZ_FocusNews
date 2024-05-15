package com.example.mz_focusnews;

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
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.mz_focusnews.request.NewsViewCountRequest;

import java.util.ArrayList;
import java.util.List;

public class CategoryFragment extends Fragment {

    private RecyclerView recyclerView;
    private NewsAdapter adapter;
    private List<NewsItem> newsItemList;
    private NavController navController;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
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
        newsItemList.add(new NewsItem("AI가 새로운 의료 기술을 개발", "한국일보", "2002-11-08"));
        newsItemList.add(new NewsItem("로켓 발사 성공, 새로운 우주 시대의 시작", "우주뉴스", "2024-04-02"));
        newsItemList.add(new NewsItem("미국에서 새로운 화성 탐사 임무 발표", "NASA News", "2024-04-03"));
        // 추가 데이터...

        // 어댑터 초기화 및 RecyclerView에 설정
        adapter = new NewsAdapter(getActivity(), newsItemList, new NewsAdapter.OnNewsItemClickListener() {
            @Override
            public void onNewsItemClick(NewsItem newsItem) {
                // 클릭된 뉴스 아이템 정보를 서버에 전송 (조회수 증가)
                sendNewsItemToServer(newsItem);

                // 클릭된 뉴스 아이템 정보를 Bundle에 담아서 NavGraph로 전달 (뉴스 데이터 전달)
                /**
                 * 지금은 그냥 간단하게 제목이랑 시간대만 보내는데..
                 * 추후에는 newsItem 자체에 내용도 담아서 같이 전달하던가 등등의 방법을 모색해야할듯
                 */
                Bundle bundle = new Bundle();
                bundle.putParcelable("news_item", newsItem);
                navController.navigate(R.id.action_categoryFragment_to_contentFragment, bundle);            }
        });

        recyclerView.setAdapter(adapter);

        return view;
    }

    // 클릭된 뉴스 아이템의 정보를 서버에 전송하는 메소드
    private void sendNewsItemToServer(NewsItem newsItem) {
//        // 클릭된 뉴스 아이템의 ID를 서버에 전송
//        String newsId = newsItem.getTitle();
//
//        // 서버에 전송하는 Request 객체 생성
//        NewsViewCountRequest request = new NewsViewCountRequest(newsId, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                // 서버 응답을 받았을 때의 처리
//                // 예를 들어, 응답이 성공인 경우에는 사용자에게 알림을 표시하거나, 화면을 갱신하는 등의 작업을 수행
//                Toast.makeText(getContext(), "뉴스 조회수가 증가했습니다.", Toast.LENGTH_SHORT).show();
//                // 여기에 추가적인 로직을 작성할 수 있습니다.
//            }
//        });
//
//        // RequestQueue에 Request 객체 추가
//        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
//        requestQueue.add(request);
    }
}
