package com.example.mz_focusnews;

import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.mz_focusnews.NewsDB.*;

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

        politicsButton.setOnClickListener(v -> loadNewsByCategory("politics"));
        economyButton.setOnClickListener(v -> loadNewsByCategory("economy"));
        societyButton.setOnClickListener(v -> loadNewsByCategory("society"));
        areaButton.setOnClickListener(v -> loadNewsByCategory("area"));
        recruitmentButton.setOnClickListener(v -> loadNewsByCategory("recruitment"));
        scienceButton.setOnClickListener(v -> loadNewsByCategory("science"));
        entertainmentButton.setOnClickListener(v -> loadNewsByCategory("entertainment"));
        sportsButton.setOnClickListener(v -> loadNewsByCategory("sports"));

        return view;
    }
    private void loadNewsByCategory(String category) {
        progressBar.setVisibility(View.VISIBLE); // 로딩 인디케이터 표시

        RetrofitClient.getInstance().getNewsApi().getNewsByCategory(category).enqueue(new Callback<List<News>>() {
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
                    try {
                        Log.e("CategoryFragment", "Response error: " + errorBody.string());
                        Toast.makeText(getContext(), "뉴스를 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<News>> call, Throwable t) {
                progressBar.setVisibility(View.GONE); // 로딩 인디케이터 숨기기

                // 요청 실패 시
                Log.e("CategoryFragment", "Request failed: " + t.getMessage());
                Toast.makeText(getContext(), "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();

            }
        });
    }
}
