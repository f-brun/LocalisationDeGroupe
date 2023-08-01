package org.patarasprod.localisationdegroupe;

import android.util.Log;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Marker;

import java.io.IOException;
import java.time.Instant;
import java.util.Iterator;

public class Position {
    /** Classe représentant la position dans l'espace et le temps d'un utilisateur **/
    public String nom;
    public double latitude;
    public double longitude;
    public Instant dateMesure;   // Date où a été réalisée la mesure
    Marker marqueur;  // Marqueur Open Street Map associé à cette position

    //Config
    static Config cfg = null;;

    // Constantes
    final String FORMAT_AFFICHAGE_POSITION = "%.7f";
    final String SEPARATEUR_LATITUDE_LONGITUDE = ";";
    final String SEPARATEUR_CHAMPS = "||";
    // Valeurs en cas de position invalide
    public static final String NOM_INVALIDE = "<Invalide>";
    final double LATITUDE_INVALIDE = 200.0;
    final int LIMITE_NOM_LONG = 40;    // Limite de taille au-delà de laquelle le nom est invalide

    // Description des champs du fichier json de sauvegarde des positions
    private final String CHAMP_NOM = "nom";
    private final String CHAMP_LATITUDE = "latitude";
    private final String CHAMP_LONGITUDE = "longitude";
    private final String CHAMP_DATE_MESURE = "date_mesure";

    public Position(String nom, double latitude, double longitude) {
        this.nom = nom;
        this.latitude = latitude;
        this.longitude = longitude;
        this.dateMesure = Instant.now();
        /*
        if (cfg != null && cfg.map != null) {
            this.marqueur = new Marker(cfg.map);
            this.marqueur.setPosition(new GeoPoint(latitude, longitude));
        } else this.marqueur = null;
         */
    }

    public Position(String chaine) {
        /** Crée une instance de Position à partir d'une chaîne ayant le même format que
         * Position.toString (champs séparés par SEPARATEUR_CHAMPS) :
         * Nom
         * Localisation (2 nombres écrit avec la notation anglosaxone ('.' comme séparateur décimal)
         *               séparés entre eux SEPARATEUR_LATITUDE_LONGITUDE (dans cet ordre)
         * Date-heure-timezone  au format classique de la classe Instant
         *
         * Si la conversion de la chaîne n'est pas possible pour une raison ou une autre, la
         * position 'invalide' est renvoyée
         */
        //System.out.println("Création d'une position avec la chaîne : " + chaine);
        String chaine_originale = chaine;
        Position positionDecodee = new Position(NOM_INVALIDE, LATITUDE_INVALIDE, 0);
        try {
            int separateur = chaine.indexOf(SEPARATEUR_CHAMPS);
            if (separateur > 0) {
                positionDecodee.nom = chaine.substring(0, separateur);
                chaine = chaine.substring(separateur + SEPARATEUR_CHAMPS.length());
                separateur = chaine.indexOf(SEPARATEUR_CHAMPS);
                if (separateur > 0) {
                    int positionVirgule = chaine.indexOf(SEPARATEUR_LATITUDE_LONGITUDE);
                    if (positionVirgule > 0) {
                        positionDecodee.latitude = Double.valueOf(chaine.substring(0, positionVirgule));
                        chaine = chaine.substring(positionVirgule + SEPARATEUR_LATITUDE_LONGITUDE.length());
                        separateur = chaine.indexOf(SEPARATEUR_CHAMPS);
                        if (separateur > 0) {
                            positionDecodee.longitude = Double.valueOf(chaine.substring(0, separateur));
                            chaine = chaine.substring(separateur + SEPARATEUR_CHAMPS.length());
                            positionDecodee.dateMesure = Instant.parse(chaine);
                            //System.out.println("Création réussie : " + this.toString());
                        /*
                        if (cfg != null && cfg.map != null) {
                            this.marqueur = new Marker(cfg.map);
                            this.marqueur.setPosition(new GeoPoint(this.latitude, this.longitude));
                        } else this.marqueur = null;
                         */
                            this.nom = positionDecodee.nom;
                            this.latitude = positionDecodee.latitude;
                            this.longitude = positionDecodee.longitude;
                            this.dateMesure = positionDecodee.dateMesure;
                            return;   // fin sans encombre
                        }
                    }
                }
            }
        }
        catch (Exception e) {
                Log.v("MaJ_Position", "Erreur dans le décodage de la chaîne : " + chaine_originale);
        }
        // Il y a eu echec d'au moins une des composantes : on crée une position invalide
        this.nom = NOM_INVALIDE;
        this.latitude = LATITUDE_INVALIDE;
        this.longitude = 0;
        this.dateMesure = Instant.now();
        //this.marqueur = null;
    }

