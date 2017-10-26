package tech.edt.MapApp;

import android.os.Parcel;

import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.google.android.gms.maps.model.BitmapDescriptor;

/**
 * Created by class on 2017-10-24.
 * Extension of Feature class, every Building is a Search suggestion
 */

public class Building extends Feature implements SearchSuggestion {

    private String code, address, short_name;


    public Building(double lat, double lng, String name, String code, String address, String short_name) {
        super(lat, lng, name);
        this.code = code;
        this.address = address;
        this.short_name = short_name;
    }

    public BitmapDescriptor getIcon() {
        return Util.getBuildingBMP();
    }

    public String getCode() {
        return code;
    }

    public String getAddress(String code) {
        return address;
    }

    /**
     * Search suggestion stuff
     */
    public static final String CREATOR = "EDT";


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


}
