package com.example.mz_focusnews;

import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import com.example.mz_focusnews.NewsCrawling.*;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private RecyclerView recyclerView;
    private WebView webView;
    private NewsAdapter newsAdapter;
    private NewsScraper newsScraper;

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
            newsScraper.shutdown();
        }
    }
}