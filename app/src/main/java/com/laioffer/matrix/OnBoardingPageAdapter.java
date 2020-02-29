package com.laioffer.matrix;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class OnBoardingPageAdapter extends FragmentPagerAdapter {
    private static int NUM_ITEMS = 2;

    public OnBoardingPageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return  new LoginFragment();
            case 1:
                return RegisterFragment.newInstance();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return NUM_ITEMS;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Login";
            case 1:
                return "Register";
        }
        return null;
    }

}
