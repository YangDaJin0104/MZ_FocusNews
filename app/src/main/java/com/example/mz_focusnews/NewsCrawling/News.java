package com.example.mz_focusnews.NewsCrawling;

import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mz_focusnews.R;

import java.util.ArrayList;

public class News extends AppCompatActivity {
    private RecyclerView recyclerView;
    private NewsAdapter adapter;
    private NewsScraper newsScraper;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.list_item_news);      // 오류 발생해서 주석 처리. merge 시 주석 풀기

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NewsAdapter(this, new ArrayList<>());
        recyclerView.setAdapter(adapter);

        newsScraper = new NewsScraper(this);

        fetchArticles();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void fetchArticles() {
        newsScraper.fetchNews(new NewsScraperCallback() {
            @Override
            public void onSuccess(ArrayList<NewsArticle> articles) {
                runOnUiThread(() -> {
                    adapter.updateData(articles);
                });
            }

            @Override
            public void onFailure(Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(News.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        });
    }
}
