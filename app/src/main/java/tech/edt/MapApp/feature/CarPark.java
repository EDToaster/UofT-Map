package tech.edt.MapApp.feature;

import com.google.android.gms.maps.model.BitmapDescriptor;

import tech.edt.MapApp.Util;

/**
 * Created by class on 2017-10-28.
 */

public class CarPark extends Feature {
    /**
     * Search suggestion stuff
     * (Bikes are not searchable
     */
    public static final String CREATOR = "EDT";

    private String desc;
    private String buildingCode;
    private String access;
    private String address;
    private String aka;

    public CarPark(double lat, double lng, String name, String aka, String buildingCode, String address, String access, String desc) {
        super(lat, lng, name, false, false);
        this.desc = desc;
        this.buildingCode = buildingCode;
        this.address = address;
        this.access = access;
        this.aka = aka;
    }

    public BitmapDescriptor getIcon() {
        return Util.getBikeBMP();
    }

    //How many spots
    public String getSnippet() {
        return access;
    }

    public String getDesc() {
        return desc;
    }

    public String getBuildingCode() {
        return buildingCode;
    }
}
