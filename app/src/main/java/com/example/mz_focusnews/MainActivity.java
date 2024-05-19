package com.example.mz_focusnews;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private NavController navController;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // 액티비티의 메인 레이아웃 파일을 사용하도록 변경

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        NavigationUI.setupWithNavController(bottomNavigationView, navController);
    }
}

//        // RecyclerView 설정
//        recyclerView = findViewById(R.id.recyclerView);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        newsAdapter = new NewsAdapter(this, new ArrayList<>());
//        recyclerView.setAdapter(newsAdapter);
//
//        // NewsScraper 인스턴스 생성
//        newsScraper = new NewsScraper(this);
//
//        // 뉴스 기사 가져오기
//        fetchArticles();
//    }

//    private void fetchArticles() {
//        newsScraper.fetchNews(new NewsScraperCallback() {
//            @Override
//            public void onSuccess(ArrayList<NewsArticle> articles) {
//                // UI 스레드에서 RecyclerView 데이터 업데이트
//                runOnUiThread(() -> newsAdapter.updateData(articles));
//            }
//
//            @Override
//            public void onFailure(Exception e) {
//                // 오류 메시지 표시
//                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Failed to fetch news: " + e.getMessage(), Toast.LENGTH_LONG).show());
//            }
//        });
//    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        if (newsScraper != null) {
//            newsScraper.shutdown(); // 스크레이퍼 리소스 정리
//
//        }
//    }
//}
