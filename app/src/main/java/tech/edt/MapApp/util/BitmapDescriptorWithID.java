package tech.edt.MapApp.util;

import com.google.android.gms.maps.model.BitmapDescriptor;

/**
 * Created by class on 2018-01-19.
 */

public class BitmapDescriptorWithID {
    private int id;
    private BitmapDescriptor bm;

    public BitmapDescriptorWithID(BitmapDescriptor bm, int id) {
        this.bm = bm;
        this.id = id;
    }

    public int getID() {
        return id;
    }

    public BitmapDescriptor getDesc() {
        return bm;
    }


}
