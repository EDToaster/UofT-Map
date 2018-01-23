package tech.edt.MapApp.feature;

/**
 * Created by Murad on 11/3/17.
 * Emergency phones on campus. A special safety feature
 */

public class EmergencyPhone extends Safety {

    EmergencyPhone(double lat, double lng, String name) {
        super(lat, lng, name, false);
    }
}
