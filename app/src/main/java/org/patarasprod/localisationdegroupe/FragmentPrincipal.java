package org.patarasprod.localisationdegroupe;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import org.patarasprod.localisationdegroupe.databinding.FragmentPrincipalBinding;

public class FragmentPrincipal extends NavHostFragment {

    // Tableau donnant la visibilité du bouton permettant de centrer sur ma position en fonction du numéro de l'onglet
    private final boolean[] visibiliteFab = {true, true, false, false};
    private FragmentPrincipalBinding binding;
    private Context contexte;
    Config cfg;

    TabLayout tabLayout;
    ViewPager viewPager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cfg = ((MainActivity) requireActivity()).recupere_configuration();
//        cfg.navController = this.getNavController();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        contexte = context;
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        binding = FragmentPrincipalBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        cfg = ((MainActivity) requireActivity()).recupere_configuration();

        // Bouton flottant permettant de centrer sur ma position;
        cfg.centrerSurMaPosition = binding.fabCentrerSurMaPosition;

        if (Config.DEBUG_LEVEL > 3) Log.v("Fragment principal","CREATION View FRAGMENT PRINCIPAL");

        //setContentView(R.layout.activity_main);
        tabLayout = binding.tabLayout2;    //findViewById(R.id.tabLayout);

        /* POUR DESACTIVER LE SWIPE IL FAUT UTILISER UN ViewPager2 (voir https://developer.android.com/reference/androidx/viewpager2/widget/ViewPager2#summary
          et nottamment la méthode setUserInputEnabled pour désactiver le swipe
         */

        viewPager = binding.viewPager2; //findViewById(R.id.viewPager2);

        if (Config.DEBUG_LEVEL > 3) Log.v("Fragment principal",
                "OnCreateView fragment principal. FragmentManager = " +
                        ((MainActivity) requireActivity()).getSupportFragmentManager());
        final MyAdapter adapter = new MyAdapter(this.contexte, ((MainActivity) requireActivity()).getSupportFragmentManager(),
                tabLayout.getTabCount());


        cfg.viewPager = viewPager;


        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                if (visibiliteFab[tab.getPosition()]) {
                    cfg.centrerSurMaPosition.setVisibility(View.VISIBLE);
                } else {
                    cfg.centrerSurMaPosition.setVisibility(View.GONE);
                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        binding.fabCentrerSurMaPosition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Centrage sur ma position", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                // Centrage de la carte sur ma position
                if (cfg != null && cfg.mapController != null)
                    cfg.mapController.setCenter(cfg.maPosition.getGeoPoint());
                // On bascule sur l'onglet 1 (la carte)
                if (cfg != null && cfg.viewPager != null) cfg.viewPager.setCurrentItem(1);
            }
        });
        if (Config.DEBUG_LEVEL > 3) Log.v("Fragment principal", "---Fin du OnCreateView du fragment principal");

        disableTouchTheft(root);
        return root;
    }

    public static void disableTouchTheft(View view) {
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                view.getParent().requestDisallowInterceptTouchEvent(true);
                switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_UP:
                        view.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }
                return false;
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}