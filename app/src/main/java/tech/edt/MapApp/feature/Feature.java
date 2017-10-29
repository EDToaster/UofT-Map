package tech.edt.MapApp.feature;

import android.os.Parcel;

import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by EDT on 2017-10-24.
 * A abstract "Feature" class that needs implementation
 */

public abstract class Feature implements SearchSuggestion {
    private LatLng latlng;
    private String name;
    private Marker marker;
    private boolean isSearchable;

    Feature(double lat, double lng, String name, boolean isSearchable) {
        this.latlng = new LatLng(lat, lng);
        this.name = name;
        this.isSearchable = isSearchable;
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
        return new MarkerOptions().position(latlng).icon(getIcon()).title(this.toString()).snippet(getSnippet() + "\nClick for more info");
    }


}
