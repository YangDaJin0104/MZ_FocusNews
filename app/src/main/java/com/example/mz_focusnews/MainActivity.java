package com.example.mz_focusnews;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mz_focusnews.NewsCrawling.NewsAdapter;
import com.example.mz_focusnews.NewsCrawling.NewsArticle;
import com.example.mz_focusnews.NewsCrawling.NewsScraper;
import com.example.mz_focusnews.NewsCrawling.NewsScraperCallback;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    // 뉴스 크롤링을 위한 변수
    private RecyclerView recyclerView;
    private NewsAdapter newsAdapter;
    private NewsScraper newsScraper;


    // 하단 네비게이션 바를 위한 변수
    private FragmentManager fragmentManager = getSupportFragmentManager();
    ContentFragment contentFragment;
    CategoryFragment categoryFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_container), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

/*        contentFragment = new ContentFragment();
        categoryFragment = new CategoryFragment();

        // RecyclerView 설정
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        newsAdapter = new NewsAdapter(this, new ArrayList<>());
        recyclerView.setAdapter(newsAdapter);
        // NewsScraper 인스턴스 생성
        newsScraper = new NewsScraper(this);
        // 뉴스 기사 가져오기
        fetchArticles();*/


//        // 하단 네비게이션 바 ----
//
//        FragmentTransaction transaction = fragmentManager.beginTransaction();
//        transaction.replace(R.id.bottom_navigation_view, contentFragment).commitAllowingStateLoss();
//
//        BottomNavigationView bottomNavigation = findViewById(R.id.bottom_navigation_view);
//        bottomNavigation.setOnItemSelectedListener(new ItemSelectedListener());
//    }

    // 하단 네비게이션 바 .. 하는중
//     class ItemSelectedListener implements BottomNavigationView.OnItemSelectedListener {
//        @Override
//        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//            FragmentTransaction transaction = fragmentManager.beginTransaction();
//
//            switch (item.getItemId()) {
////                case R.id.fragment_home:
////                    Toast.makeText(getApplicationContext(), "첫번째 탭", Toast.LENGTH_SHORT).show();
////                    getSupportFragmentManager().beginTransaction().replace(R.id.bottom_navigation_view, contentFragment).commit();
////                    return true;
//                case R.id.category:
//                    Toast.makeText(getApplicationContext(), "두번째 탭", Toast.LENGTH_SHORT).show();
//                    getSupportFragmentManager().beginTransaction().replace(R.id.bottom_navigation_view, categoryFragment).commit();
//                    return true;
//            }
//            return false;
//        }
//    }
        setContentView(R.layout.activity_login); // xml 파일 에뮬레이터 테스트
    }

/*    private void fetchArticles() {
        newsScraper.fetchNews(new NewsScraperCallback() {
            @Override
            public void onSuccess(ArrayList<NewsArticle> articles) {
                // UI 스레드에서 RecyclerView 데이터 업데이트
                runOnUiThread(() -> newsAdapter.updateData(articles));
            }

            @Override
            public void onFailure(Exception e) {
                // 오류 메시지 표시
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Failed to fetch news: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (newsScraper != null) {
            newsScraper.shutdown(); // 스크레이퍼 종료
        }
    }*/
}
