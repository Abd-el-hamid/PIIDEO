package com.abdel.dell.piideo.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.abdel.dell.piideo.fragment.ChatsFragment;
import com.abdel.dell.piideo.fragment.ContactsFragment;
import com.abdel.dell.piideo.fragment.MyRequestsFragment;

import java.util.List;

/**
 * Created by Abdel on 25/02/2017.
 */

public class TabsPagerAdapter extends FragmentStatePagerAdapter {

    private List<Fragment> fragmentList;
    private List<String> titleList;


    public TabsPagerAdapter(FragmentManager fm, List<Fragment> fragmentList, List<String> titleList) {
        super(fm);
        this.fragmentList = fragmentList;
        this.titleList = titleList;
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titleList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }
}
