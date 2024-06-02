package com.example.mz_focusnews;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.example.mz_focusnews.NewsDB.News;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.android.volley.VolleyError;
import com.example.mz_focusnews.RelatedNews.FetchCompleteListener;
import com.example.mz_focusnews.RelatedNews.NewsDataFetcher;
import com.example.mz_focusnews.RelatedNews.NewsDataStore;
import com.example.mz_focusnews.RelatedNews.RelatedNewsUtils;
import com.example.mz_focusnews.RelatedNews.NewsData;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

public class MainActivity extends AppCompatActivity {
    private NavController navController;
    private BottomNavigationView bottomNavigationView;
    private NewsDataStore newsDataStore;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final AtomicBoolean isDestroyed = new AtomicBoolean(false);

    private NotificationService notificationService; // 속보 알림 기능
    private Handler handler = new Handler();
    private final int POLLING_INTERVAL = 30000; // 30 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Summary 관련 파일들 삭제함에 따라 관련 메소드들도 삭제했습니당 (ming)

        // 속보 푸시 알림 초기화
        notificationService = new NotificationService(this);
        startNotificationPolling();

        // img_url 값이 null인 뉴스 이미지 생성 (title 기반)
        newsDataStore = new NewsDataStore();
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

        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        // 특정 프래그먼트에서 네비게이션 바 숨기기
        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
                // 로그인과 회원가입 프래그먼트 ID에 따라 하단 네비게이션 바 표시 결정
                int dest_id = destination.getId();
                if(dest_id == R.id.loginFragment || dest_id == R.id.registerFragment
                        || dest_id == R.id.changePasswordFragment || dest_id == R.id.findPasswordFragment
                        || dest_id == R.id.keywordChangeFragment || dest_id == R.id.keywordFragment) {
                    bottomNavigationView.setVisibility(View.GONE); // 네비게이션 바 숨기기
                } else {
                    bottomNavigationView.setVisibility(View.VISIBLE); // 네비게이션 바 표시하기
                }
            }
        });

        NavigationUI.setupWithNavController(bottomNavigationView, navController);
    }

    private void startNotificationPolling() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                notificationService.fetchNotifications();
                handler.postDelayed(this, POLLING_INTERVAL);
            }
        }, POLLING_INTERVAL);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isDestroyed.set(true);
        executorService.shutdownNow(); // 앱 종료 시 스레드 풀 종료
        handler.removeCallbacksAndMessages(null); // 핸들러의 모든 콜백 및 메시지 제거
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (executorService != null) {
            executorService.shutdownNow();  // 모든 작업을 즉시 중지하고 스레드 풀을 종료
        }
    }

    private void updateDatabaseWithRelatedNews() {
        for (NewsData item : newsDataStore.getAllNewsItems()) {
            RelatedNewsUtils.updateRelatedNews(this, item.getId(), item.getRelated1(), item.getRelated2());
        }
    }


    private void handleFilteredNews(List<News> filteredNewsList) {
        // 필터링된 뉴스 리스트를 사용하여 필요한 작업을 수행
        for (News news : filteredNewsList) {
            Log.d("FilteredNews", news.getTitle() + " - " + news.getSummary());
        }
    }

    // 뒤로가기 기능 정의
    @Override
    public void onBackPressed() {
        Fragment navHostFragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment != null) {
            Fragment currentFragment = navHostFragment.getChildFragmentManager().getPrimaryNavigationFragment();
            if (currentFragment instanceof OnBackPressedListener) {
                ((OnBackPressedListener) currentFragment).onBackPressed();
                return;
            }
        }
        super.onBackPressed();
    }
}
