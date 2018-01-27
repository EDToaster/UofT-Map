package tech.edt.MapApp.feature;

import android.graphics.Color;
import android.os.Parcel;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import tech.edt.MapApp.util.BitmapDescriptorWithID;
import tech.edt.MapApp.util.Util;

/**
 * Created by class on 2017-10-24.
 * Extension of Feature class, every Building is a Search suggestion
 */

public class Building extends Feature implements SearchSuggestion {

    private String code, address, short_name, short_address;
    private ArrayList<LatLng> polygon;


    public Building(double lat, double lng, String name, String code, String short_address,
                    String address, String short_name, ArrayList<LatLng> polygon) {
        super(lat, lng, name, true, true, Color.parseColor("#0099cc"));
        this.code = code;
        this.address = address;
        this.short_name = short_name;
        this.short_address = short_address;
        this.polygon = polygon;
    }

    public ArrayList<LatLng> getPolygon() {
        return polygon;
    }

    public BitmapDescriptorWithID getBitmapDescriptor() {
        return Util.getBuildingBMP();
    }

    private String getCode() {
        return code;
    }

    public String getAddress() {
        return address;
    }

    /**
     * Search suggestion stuff
     */
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return getCode() + " - " + getName();
    }

    @Override
    public String toShortString() {
        return short_name;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
    }

    @Override
    public String getSnippet() {
        return short_address;
    }


}
