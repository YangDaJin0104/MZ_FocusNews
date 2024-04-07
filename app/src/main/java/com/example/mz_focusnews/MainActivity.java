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

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

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

        contentFragment = new ContentFragment();
        categoryFragment = new CategoryFragment();

//        // 하단 네비게이션 바 ----
//
//        FragmentTransaction transaction = fragmentManager.beginTransaction();
//        transaction.replace(R.id.bottom_navigation_view, contentFragment).commitAllowingStateLoss();
//
//        BottomNavigationView bottomNavigation = findViewById(R.id.bottom_navigation_view);
//        bottomNavigation.setOnItemSelectedListener(new ItemSelectedListener());
    }

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
}
