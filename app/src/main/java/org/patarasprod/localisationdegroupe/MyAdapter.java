package org.patarasprod.localisationdegroupe;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.Lifecycle;

import java.util.Objects;


public class MyAdapter extends androidx.viewpager2.adapter.FragmentStateAdapter {

    int totalTabs;

    public MyAdapter(FragmentActivity fa, int totalTabs) {
        super(fa);
        this.totalTabs = totalTabs;
    }
    @Override
    public Fragment createFragment(int position) {
        if (Config.DEBUG_LEVEL > 3) Log.v("MyAdapter","Méthode Fragment appellée avec position = " + position);
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
                Log.v("MyAdapter","@@@@ ERREUR : Méthode Fragment appellée avec position = " + position);
                return null;
        }
    }

    @Override
    public int getItemCount() {
        return totalTabs;
    }
}