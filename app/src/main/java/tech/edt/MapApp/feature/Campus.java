package tech.edt.MapApp.feature;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by class on 2017-10-29.
 */

public class Campus extends Feature {

    public static final String CREATOR = "EDT";

    private ArrayList<Feature> features;

    public Campus(LatLng ll, String name) {
        super(ll, name, false, false);
        features = new ArrayList<>();
    }

    public ArrayList<Feature> getFeatures() {
        return features;
    }

    public void addFeature(Feature f) {
        this.features.add(f);
    }
}
