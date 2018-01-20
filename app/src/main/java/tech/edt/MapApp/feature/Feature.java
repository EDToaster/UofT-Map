package tech.edt.MapApp.feature;

import android.os.Parcel;

import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import tech.edt.MapApp.util.BitmapDescriptorWithID;

/**
 * Created by EDT on 2017-10-24.
 * A abstract "Feature" class that needs implementation
 */

public abstract class Feature implements SearchSuggestion {
    private LatLng latlng;
    private String name;
    private Marker marker;
    private boolean isSearchable;
    private boolean isClickable;

    Feature(double lat, double lng, String name, boolean isSearchable, boolean isClickable) {
        this(new LatLng(lat, lng), name, isSearchable, isClickable);
    }

    Feature(LatLng ll, String name, boolean isSearchable, boolean isClickable) {

        this.latlng = ll;
        this.name = name;
        this.isSearchable = isSearchable;
        this.isClickable = isClickable;
    }

    public boolean isSearchable() {
        return isSearchable;
    }

    public LatLng getLatLng() {
        return this.latlng;
    }

    public String getName() {
        return this.name;
    }

    public BitmapDescriptorWithID getBitmapDescriptor() {
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

    //Searchable method (the string that replaces the searchView)
    public String toShortString() {
        return toString();
    }

    public String getSnippet() {
        return "";
    }

    public Marker getMarker(GoogleMap mMap) {
        if (marker == null) {
            marker = mMap.addMarker(getMarkerOptions());
            marker.setVisible(false);
            marker.setTag(this);
        }
        return marker;
    }

    private MarkerOptions getMarkerOptions() {
        return new MarkerOptions().position(latlng).icon(getBitmapDescriptor().getDesc())
                .title(this.toString()).snippet(getSnippet() +
                        (isClickable ? "\nClick for more info" : ""));
    }


}
