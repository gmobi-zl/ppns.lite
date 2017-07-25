package com.poponews.lite.ui;

/**
 * Created by zl on 2016/10/21.
 */
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.momock.util.Logger;

import java.util.List;

public class FragmentAdapter extends FragmentStatePagerAdapter {
    private List<ListFragment> mFragments;
    private List<String> mTitles;

    public FragmentAdapter(FragmentManager fm, List<ListFragment> fragments, List<String> titles) {
        super(fm);
        mFragments = fragments;
        mTitles = titles;
    }

    @Override
    public ListFragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles.get(position);
    }

    public void refreshFragmentAtIndex(int pos){
        try {
            ListFragment lFragment = mFragments.get(pos);
            if (lFragment != null)
                lFragment.refreshRecyclerView();
        } catch(Exception e){
            Logger.error(e);
        }
    }
}
