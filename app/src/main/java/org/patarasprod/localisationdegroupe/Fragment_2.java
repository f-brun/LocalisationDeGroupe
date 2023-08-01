package org.patarasprod.localisationdegroupe;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;
import org.patarasprod.localisationdegroupe.databinding.Fragment2Binding;

/**
 * A placeholder fragment containing a simple view.
 */
public class Fragment_2 extends Fragment {

    private Fragment2Binding binding;
    Config cfg;
    RotationGestureOverlay mRotationGestureOverlay;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        binding = Fragment2Binding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Mise en place de la carte
        cfg = ((MainActivity) requireActivity()).recupere_configuration();
        if (Config.DEBUG_LEVEL > 3) Log.v("Fragment 2","onCreateView Fragment2 cfg = " + cfg);
        cfg.map = binding.map;
        cfg.map.setTileSource(TileSourceFactory.MAPNIK);
        //Configuration du user-agent pour indiquer à Mapnik qui utilise leurs services
        Configuration.getInstance().setUserAgentValue("Localisation-de-groupe");

        //Configuration de la carte
        cfg.map.setBuiltInZoomControls(true);
        cfg.map.setMultiTouchControls(true);   // Possibilité de zoomer
        mRotationGestureOverlay = new RotationGestureOverlay(cfg.map);
        mRotationGestureOverlay.setEnabled(true);
        cfg.map.setMultiTouchControls(true);
        cfg.map.getOverlays().add(this.mRotationGestureOverlay);

        // Centrage et réglage du zoom
        cfg.mapController = cfg.map.getController();
        cfg.mapController.setZoom(cfg.niveauZoomCarte);
        if (cfg.localisation != null) {
            Location maLocalisation = cfg.localisation.getLocalisation();
            if (maLocalisation != null) cfg.maPositionSurLaCarte =
                    new GeoPoint(maLocalisation.getLatitude(), maLocalisation.getLongitude());
        } else {
            cfg.maPositionSurLaCarte = new GeoPoint(cfg.centreCarte.getLatitude(),
                    cfg.centreCarte.getLongitude());
        }
        cfg.mapController.setCenter(cfg.centreCarte);

        //Compas en surimpression
        cfg.mCompassOverlay = new CompassOverlay((Context)requireActivity(),
                new InternalCompassOrientationProvider((Context)requireActivity()), cfg.map);
        cfg.mCompassOverlay.enableCompass();
        cfg.map.getOverlays().add(cfg.mCompassOverlay);

        // Mylocation : Surimpression de ma position
        cfg.maPosition_LocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider((Context)requireActivity()), cfg.map);
        cfg.maPosition_LocationOverlay.enableMyLocation();
        cfg.map.getOverlays().add(cfg.maPosition_LocationOverlay);

        // Scalemap : échelle en surimpression
        DisplayMetrics dm = requireActivity().getResources().getDisplayMetrics();
        ScaleBarOverlay mScaleBarOverlay = new ScaleBarOverlay(cfg.map);
        mScaleBarOverlay.setCentred(true);
        //play around with these values to get the location on screen in the right place for your application
        mScaleBarOverlay.setScaleBarOffset(dm.widthPixels / 2, 10);
        cfg.map.getOverlays().add(mScaleBarOverlay);

        cfg.gestionPositionsUtilisateurs.majPositionsSurLaCarte();
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Config.DEBUG_LEVEL > 3) Log.v("Fragment 2","onResume Fragment2 cfg = " + cfg);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Config.DEBUG_LEVEL > 3) Log.v("Fragment 2","onPause Fragment2 cfg = " + cfg);
        cfg.centreCarte = (GeoPoint) cfg.map.getMapCenter();
        cfg.niveauZoomCarte = cfg.map.getZoomLevelDouble();
        cfg.orientationCarte = cfg.map.getMapOrientation();
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Restaure les caractéristiques de visualisation de la carte (centre, zoom, orientation
        cfg.map.post(
                new Runnable() {
                    @Override
                    public void run() {
                        cfg.mapController.setCenter(cfg.centreCarte);
                        cfg.mapController.setZoom(cfg.niveauZoomCarte);
                        cfg.map.setMapOrientation(cfg.orientationCarte);
                    }
                }
        );
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}