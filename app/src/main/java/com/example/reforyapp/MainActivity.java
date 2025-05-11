package com.example.reforyapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.example.reforyapp.Fragment.FragmentAdd;
import com.example.reforyapp.Fragment.FragmentHistory;
import com.example.reforyapp.Adapter.MainPagerAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private MenuItem menuItem;
    private BottomNavigationView bottomNavigationView;

    // 製作BottomNavigationView按下的方法
    private BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            // BottomNavigationView按下時判斷Menu的ID，讓ViewPaper跳去相對應的Fragment
            int id= item.getItemId();
            if(id == R.id.navAdd)
                viewPager.setCurrentItem(0);
            else if(id == R.id.navHistory)
                viewPager.setCurrentItem(1);
            return true;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);

        // 設定ViewPaper的Adapter
        MainPagerAdapter adapter = new MainPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new FragmentAdd(), "Add");
        adapter.addFragment(new FragmentHistory(), "History");
        viewPager = findViewById(R.id.viewPagerMain);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(2);

        // 設定ViewPaper的事件監聽器
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }
            // ViewPaper選擇到其他頁面時
            @Override
            public void onPageSelected(int position) {
                // 將相對應的bottomNavigationView選項選取
                menuItem = bottomNavigationView.getMenu().getItem(position).setChecked(true);
                hideKeyboard();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
    // 隱藏鍵盤
    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }
}