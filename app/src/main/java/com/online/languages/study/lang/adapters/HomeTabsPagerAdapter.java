package com.online.languages.study.lang.adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.online.languages.study.lang.fragments.HomeFragment1;
import com.online.languages.study.lang.fragments.HomeFragment2;
import com.online.languages.study.lang.recommend.TaskFragment;


public class HomeTabsPagerAdapter extends FragmentPagerAdapter {

    private int mNumOfTabs;
    private SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();

    public HomeTabsPagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return new HomeFragment1();
            case 1:
                return new HomeFragment2();
            case 2:
                return new TaskFragment();

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        registeredFragments.put(position, fragment);
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        registeredFragments.remove(position);
        super.destroyItem(container, position, object);
    }

    public Fragment getFragmentOne(int position) {
        return registeredFragments.get(0);
    }

    public Fragment getFragmentTwo(int position) {
        return registeredFragments.get(1);
    }

    public Fragment getRegisteredFragment(int position) {
        return registeredFragments.get(position);
    }
}