    public Position(JSONObject objetJSON) {
        try {
            this.nom = (String) objetJSON.get(CHAMP_NOM);
            this.latitude = objetJSON.getDouble(CHAMP_LATITUDE);
            this.longitude = objetJSON.getDouble(CHAMP_LONGITUDE);
            this.dateMesure = Instant.parse((String) objetJSON.get(CHAMP_DATE_MESURE));
        } catch (JSONException e) {
            Log.v("Position", "ERREUR dans le constructeur à partir d'un objet JSON :"
                    + objetJSON);
        }

    }

    public void majPosition(double latitude, double longitude) {
        // Met à jour la position
        this.latitude = latitude;
        this.longitude = longitude;
        this.dateMesure = Instant.now();
        /*
        if (this.marqueur != null) {
            this.marqueur.setPosition(new GeoPoint(latitude, longitude));
        } else if (cfg != null && cfg.map != null) {
            this.marqueur = new Marker(cfg.map);
            this.marqueur.setPosition(new GeoPoint(latitude, longitude));
        }
        */
    }

    public void majPosition(Position nouvellePosition) {
        /** Met à jour une position à partir d'une nouvelle position : seul le nom est à conserver **/
        this.latitude = nouvellePosition.latitude;
        this.longitude = nouvellePosition.longitude;
        this.dateMesure = nouvellePosition.dateMesure;
        //this.marqueur = nouvellePosition.marqueur;
    }

    public boolean estPositionValide() {
        /** Renvoie true si la position donnée est valide (vérifie la présence des champs et
         *  leurs valeurs (dans les bornes acceptables)
         **/
        if (this.nom == null || this.nom.length() == 0 ||
                this.nom.length() > LIMITE_NOM_LONG) return false;
        if (this.latitude < -90 || this.latitude > 90) return false;
        if (this.longitude < -180 || this.longitude > 180) return false;
        if (this.dateMesure == null || this.dateMesure.isBefore(Instant.EPOCH) ||
                this.dateMesure.isAfter(Instant.now())) {
            Log.v("Position", "Date d'une position invalide : " + this);
            Log.v("Position", "isBefore(Instant.EPOCH) : " + this.dateMesure.isBefore(Instant.EPOCH));
            Log.v("Position", "this.dateMesure.isAfter(Instant.now()) : " + this.dateMesure.isAfter(Instant.now()));
            return false;
        }
        return true;
    }

    public JSONObject toJSONObject() {
        JSONObject objetPosition = new JSONObject();
        try {
            objetPosition.put(CHAMP_NOM, this.nom);
            objetPosition.put(CHAMP_LATITUDE, this.latitude);
            objetPosition.put(CHAMP_LONGITUDE, this.longitude);
            objetPosition.put(CHAMP_DATE_MESURE, this.dateMesure.toString());
        } catch (JSONException e) {
            Log.v("Position", "ERREUR dans la création d'un objet JSON pour la position :"
                    + this);
        }
        return objetPosition;
    }

    public GeoPoint getGeoPoint() {
        return new GeoPoint(this.latitude, this.longitude);
    }

    @NonNull
    @Override
    public String toString() {
        return nom
         + SEPARATEUR_CHAMPS
         + String.format(FORMAT_AFFICHAGE_POSITION, this.latitude).replace(',','.')
         + SEPARATEUR_LATITUDE_LONGITUDE
         + String.format(FORMAT_AFFICHAGE_POSITION, this.longitude).replace(',','.')
         + SEPARATEUR_CHAMPS
         + this.dateMesure.toString() ;
    }
}
