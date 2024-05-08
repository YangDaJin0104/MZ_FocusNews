package com.example.mz_focusnews;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

//    private NavController navController;
//    private BottomNavigationView bottomNavigationView;
//
//    // 하단 네비게이션 바를 위한 변수
//    private FragmentManager fragmentManager = getSupportFragmentManager();
//    ContentFragment contentFragment;
//    CategoryFragment categoryFragment;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        bottomNavigationView = findViewById(R.id.bottom_navigation);
//        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
//
//        NavigationUI.setupWithNavController(bottomNavigationView, navController);
//
//
//        // Bundle을 가져와서 HomeFragment에 전달
//        Bundle userDataBundle = getIntent().getBundleExtra("userData");
//        if (userDataBundle != null) {
//            HomeFragment homeFragment = new HomeFragment();
//            homeFragment.setArguments(userDataBundle);
//
//            getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.nav_host_fragment, homeFragment)
//                    .commit();
//        }
//    }
}
