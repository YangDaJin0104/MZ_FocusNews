package com.example.mz_focusnews;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import com.example.mz_focusnews.NewsCrawling.*;
import com.example.mz_focusnews.Quiz.CSVFileReader;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CSVFileReader csvFileReader = new CSVFileReader();

        // CSV 파일을 읽어옴
        List<String[]> csvData = csvFileReader.readCSVFile(this, "quiz.csv");

        // 읽어온 데이터 출력
        for (String[] line : csvData) {
            StringBuilder lineBuilder = new StringBuilder();
            for (String value : line) {
                lineBuilder.append(value).append(", ");
            }
            Log.d(TAG, "CSV Read: " + lineBuilder.toString());
        }
    }
    /*private RecyclerView recyclerView;
    private NewsAdapter newsAdapter;
    private NewsScraper newsScraper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_item_news); // 액티비티의 메인 레이아웃 파일을 사용하도록 변경

        // RecyclerView 설정
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        newsAdapter = new NewsAdapter(this, new ArrayList<>());
        recyclerView.setAdapter(newsAdapter);

        // NewsScraper 인스턴스 생성
        newsScraper = new NewsScraper(this);

        // 뉴스 기사 가져오기
        fetchArticles();
    }

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (newsScraper != null) {
            newsScraper.shutdown(); // 스크레이퍼 리소스 정리
        }
    }*/
}