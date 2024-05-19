package com.example.mz_focusnews;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mz_focusnews.NewsCrawling.NewsAdapter;
import com.example.mz_focusnews.NewsCrawling.NewsArticle;
import com.example.mz_focusnews.NewsCrawling.NewsScraper;
import com.example.mz_focusnews.NewsCrawling.NewsScraperCallback;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private WebView webView;
    private NewsAdapter adapter;
    private ArrayList<NewsArticle> articles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        webView = findViewById(R.id.webView);

        articles = new ArrayList<>();
        adapter = new NewsAdapter(this, articles);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        setupWebView();

        fetchNews();
    }

    private void setupWebView() {
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webView.setWebChromeClient(new WebChromeClient());
    }

    private void fetchNews() {
        NewsScraper scraper = new NewsScraper(this);
        scraper.fetchNews(new NewsScraperCallback() {
            @Override
            public void onSuccess(List<NewsArticle> articles) {
                runOnUiThread(() -> {
                    adapter.updateData(new ArrayList<>(articles));
                    recyclerView.setVisibility(View.VISIBLE);
                    webView.setVisibility(View.GONE);
                });
            }

            @Override
            public void onFailure(Exception exception) {
                // 예외 처리 코드 추가
            }
        });
    }

    public void showArticleInWebView(String url) {
        recyclerView.setVisibility(View.GONE);
        webView.setVisibility(View.VISIBLE);
        webView.loadUrl(url);
    }
}


