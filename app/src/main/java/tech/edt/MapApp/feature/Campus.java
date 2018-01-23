package tech.edt.MapApp.feature;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by class on 2017-10-29.
 */

public class Campus extends Feature {

    private ArrayList<Feature> features;

    private String tag;

    public Campus(LatLng ll, String name, String tag) {
        super(ll, name, false, false);
        features = new ArrayList<>();
        this.tag = tag;
    }

    public String getDrawerTag() {
        return this.tag;
    }

    public ArrayList<Feature> getFeatures() {
        return features;
    }

    public void addFeature(Feature f) {
        this.features.add(f);
    }
}
