package tech.edt.MapApp.feature;

import android.os.Parcel;

import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by murad on 11/1/17.
 */

public class GreenSpace extends Feature implements SearchSuggestion {
    private String address;
    private String description;
    private final String CREATOR = "";


    GreenSpace(LatLng ll, String name, boolean isSearchable, boolean isClickable,
               String address, String description) {
        super(ll, name, isSearchable, isClickable);
        this.address = address;
        this.description = description;

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public String toShortString() {
        return getName();
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
    }




}
