package tech.edt.MapApp.feature;

import android.os.Parcel;

import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;

import tech.edt.MapApp.Util;

/**
 * Created by Murad on 11/1/17.
 */

public class GreenSpace extends Feature implements SearchSuggestion {
    private String address;
    private String description;
    private final String CREATOR = "Murad";


    GreenSpace(LatLng ll, String name, String address, String description) {
        super(ll, name, false, true);
        this.address = address;
        this.description = description;

    }

    public BitmapDescriptor getIcon() {
        return Util.getBuildingBMP();
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


    public String getDialogText() {
        return description;
    }
}
