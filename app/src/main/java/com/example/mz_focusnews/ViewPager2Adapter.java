package com.example.mz_focusnews;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.mz_focusnews.newspager.MonthlyNewsFragment;
import com.example.mz_focusnews.newspager.DailyNewsFragment;
import com.example.mz_focusnews.newspager.WeeklyNewsFragment;

public class ViewPager2Adapter extends FragmentStateAdapter {

    public ViewPager2Adapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new DailyNewsFragment();
            case 1:
                return new MonthlyNewsFragment();
            case 2:
                return new WeeklyNewsFragment();
            default:
                return null;
        }
    }

    @Override
    public int getItemCount() {
        return 3;   // 페이지 수
    }
}
