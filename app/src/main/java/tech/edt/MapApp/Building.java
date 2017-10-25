package tech.edt.MapApp;

/**
 * Created by class on 2017-10-24.
 * Extension of Feature class
 */

public class Building extends Feature {

    private String code, address;


    public Building(double lat, double lng, String desc, String code, String address) {
        super(lat, lng, desc);
        this.code = code;
        this.address = address;
        this.bitmap = Util.getBuildingBMP();
    }

    public String getCode() {
        return code;
    }

    public String getAddress(String code) {
        return address;
    }
}
