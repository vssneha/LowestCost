package com.jfpdazey.pathoflowestcost;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class PathOptionsPagerAdapter extends FragmentPagerAdapter {

    public PathOptionsPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new CalculatePathOfLowestCostFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Calculate Path of Lowest Cost";
            default:
                return null;
        }
    }
}
