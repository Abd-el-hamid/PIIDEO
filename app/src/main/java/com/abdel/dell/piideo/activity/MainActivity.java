package com.abdel.dell.piideo.activity;

import android.app.Dialog;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.abdel.dell.piideo.R;
import com.abdel.dell.piideo.adapter.TabsPagerAdapter;
import com.abdel.dell.piideo.fragment.ChatsFragment;
import com.abdel.dell.piideo.fragment.ContactsFragment;
import com.abdel.dell.piideo.fragment.MyRequestsFragment;
import com.abdel.dell.piideo.fragment.PublicWallFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ViewPager viewPager;
    private TabsPagerAdapter tabsPagerAdapter;
    private List<Fragment> fragmentList = new ArrayList<>();
    private List<String> titleList = new ArrayList<>();
    private TabLayout tabLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        prepareDataResource();

        viewPager.setAdapter(tabsPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        viewPager.setCurrentItem(1);
    }

    private void init() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("PIIDEO");
        viewPager = (ViewPager)findViewById(R.id.viewPager);
        tabsPagerAdapter = new TabsPagerAdapter(getSupportFragmentManager(), fragmentList, titleList);
        tabLayout = (TabLayout)findViewById(R.id.tabLayout);
    }

    private void prepareDataResource() {
        addData(new ChatsFragment(), "Chats");
        addData(new ContactsFragment(), "Contacts");
        addData(new MyRequestsFragment(), "My requests");
        addData(new PublicWallFragment(), "Public wall");

    }

    private void addData(Fragment fragment, String title) {
        fragmentList.add(fragment);
        titleList.add(title);
    }
}
