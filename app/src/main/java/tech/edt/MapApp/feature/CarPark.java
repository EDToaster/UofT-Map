package tech.edt.MapApp.feature;

import com.google.android.gms.maps.model.BitmapDescriptor;

import tech.edt.MapApp.util.BitmapDescriptorWithID;
import tech.edt.MapApp.util.Util;

/**
 * Created by class on 2017-10-28.
 * An Extension of the Feature class. Used to represent parking lots. not searchable.
 */

public class CarPark extends Feature {
    /**
     * Search suggestion stuff
     * (Bikes are not searchable
     */
    public static final String CREATOR = "EDT";

    private String desc;
    private String buildingCode;
    private String address;
    private String phone;

    //TODO: fix parking json
    public CarPark(double lat, double lng, String name,
                   String buildingCode, String address, String desc, String phone) {
        super(lat, lng, name, false, false);
        this.desc = desc;
        this.buildingCode = buildingCode;
        this.address = address;
        this.phone = phone;
    }

    public BitmapDescriptorWithID getBitmapDescriptor() {
        return Util.getCarBMP();
    }

    //How many spots
    public String getSnippet() {
        return phone;
    }

    public String getDesc() {
        return desc;
    }

    public String getBuildingCode() {
        return buildingCode;
    }
}
