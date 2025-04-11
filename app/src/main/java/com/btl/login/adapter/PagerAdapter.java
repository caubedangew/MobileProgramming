package com.btl.login.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.btl.login.fragments.ClassFragment;
import com.btl.login.fragments.SubjectFragment;
import com.btl.login.fragments.TeacherFragment;

public class PagerAdapter extends FragmentStateAdapter {

    public PagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new TeacherFragment();
            case 1:
                return new ClassFragment();
            case 2:
                return new SubjectFragment();
            default:
                return new TeacherFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3; // Số lượng trang
    }
}