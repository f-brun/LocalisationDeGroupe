package org.patarasprod.localisationdegroupe;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.snackbar.Snackbar;

import org.patarasprod.localisationdegroupe.databinding.Fragment1Binding;

/**
 * A placeholder fragment containing a simple view.
 */
public class Fragment_1 extends NavHostFragment {

    private Fragment1Binding binding;
    Config cfg;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {


        binding = Fragment1Binding.inflate(inflater, container, false);
        View root = binding.getRoot();
        cfg = ((MainActivity) requireActivity()).recupere_configuration();
        cfg.textViewLocalisation = binding.labelMaPosition;
        cfg.textViewAltitude = binding.labelAltitude;
        cfg.textViewLatitude = binding.labelLatitude;
        cfg.textViewLongitude = binding.labelLongitude;

        if (cfg != null && Config.DEBUG_LEVEL > 3) Log.v("Fragment 1", "Création du fragment 1");
        cfg.localisation.getLocalisation();

        return root;
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}