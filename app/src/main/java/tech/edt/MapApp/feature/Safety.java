package tech.edt.MapApp.feature;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import tech.edt.MapApp.util.BitmapDescriptorWithID;
import tech.edt.MapApp.util.Util;

/**
 * Created by murad on 11/3/17.
 */

public abstract class Safety extends Feature {

    Safety(double lat, double lng, String name, boolean isSearchable) { //last param intentional
        super(lat, lng, name, isSearchable, true, 0);
    }

    public BitmapDescriptorWithID getBitmapDescriptor() {
        return Util.getSafetyBMP();
    }
}
