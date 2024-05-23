package com.example.mz_focusnews;

import android.os.Build;
import android.os.Bundle;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mz_focusnews.NewsDB.News;
import com.example.mz_focusnews.NewsDB.NewsAdapter;
import com.example.mz_focusnews.NewsDB.RetrofitClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryFragment extends Fragment {

    private RecyclerView recyclerView;
    private NewsAdapter newsAdapter;
    private ProgressBar progressBar;
    private Button selectedButton; // 현재 선택된 버튼을 추적
    private String currentCategory = "politics"; // 현재 선택된 카테고리
    private String currentSortOption = "latest"; // 기본 정렬 옵션

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        newsAdapter = new NewsAdapter(new ArrayList<>());
        recyclerView.setAdapter(newsAdapter);

        Button politicsButton = view.findViewById(R.id.politics);
        Button economyButton = view.findViewById(R.id.economy);
        Button societyButton = view.findViewById(R.id.society);
        Button areaButton = view.findViewById(R.id.area);
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
        areaButton.setOnClickListener(v -> {
            currentCategory = "area";
            loadNewsByCategory(currentCategory);
            updateSelectedButton(areaButton);
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
