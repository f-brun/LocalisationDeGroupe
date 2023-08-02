package org.patarasprod.localisationdegroupe;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import java.util.Locale;


public final class LocalisationGPS implements LocationListener {

    private final Context mContext;

    Config cfg = null;                      // Référence vers la config de l'appli

    public boolean GPSDisponible = false;        // flag for GPS status
    boolean NetworkDisponible = false;           // flag for network status
    boolean localisationDisponible = false;             // flag for GPS status

    Location localisation;      // localisation
    double latitude = 1000.0; // latitude
    double longitude = 1000.0; // longitude


    // The minimum distance to change Updates in meters
    private static final float MIN_DISTANCE_CHANGE_FOR_UPDATES = 1; // 1 mètre mini

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000; // 1 seconde
    private static final String FORMAT_AFFICHAGE_POSITION = "%.7f";
    private static final String FORMAT_AFFICHAGE_ALTITUDE = "%.0f";

    Locale localeEn = new Locale("en", "UK");  // Pour avoir le point en séparateur décimal
//    Locale localeFr = new Locale("fr", "FR");  // Pour avoir la virgule en séparateur décimal

    // Declaring a Location Manager
    public LocationManager locationManager;

    public LocalisationGPS(Context context, Config config) {
        this.mContext = context;
        cfg = config;
        if (Config.DEBUG_LEVEL > 3) Log.v("LocalisationGPS", "Initialisation du système");
        initialisations();
        getLocalisation();
    }

    @SuppressLint("MissingPermission")
    private void initialisations() {
        try {
            // Vérification des autorisations
            if (demandeAutorisationsLocalisation()){
                locationManager = (LocationManager) mContext
                        .getSystemService(Context.LOCATION_SERVICE);

                // Est-ce que la localisation par le réseau (Network) est dispo ?
                NetworkDisponible = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                if (Config.DEBUG_LEVEL > 1) Log.v("LocalisationGPS", "Network disponible :" + NetworkDisponible);

                // Le GPS est-il disponible ?
                GPSDisponible = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                if (Config.DEBUG_LEVEL > 1) Log.v("LocalisationGPS", "GPS disponible :" + GPSDisponible);

                getLocalisation();
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean demandeAutorisationsLocalisation() {
        //System.out.println("Demande des autorisations de localisation");
        if (ActivityCompat.checkSelfPermission(this.mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this.mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            // TODO
            ActivityCompat.requestPermissions((Activity)mContext, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            if (ActivityCompat.checkSelfPermission(this.mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this.mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * Récupère la localisation actuelle
     *
     * @return localisation actuelle (objet Location) ou null si échec
     */
    @SuppressLint("MissingPermission")
    public Location getLocalisation() {
        if (demandeAutorisationsLocalisation()) {
            locationManager = (LocationManager) mContext
                    .getSystemService(Context.LOCATION_SERVICE);
            NetworkDisponible = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            GPSDisponible = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (!GPSDisponible && !NetworkDisponible) {
                // no network provider is enabled
                if (Config.DEBUG_LEVEL > 0) Log.v("LocalisationGPS", "Aucun accès à une méthode de localisation");
                return null;
            } else if (GPSDisponible) {
                localisation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            } else {
                localisation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
            if (localisation != null) {
                latitude = localisation.getLatitude();
                longitude = localisation.getLongitude();
            }
            // Demande l'actualisation si disponible
            if (NetworkDisponible) locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    cfg.intervalleMesureSecondes * 1000,
                    MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
            if (GPSDisponible) locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    cfg.intervalleMesureSecondes * 1000,
                    MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
            if (cfg.maPosition != null) {
                cfg.maPosition.majPosition(latitude, longitude);
            } else cfg.maPosition = new Position(cfg.nomUtilisateur, latitude, longitude);
            actualise_position();
            return localisation;
        }
        return null;
    }

    @SuppressLint("DefaultLocale")
    public void actualise_position() {
        if (cfg.textViewLocalisation != null) {
            cfg.textViewLocalisation.setText(coordsSurDeuxLignes());
        }
        if (cfg.textViewAltitude != null) {
            String texte = "";
            if (localisation != null && localisation.hasAltitude()) {
                texte = String.format(FORMAT_AFFICHAGE_ALTITUDE, localisation.getAltitude()) + " m";
            } else {
                texte = "N/A";
            }
            cfg.textViewAltitude.setText(texte);
        }
        if (cfg.textViewLatitude != null) {
            cfg.textViewLatitude.setText(conversionEnDegresMinutesSecondes(latitude));
        }
        if (cfg.textViewLongitude != null) {
            cfg.textViewLongitude.setText(conversionEnDegresMinutesSecondes(longitude));
        }
    }

    /**
     * Arrêt des mises à jour de la position (quand on ferme l'application)
     * */
    public void arretMisesAJour() {
        if (locationManager != null) {
            locationManager.removeUpdates(this);
        }
    }

    /**
     * Function to get latitude
     * */
    public double getLatitude() {
        if (localisation != null) {
            latitude = localisation.getLatitude();
        }
        // return latitude
        return latitude;
    }

    /**
     * Function to get longitude
     * */
    public double getLongitude() {
        if (localisation != null) {
            longitude = localisation.getLongitude();
        }
        // return longitude
        return longitude;
    }

    /**
     * Function to check GPS/wifi enabled
     *
     * @return boolean
     * */
    public boolean isLocalisationDisponible() {
        return this.localisationDisponible;
    }

    /**
     * Function to show settings alert dialog On pressing Settings button will
     * lauch Settings Options
     * */
    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings");

        // Setting Dialog Message
        alertDialog
                .setMessage("GPS is not enabled. Do you want to go to settings menu?");

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        mContext.startActivity(intent);
                    }
                });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        // Showing Alert Message
        alertDialog.show();
    }

    @Override
    public void onLocationChanged(Location location) {
        if (Config.DEBUG_LEVEL > 4) Log.v("LocalisationGPS", "Position modifiée");
        getLocalisation();
        actualise_position();
    }

    @Override
    public void onProviderDisabled(String provider) {
        // TODO
    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        if (Config.DEBUG_LEVEL > 3) Log.v("LocalisationGPS","Status modifié");
        // TODO
    }
    public String coordsSurUneLigne() {
        return String.format(localeEn, FORMAT_AFFICHAGE_POSITION,latitude) +
                "N, " + String.format(localeEn, FORMAT_AFFICHAGE_POSITION, longitude) + "E";
    }
    public String coordsSurDeuxLignes() {
        return String.format(localeEn, FORMAT_AFFICHAGE_POSITION,latitude) + "N\n"
               + String.format(localeEn, FORMAT_AFFICHAGE_POSITION, longitude) + "E";
    }
    public String conversionEnDegresMinutesSecondes(double angle) {
        int degres = (int) angle;
        int minutes = (int) ((angle - ((double)degres) ) * 60);
        double secondes = (angle - ((double)degres) - ((double)minutes/60)) * 3600;
        return "" + degres + "°" + minutes + "'" + String.format(localeEn, "%.3f", secondes) + '"';
    }
}