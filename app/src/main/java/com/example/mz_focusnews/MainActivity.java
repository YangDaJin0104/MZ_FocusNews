package com.example.mz_focusnews;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.example.mz_focusnews.NewsDB.ImageGenerator;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.android.volley.VolleyError;
import com.example.mz_focusnews.NewsSummary.Summary;
import com.example.mz_focusnews.NewsSummary.SummaryUtils;
import com.example.mz_focusnews.RelatedNews.FetchCompleteListener;
import com.example.mz_focusnews.RelatedNews.NewsDataFetcher;
import com.example.mz_focusnews.RelatedNews.NewsDataStore;
import com.example.mz_focusnews.RelatedNews.RelatedNewsUtils;
import com.example.mz_focusnews.RelatedNews.NewsData;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

public class MainActivity extends AppCompatActivity {
    private NavController navController;
    private BottomNavigationView bottomNavigationView;
    private NewsDataStore newsDataStore;
    SummaryUtils summaryUtils;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final AtomicBoolean isDestroyed = new AtomicBoolean(false);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fetchAllNewsIdsAndProcess();

        newsDataStore = new NewsDataStore();
        summaryUtils = new SummaryUtils(this);
        NewsDataFetcher fetcher = new NewsDataFetcher(newsDataStore, this);

        Context context = this;
        fetcher.fetchAllNews(this, new FetchCompleteListener() {
            @Override
            public void onFetchComplete() {
                executorService.execute(() -> {
                    try {
                        newsDataStore.calculateAndSetRelatedArticles(context);
                        updateDatabaseWithRelatedNews();  // 데이터베이스 업데이트 호출
                    } catch (IOException e) {
                        Log.e("MainActivity", "Error calculating related articles", e);
                    }
                });

            }
            @Override
            public void onFetchFailed(VolleyError error) {
                Log.e("MainActivity", "News fetch failed", error);
            }
        });

        cleanDB();

        new ImageGenerator(MainActivity.this).execute();

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        NavigationUI.setupWithNavController(bottomNavigationView, navController);


        // login. register 프래그먼트에서 네비게이션 바 숨기기
        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
                // 로그인과 회원가입 프래그먼트 ID에 따라 하단 네비게이션 바 표시 결정
                if(destination.getId() == R.id.loginFragment || destination.getId() == R.id.registerFragment) {
                    bottomNavigationView.setVisibility(View.GONE); // 네비게이션 바 숨기기
                } else {
                    bottomNavigationView.setVisibility(View.VISIBLE); // 네비게이션 바 표시하기
                }
            }
        });

        NavigationUI.setupWithNavController(bottomNavigationView, navController);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isDestroyed.set(true);
        executorService.shutdownNow(); // 앱 종료 시 스레드 풀 종료
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (executorService != null) {
            executorService.shutdownNow();  // 모든 작업을 즉시 중지하고 스레드 풀을 종료
        }
    }

    private void fetchAllNewsIdsAndProcess() {
        executorService.execute(() -> {
            NewsDataAdapter adapter = new NewsDataAdapter(this);
            adapter.fetchAllNewsId(this, new NewsDataCallback() {
                @Override
                public void onDataFetched(int newsId, String content) {
                    if (!isDestroyed.get()){
                        runOnUiThread(() -> performSummaryAndUpload(newsId, content));
                    }
                }
                @Override
                public void onError(String error) {
                    runOnUiThread(() -> Log.e("MainActivity", "Error fetching data: " + error));
                }
            });
        });
    }

    private void updateDatabaseWithRelatedNews() {
        for (NewsData item : newsDataStore.getAllNewsItems()) {
            RelatedNewsUtils.updateRelatedNews(this, item.getId(), item.getRelated1(), item.getRelated2());
        }
    }

    private void performSummaryAndUpload(int newsId, String content) {
        Context context = this;

        executorService.execute(() -> {
            String summary = Summary.chatGPT_summary(context, content);
            if (summary != null && !isDestroyed.get()) {

                summaryUtils.sendSummaryToServer(this, newsId, summary);
            }
        });
    }

    private void cleanDB() {
        executorService.execute(() -> {
            summaryUtils.deleteBadData(this);
            Log.d("cleanDB", "clean success");

        });
    }

}



