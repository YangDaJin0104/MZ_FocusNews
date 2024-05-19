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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        webView = findViewById(R.id.webview);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        newsAdapter = new NewsAdapter(this, new ArrayList<>());
        recyclerView.setAdapter(newsAdapter);

        newsScraper = new NewsScraper(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (newsScraper != null) {
            newsScraper.shutdown();
        }
    }
}
