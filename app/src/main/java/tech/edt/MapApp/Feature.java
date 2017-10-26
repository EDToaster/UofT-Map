package tech.edt.MapApp;

import android.os.Parcel;

import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by EDT on 2017-10-24.
 * A abstract "Feature" class that needs implementation
 */

abstract class Feature implements SearchSuggestion {
    private LatLng latlng;
    private String name;
    private MarkerOptions markerOptions;

    Feature(double lat, double lng, String name) {
        this.latlng = new LatLng(lat, lng);
        this.name = name;
    }

    public LatLng getLatLng() {
        return this.latlng;
    }

    public String getName() {
        return this.name;
    }

    public BitmapDescriptor getIcon() {
        return null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public String getBody() {
        return toString();
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

    }

    public String getStrippedMatchString() {
        return toString().replaceAll("[^a-zA-Z ]", "").toLowerCase();
    }

    public String toString() {
        return name;
    }

    public String toShortString() {
        return toString();
    }

    public MarkerOptions getMarkerOptions() {
        if (markerOptions == null)
            markerOptions = new MarkerOptions().position(latlng).icon(getIcon()).title(this.toString());
        return markerOptions;
    }

}
