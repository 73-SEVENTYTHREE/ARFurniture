package com.google.ar.sceneform.samples.ARFurniture;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class HomepageActivity extends AppCompatActivity {

    ViewPager viewPager;
    BottomNavigationView bottomNavigationView;
    TextView textView;

    List<Fragment>fragments;
    MenuItem menuItem;

    NavigationView navigationView;
    static ShoppingCartActivity shoppingPage = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);



        /*
        //接收用户名并显示在前端,有问题。。。
        Intent intent = getIntent();
        String username = intent.getStringExtra("username");
        textView = findViewById(R.id.myname);
        textView.setText(username);
        */
        viewPager = findViewById(R.id.viewPager);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        fragments = new ArrayList<>();
        fragments.add(new FragmentSearch());
        fragments.add(new FragmentShopping());
        fragments.add(new FragmentPerson());

        myAdapter adapter = new myAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(adapter);

        shoppingPage = new ShoppingCartActivity();
        //处理底部导航点击事件
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){
                    case R.id.search:
                        viewPager.setCurrentItem(0);
                        break;
                    case R.id.shopping:
                        Intent intent = new Intent(HomepageActivity.this, ShoppingCartActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.person:
                        viewPager.setCurrentItem(2);
                        break;
                    default:
                        break;
                }

                return true;
            }
        });


        //处理侧边导航点击事件
        navigationView = findViewById(R.id.nav);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                String menuItem = item.getTitle().toString();
                Intent intent;
                switch (menuItem){
                    case "个人信息":
                        viewPager.setCurrentItem(2);
                        break;
                    case "购物计划":
                        viewPager.setCurrentItem(1);
                        break;
                    case "我的订单":
                        intent = new Intent(HomepageActivity.this, MyOrderActivity.class);
                        startActivity(intent);
                        break;
                    case "重置密码":
                        intent = new Intent(HomepageActivity.this, ResetPwdActivity.class);
                        startActivity(intent);
                        break;
                    default:
                        break;
                }

                return true;
            }
        });


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (menuItem == null){
                    menuItem = bottomNavigationView.getMenu().getItem(0);
                }

                //将上次设为false，等待下次选择
                menuItem.setChecked(false);
                menuItem = bottomNavigationView.getMenu().getItem(position);
                menuItem.setChecked(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });




    }

    private class myAdapter extends FragmentPagerAdapter{

        private List<Fragment>fragments;

        public myAdapter(@NonNull FragmentManager fm, List<Fragment>fragments) {
            super(fm);
            this.fragments = fragments;
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
    }

}