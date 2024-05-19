<<<<<<< HEAD
=======

>>>>>>> 0290dd1f04e61399a8da043239b2a8d59cd08b0e
package com.example.mz_focusnews;

import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;


import com.example.mz_focusnews.NewsCrawling.*;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private RecyclerView recyclerView;
    private WebView webView;
    private NewsAdapter newsAdapter;
    private NewsScraper newsScraper;
    private NavController navController;
    private BottomNavigationView bottomNavigationView;

    // 하단 네비게이션 바를 위한 변수
    private FragmentManager fragmentManager = getSupportFragmentManager();
    ContentFragment contentFragment;
    CategoryFragment categoryFragment;

<<<<<<< HEAD
/*    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        webView = findViewById(R.id.webview);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        newsAdapter = new NewsAdapter(this, new ArrayList<>());
        newsAdapter.setOnItemClickListener(this);
        recyclerView.setAdapter(newsAdapter);

        newsScraper = new NewsScraper(this);

        fetchNews();
    }

    private void fetchNews() {
        newsScraper.fetchNews(new NewsScraperCallback() {
            @Override
            public void onSuccess(List<NewsArticle> articles) {
                newsAdapter.updateData(new ArrayList<>(articles));
            }

            @Override
            public void onFailure(Exception e) {
                Log.e(TAG, "Error fetching news", e);
                Toast.makeText(MainActivity.this, "Error fetching news", Toast.LENGTH_SHORT).show();
            }
        });
    }
=======
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // 액티비티의 메인 레이아웃 파일을 사용하도록 변경

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        // Bundle을 가져와서 HomeFragment에 전달
        Bundle userDataBundle = getIntent().getBundleExtra("userData");
        if (userDataBundle != null) {
            HomeFragment homeFragment = new HomeFragment();
            homeFragment.setArguments(userDataBundle);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.nav_host_fragment, homeFragment)
                    .commit();
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
    }

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
>>>>>>> 0290dd1f04e61399a8da043239b2a8d59cd08b0e

    @Override
    public void onItemClick(String link) {
        if (link != null && !link.isEmpty()) {
            webView.loadUrl(link);
        } else {
            Toast.makeText(this, "Invalid URL", Toast.LENGTH_SHORT).show();
        }
    }*/

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (newsScraper != null) {
<<<<<<< HEAD
            newsScraper.shutdown();
        }
    }
}
=======
            newsScraper.shutdown(); // 스크레이퍼 리소스 정리

        }
    }
}
>>>>>>> 0290dd1f04e61399a8da043239b2a8d59cd08b0e
