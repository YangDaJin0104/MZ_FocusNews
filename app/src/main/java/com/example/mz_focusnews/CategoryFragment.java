package com.example.mz_focusnews;

import static com.example.mz_focusnews.NewsUtils.logUserInteraction;
import static com.example.mz_focusnews.NewsUtils.sendNewsItemToServer;

import android.os.Build;
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

        Log.d("CategoryFragment", "User ID: " + user_id);

        newsList = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            newsAdapter = new NewsAdapter(getActivity(), newsList, news -> handleNewsClick(news));
        }
        newsFetcher = new NewsFetcher(getActivity(), newsAdapter, newsList);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(newsAdapter);

        // ProgressBar 초기화
        progressBar = view.findViewById(R.id.progressBar);

        loadNewsByCategory(currentCategory);
        updateSelectedButton(view.findViewById(R.id.politics));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        newsAdapter = new NewsAdapter(getActivity(), newsList, news -> handleNewsClick(news));
        recyclerView.setAdapter(newsAdapter);

        initializeUIComponents(view);

        Button politicsButton = view.findViewById(R.id.politics);
        Button economyButton = view.findViewById(R.id.economy);
        Button societyButton = view.findViewById(R.id.society);
        Button keywordButton = view.findViewById(R.id.user_keyword);
        Button recruitmentButton = view.findViewById(R.id.recruitment);
        Button scienceButton = view.findViewById(R.id.science);
        Button entertainmentButton = view.findViewById(R.id.entertainment);
        Button sportsButton = view.findViewById(R.id.sports);

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
                loadNewsByCategory(currentCategory);
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
            currentCategory = "keyword";
            loadNewsByCategory(currentCategory);
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

        return view;
    }

    private void handleNewsClick(News news) {
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
        Button politicsButton = view.findViewById(R.id.politics);
        politicsButton.setOnClickListener(v -> loadNewsByCategory("politics"));
    }

    private void loadNewsByCategory(String category) {
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }

        if ("keyword".equals(category)) {
            // 키워드에 맞는 뉴스 불러오기
            newsFetcher.fetchUserKeywordsAndNews(user_id, new NewsFetcher.NewsFetchListener() {
                @Override
                public void onFetchCompleted(List<News> fetchedNews) {
                    newsList.clear();
                    newsList.addAll(fetchedNews);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        newsAdapter.updateNews(newsList);
                    }
                    if (progressBar != null) {
                        progressBar.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onFetchFailed() {
                    if (progressBar != null) {
                        progressBar.setVisibility(View.GONE);
                    }
                    Toast.makeText(getContext(), "키워드 뉴스를 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
                }
            });
            return;
        }

        RetrofitClient.getInstance().getNewsApi().getNewsByCategory(category, currentSortOption).enqueue(new Callback<List<News>>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(Call<List<News>> call, Response<List<News>> response) {
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }

                if (response.isSuccessful() && response.body() != null) {
                    newsList.clear();
                    newsList.addAll(response.body());
                    newsAdapter.updateNews(newsList);
                } else {
                    ResponseBody errorBody = response.errorBody();
                    Toast.makeText(getContext(), "뉴스를 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<News>> call, Throwable t) {
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    private void updateSelectedButton(Button newSelectedButton) {
        if (selectedButton != null) {
            selectedButton.setTextColor(getResources().getColor(android.R.color.black));
        }
        newSelectedButton.setTextColor(getResources().getColor(android.R.color.holo_blue_light));
        selectedButton = newSelectedButton;
    }
}