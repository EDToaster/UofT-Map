package tech.edt.MapApp.feature;

import android.annotation.SuppressLint;
import android.os.Parcel;

import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by murad on 11/1/17.
 */

public class StudentServices extends Feature implements SearchSuggestion{
    private String address;
    private String phone;
    private String url;
    private String description;
    private  final String CREATOR = "";

    StudentServices(LatLng ll, String name, boolean isSearchable, boolean isClickable,
                    String address, String phone, String url,String description) {
        super(ll, name, isSearchable, isClickable);
        this.address = address;
        this.url = url;
        this.description = description;
        this.phone = phone;
    }

    StudentServices(LatLng ll, String name, boolean isSearchable, boolean isClickable,
                    String address, String url,String description) {
        super(ll, name, isSearchable, isClickable);
        this.address = address;
        this.url = url;
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

    public String getUrl() {
        return url;
    }




}
