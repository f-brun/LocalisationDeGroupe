package org.patarasprod.localisationdegroupe.views;

import android.util.Log;

import androidx.viewpager2.widget.ViewPager2;

import org.patarasprod.localisationdegroupe.Config;
import org.patarasprod.localisationdegroupe.MainActivity;
import org.patarasprod.localisationdegroupe.MyAdapter;

public class OnPageChangeCallback_ViewPager2 extends ViewPager2.OnPageChangeCallback {

    Config cfg;

    public OnPageChangeCallback_ViewPager2(Config config) {
        cfg = config;
    }

    public void onPageSelected(int position) {
        if (Config.DEBUG_LEVEL > 3) Log.v("OnPageChangeCallback", "*****-*** Onglet n°" + position + " sélectionné !");
    }
}
