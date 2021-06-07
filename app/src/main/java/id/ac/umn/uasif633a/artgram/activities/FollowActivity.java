package id.ac.umn.uasif633a.artgram.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

import id.ac.umn.uasif633a.artgram.R;
import id.ac.umn.uasif633a.artgram.fragments.FollowersFragment;
import id.ac.umn.uasif633a.artgram.fragments.FollowingFragment;

public class FollowActivity extends AppCompatActivity {
    private Bundle extras;
    private boolean following;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow);
        ViewPager viewPager = findViewById(R.id.activity_follow_view_pager);
        TabLayout tabLayout = findViewById(R.id.activity_follow_tab_layout);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragments(new FollowingFragment(), "Following");
        viewPagerAdapter.addFragments(new FollowersFragment(), "Followers");
        viewPager.setAdapter(viewPagerAdapter);

        extras = getIntent().getExtras();
        if (extras != null) {
            following = extras.getBoolean("FOLLOWING");
        }

        if(following == true){
            viewPager.setCurrentItem(0);
        } else {
            viewPager.setCurrentItem(1);
        }

        tabLayout.setupWithViewPager(viewPager);
    }

    public static class ViewPagerAdapter extends FragmentPagerAdapter{

        private ArrayList<Fragment> fragments;
        private ArrayList<String> titles;
        public ViewPagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
            this.fragments = new ArrayList<>();
            this.titles = new ArrayList<>();
        }

        void addFragments(Fragment fragment, String title){
            fragments.add(fragment);
            titles.add(title);
        }
        @NonNull
        @Override
        public Fragment getItem(int position) {

            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }
}