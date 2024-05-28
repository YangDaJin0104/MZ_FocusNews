package com.example.mz_focusnews;

import android.os.Build;
import static com.example.mz_focusnews.NewsUtils.*;
import static com.example.mz_focusnews.NewsUtils.logUserInteraction;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mz_focusnews.KeyWords.NewsFetcher;
import com.example.mz_focusnews.NewsDB.News;
import com.example.mz_focusnews.NewsDB.RetrofitClient;
import com.example.mz_focusnews.adapter.NewsAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryFragment extends Fragment {

    private String user_id;

    private RecyclerView recyclerView;
    private NewsAdapter newsAdapter;
    private ProgressBar progressBar;
    private List<News> newsList;
    private NavController navController;
    private Map<String, UserSession> userSessions;
    private Button selectedButton; // 현재 선택된 버튼을 추적
    private String currentCategory = "politics"; // 현재 선택된 카테고리
    private String currentSortOption = "latest"; // 기본 정렬 옵션
    private NewsFetcher newsFetcher; // 뉴스 페처 객체

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        userSessions = new HashMap<>();

        SharedPreferences sp = getActivity().getSharedPreferences("UserData", Context.MODE_PRIVATE);
        user_id = sp.getString("user_id", null); // 기본값으로 null 설정

        // 로그 추가하여 user_id 확인
        Log.d("CategoryFragment", "User ID: " + user_id);

        // 뉴스 페처 초기화
        newsList = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            newsAdapter = new NewsAdapter(getActivity(), newsList, news -> handleNewsClick(news));
        }
        newsFetcher = new NewsFetcher(getActivity(), newsAdapter, newsList);

        // RecyclerView 초기화
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(newsAdapter);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category, container, false);

        // RecyclerView 참조를 먼저 얻은 다음 어댑터를 설정
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // 어댑터 초기화 및 설정
        newsAdapter = new NewsAdapter(getActivity(), newsList, news -> {
            // 클릭 이벤트 처리 로직
            handleNewsClick(news);
        });
        recyclerView.setAdapter(newsAdapter);

        // 버튼 및 기타 UI 컴포넌트 초기화
        initializeUIComponents(view);

        Button politicsButton = view.findViewById(R.id.politics);
        Button economyButton = view.findViewById(R.id.economy);
        Button societyButton = view.findViewById(R.id.society);
        Button keywordButton = view.findViewById(R.id.area); // 키워드 버튼 수정
        Button recruitmentButton = view.findViewById(R.id.recruitment);
        Button scienceButton = view.findViewById(R.id.science);
        Button entertainmentButton = view.findViewById(R.id.entertainment);
        Button sportsButton = view.findViewById(R.id.sports);

        progressBar = view.findViewById(R.id.progressBar); // 로딩 인디케이터

        Spinner sortSpinner = view.findViewById(R.id.sortSpinner);
        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        currentSortOption = "latest";
                        break;
                    case 1:
                        currentSortOption = "default";
                        break;
                    case 2:
                        currentSortOption = "views";
                        break;
                }
                loadNewsByCategory(currentCategory); // 정렬 기준 변경 시 데이터 다시 로드
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        politicsButton.setOnClickListener(v -> {
            currentCategory = "politics";
            loadNewsByCategory(currentCategory);
            updateSelectedButton(politicsButton);
        });
        economyButton.setOnClickListener(v -> {
            currentCategory = "economy";
            loadNewsByCategory(currentCategory);
            updateSelectedButton(economyButton);
        });
        societyButton.setOnClickListener(v -> {
            currentCategory = "society";
            loadNewsByCategory(currentCategory);
            updateSelectedButton(societyButton);
        });
        keywordButton.setOnClickListener(v -> {
            // 키워드 버튼 클릭 시
            newsFetcher.fetchUserKeywordAndNews(user_id); // 유저 ID 사용
            updateSelectedButton(keywordButton);
        });
        recruitmentButton.setOnClickListener(v -> {
            currentCategory = "recruitment";
            loadNewsByCategory(currentCategory);
            updateSelectedButton(recruitmentButton);
        });
        scienceButton.setOnClickListener(v -> {
            currentCategory = "science";
            loadNewsByCategory(currentCategory);
            updateSelectedButton(scienceButton);
        });
        entertainmentButton.setOnClickListener(v -> {
            currentCategory = "entertainment";
            loadNewsByCategory(currentCategory);
            updateSelectedButton(entertainmentButton);
        });
        sportsButton.setOnClickListener(v -> {
            currentCategory = "sports";
            loadNewsByCategory(currentCategory);
            updateSelectedButton(sportsButton);
        });

        // 초기 화면에 정치 뉴스를 로드하고 버튼 색상 업데이트
        loadNewsByCategory(currentCategory);
        updateSelectedButton(politicsButton);

        return view;
    }

    private void handleNewsClick(News news) {
        // 로그 추가하여 user_id와 news 값을 확인
        Log.d("handleNewsClick", "User ID: " + user_id);
        Log.d("handleNewsClick", "News ID: " + news.getNewsId());

        sendNewsItemToServer(getContext(), news);
        logUserInteraction(getContext(), userSessions, user_id, news);

        Bundle bundle = new Bundle();
        bundle.putInt("newsId", news.getNewsId());
        Log.d("throw newsId from category", String.valueOf(news.getNewsId()));

        NavHostFragment.findNavController(CategoryFragment.this)
                .navigate(R.id.action_categoryFragment_to_contentFragment, bundle);
    }

    private void initializeUIComponents(View view) {
        // 버튼들 초기화 및 이벤트 리스너 할당
        Button politicsButton = view.findViewById(R.id.politics);
        politicsButton.setOnClickListener(v -> loadNewsByCategory("politics"));
        // 나머지 버튼들도 비슷한 방식으로 초기화
    }

    private void loadNewsByCategory(String category) {
        progressBar.setVisibility(View.VISIBLE); // 로딩 인디케이터 표시

        RetrofitClient.getInstance().getNewsApi().getNewsByCategory(category, currentSortOption).enqueue(new Callback<List<News>>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(Call<List<News>> call, Response<List<News>> response) {
                progressBar.setVisibility(View.GONE); // 로딩 인디케이터 숨기기

                if (response.isSuccessful() && response.body() != null) {
                    List<News> newsList = response.body();
                    newsAdapter.updateNews(newsList);
                } else {
                    // 응답이 성공적이지 않을 때
                    ResponseBody errorBody = response.errorBody();
                    Toast.makeText(getContext(), "뉴스를 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<News>> call, Throwable t) {
                progressBar.setVisibility(View.GONE); // 로딩 인디케이터 숨기기

                // 요청 실패 시
                Toast.makeText(getContext(), "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateSelectedButton(Button newSelectedButton) {
        // 이전에 선택된 버튼의 텍스트 색상 초기화
        if (selectedButton != null) {
            selectedButton.setTextColor(getResources().getColor(android.R.color.black));
        }
        // 새로운 선택된 버튼의 텍스트 색상 변경
        newSelectedButton.setTextColor(getResources().getColor(android.R.color.holo_blue_light));
        selectedButton = newSelectedButton;
    }
}
