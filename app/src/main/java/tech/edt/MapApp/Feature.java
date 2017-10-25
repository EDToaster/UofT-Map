package tech.edt.MapApp;

import com.google.android.gms.maps.model.BitmapDescriptor;

/**
 * Created by EDT on 2017-10-24.
 * A abstract "Feature" class that needs implementation
 */

public abstract class Feature {
    protected double lat, lng;
    protected String desc;
    protected BitmapDescriptor bitmap;

    public Feature(double lat, double lng, String desc) {
        this.lat = lat;
        this.lng = lng;
    }

    public double getLat() {
        return this.lat;
    }

    public double getLng() {
        return this.lng;
    }

    public String getDesc() {
        return this.desc;
    }

    public BitmapDescriptor getIcon() {
        return null;
    }


}
