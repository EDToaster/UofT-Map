package tech.edt.MapApp.feature;

import android.graphics.Color;
import android.os.Parcel;

import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;

import tech.edt.MapApp.util.BitmapDescriptorWithID;
import tech.edt.MapApp.util.Util;

/**
 * Created by Murad on 11/1/17.
 * An extension of Feature Class. Represents green spaces on campus.
 * Searchable
 */

public class GreenSpace extends Feature implements SearchSuggestion {
    private String address; //for later use
    private String description;

    GreenSpace(LatLng ll, String name, String address, String description) {
        super(ll, name, true, true, Color.parseColor("#339933"));
        this.address = address;
        this.description = description;

    }

    public BitmapDescriptorWithID getBitmapDescriptor() {
        return Util.getGreenBMP();
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
