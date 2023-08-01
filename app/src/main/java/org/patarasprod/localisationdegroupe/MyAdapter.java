package org.patarasprod.localisationdegroupe;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;


class MyAdapter extends FragmentPagerAdapter {
    Context context;
    int totalTabs;
    public MyAdapter(Context c, FragmentManager fm, int totalTabs) {
        super(fm);
        context = c;
        this.totalTabs = totalTabs;
    }
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                Fragment_1 fragment = new Fragment_1();
                return fragment;
            case 1:
                Fragment_2 fragment2 = new Fragment_2();
                return fragment2;
            case 2:
                Fragment_3 fragment3 = new Fragment_3();
                return fragment3;
            case 3:
                Fragment_parametres fragmentParametres = new Fragment_parametres();
                return fragmentParametres;
            default:
                return null;
        }
    }
    @Override
    public int getCount() {
        return totalTabs;
    }
}