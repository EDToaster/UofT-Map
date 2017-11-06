package tech.edt.MapApp.feature;

/**
 * Created by murad on 11/3/17.
 */

public abstract class Safety extends Feature {
    String CREATOR = "Murad";

    Safety(double lat, double lng, String name, boolean isSearchable) { //last param intentional
        super(lat, lng, name, isSearchable, true);
    }


}
