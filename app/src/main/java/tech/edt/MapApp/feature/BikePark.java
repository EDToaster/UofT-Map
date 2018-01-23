package tech.edt.MapApp.feature;

import tech.edt.MapApp.util.BitmapDescriptorWithID;
import tech.edt.MapApp.util.Util;

/**
 * Created by class on 2017-10-28.
 */

public class BikePark extends Feature {
    /**
     * Search suggestion stuff
     * (Bikes are not searchable
     */

    private String desc;
    private String buildingCode;

    public BikePark(double lat, double lng, String name, String buildingCode, String desc) {
        super(lat, lng, name, false, false);
        this.desc = desc;
        this.buildingCode = buildingCode;
    }

    public BitmapDescriptorWithID getBitmapDescriptor() {
        return Util.getBikeBMP();
    }

    //How many bikes and racks
    public String getSnippet() {
        return getDesc();
    }

    public String getDesc() {
        return desc;
    }

    public String getBuildingCode() {
        return buildingCode;
    }
}
