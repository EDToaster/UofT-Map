package tech.edt.MapApp.util;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;


public class BitmapDescriptorWithIDFactory {
    public static BitmapDescriptorWithID fromResource(int resource) {
        return new BitmapDescriptorWithID(BitmapDescriptorFactory.fromResource(resource), resource);
    }


}
