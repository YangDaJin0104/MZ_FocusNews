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

import com.example.mz_focusnews.data.DatabaseManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.os.AsyncTask;


public class MainActivity extends AppCompatActivity {

    // 하단 네비게이션 바를 위한 변수
    private FragmentManager fragmentManager = getSupportFragmentManager();
    ContentFragment contentFragment;
    CategoryFragment categoryFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup); // xml 파일 에뮬레이터 테스트

        // AsyncTask 실행
        new DatabaseManager().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
}
