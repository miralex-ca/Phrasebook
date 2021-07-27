package com.online.languages.study.lang.adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.online.languages.study.lang.fragments.UserListTabFragment1;
import com.online.languages.study.lang.fragments.UserListTabFragment2;
import com.online.languages.study.lang.fragments.UserListTrainingFragment;


public class UserListViewPagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;
    SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();

    public UserListViewPagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return new UserListTabFragment1();
            case 1:
                return new UserListTrainingFragment();
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