package com.example.reforyapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.reforyapp.Fragment.FragmentAdd;
import com.example.reforyapp.Fragment.FragmentHistory;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private MenuItem menuItem;
    private BottomNavigationView bottomNavigationView;

    //製作BottomNavigationView按下個方法:
    private BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            //BottomNavigationView按下時判斷Menu的ID，讓ViewPaper跳去相對應的Fragment:
            int id= viewPager.getId();
            if(id == R.id.navAdd)
                viewPager.setCurrentItem(0);
            else if(id == R.id.navHistory)
                viewPager.setCurrentItem(1);
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //設定BottomNavigationView的按下事件監聽器:
        bottomNavigationView = findViewById(R.id.navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);

        //設定ViewPaper的Adapter:
        MainPagerAdapter adapter = new MainPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new FragmentAdd(), "Add");
        adapter.addFragment(new FragmentHistory(), "History");
        viewPager = findViewById(R.id.viewPagerMain);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(2);

        //設定ViewPaper的事件監聽器:
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }
            //ViewPaper選擇到其他頁面時:
            @Override
            public void onPageSelected(int position) {
                // Step06-將相對應的bottomNavigationView選項選取:
                menuItem = bottomNavigationView.getMenu().getItem(position).setChecked(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
}