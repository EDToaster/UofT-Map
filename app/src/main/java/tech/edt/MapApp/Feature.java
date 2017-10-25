package tech.edt.MapApp;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by EDT on 2017-10-24.
 * A abstract "Feature" class that needs implementation
 */

public abstract class Feature {
    protected LatLng latlng;
    protected String desc;
    protected BitmapDescriptor bitmap;

    public Feature(double lat, double lng, String desc) {
        this.latlng = new LatLng(lat, lng);
    }

    public LatLng getLatLng() {
        return this.latlng;
    }

    public String getDesc() {
        return this.desc;
    }

    public BitmapDescriptor getIcon() {
        return this.bitmap;
    }


}
