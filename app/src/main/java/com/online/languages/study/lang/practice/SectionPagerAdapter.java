package com.online.languages.study.lang.practice;

import android.util.SparseArray;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.online.languages.study.lang.fragments.CustomDataFragment;
import com.online.languages.study.lang.fragments.CustomTabFragment2;
import com.online.languages.study.lang.fragments.CustomTabFragment3;


public class SectionPagerAdapter extends FragmentStatePagerAdapter {
    private int mNumOfTabs;
    private SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();

    public SectionPagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return new SectionFragment();
            case 1:
                return new PracticeFragment();
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

    public Fragment getRegisteredFragment(int position) {
        return registeredFragments.get(position);
    }

    public Fragment getFragmentOne() {
        return registeredFragments.get(0);
    }
    public Fragment getFragmentTwo() {
        return registeredFragments.get(1);
    }

}