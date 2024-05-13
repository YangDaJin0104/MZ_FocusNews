package com.example.mz_focusnews;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mz_focusnews.NewsCrawling.*;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private NewsAdapter newsAdapter;
    private NewsScraper newsScraper;

    // 위치정보
    private LocationAddress locationAddress;
    private NewsSourceConfig newsSourceConfig;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // 올바른 메인 레이아웃을 사용하도록 수정

        // RecyclerView 설정
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        newsAdapter = new NewsAdapter(this, new ArrayList<>());
        recyclerView.setAdapter(newsAdapter);

        // NewsScraper 인스턴스 생성
        newsScraper = new NewsScraper(this);

        // 뉴스 기사 가져오기
        fetchArticles();

        // 위치정보 권한설정
        if (checkPermission()) {
            initLocationAddress();
        } else {
            requestPermissions();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void fetchArticles() {
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

/*    private void setCategoryButtonListeners() {
        findViewById(R.id.politics).setOnClickListener(v -> updateCategory("politics"));
        findViewById(R.id.economy).setOnClickListener(v -> updateCategory("economy"));
        findViewById(R.id.society).setOnClickListener(v -> updateCategory("society"));
        findViewById(R.id.area).setOnClickListener(v -> updateCategory("area"));
        findViewById(R.id.Recruitment_information).setOnClickListener(v -> updateCategory("recruitment"));
        findViewById(R.id.science_technology).setOnClickListener(v -> updateCategory("science_technology"));
        findViewById(R.id.entertainment).setOnClickListener(v -> updateCategory("entertainment"));
        findViewById(R.id.sports).setOnClickListener(v -> updateCategory("sports"));
    }
    private void initializeNewsScraper(String category) {
        NewsSourceConfig config = new NewsSourceConfig();
        config.setCategory(category);
        String url = config.getNewsSourceUrl();
        newsScraper = new NewsScraper(this, url);
        fetchArticles(category);  // 기존 fetchArticles 메소드 호출을 fetchNews로 변경
    }

    private void updateCategory(String category) {
        Toast.makeText(this, "Category changed to: " + category, Toast.LENGTH_SHORT).show();
        initializeNewsScraper(category);
    }*/

    private void initLocationAddress() {
        newsSourceConfig = new NewsSourceConfig();
        locationAddress = new LocationAddress(this, newsSourceConfig);
        locationAddress.getAddress(new LocationAddress.AddressCallback() {
            @Override
            public void onAddressRetrieved(String address) {
                System.out.println("Address: " + address);
            }

            @Override
            public void onError(String errorMessage) {
                System.out.println("Error: " + errorMessage);
            }
        });
    }

    private boolean checkPermission() {
        int fineLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int coarseLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        return fineLocationPermission == PackageManager.PERMISSION_GRANTED && coarseLocationPermission == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[] {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        }, 100);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            initLocationAddress();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (newsScraper != null) {
            newsScraper.shutdown(); // 스크레이퍼 리소스 정리
        }
    }
}