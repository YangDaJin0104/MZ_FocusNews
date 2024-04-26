package com.example.mz_focusnews;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity implements NewsScraperCallback{

    private TextView newsTextView;
    private NewsScraper newsScraper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //newsTextView = findViewById(R.id.newsTextView);

        newsScraper = new NewsScraper(this);
        newsScraper.scrapeNews();
    }
    @Override
    public void onNewsScraped(String newsData) {
        runOnUiThread(() -> {
            // 스크레이핑된 뉴스 데이터로 UI를 업데이트합니다.
            newsTextView.setText(newsData);
        });
    }
}