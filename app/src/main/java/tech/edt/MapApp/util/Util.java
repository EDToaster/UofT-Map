package tech.edt.MapApp.util;

import android.content.res.AssetManager;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import tech.edt.MapApp.R;

/**
 * Created by class on 2017-10-24.
 * Utility class for map app
 */

public class Util {
    private static BitmapDescriptorWithID BUILDINGBMP;
    private static BitmapDescriptorWithID FOODBMP;
    private static BitmapDescriptorWithID BIKEBMP;
    private static BitmapDescriptorWithID CARBMP;
    private static BitmapDescriptorWithID STUDENTBMP;

    static {
        BUILDINGBMP = BitmapDescriptorWithIDFactory.fromResource(R.drawable.building_marker);
        FOODBMP = BitmapDescriptorWithIDFactory.fromResource(R.drawable.food_marker);
        BIKEBMP = BitmapDescriptorWithIDFactory.fromResource(R.drawable.bike_marker);
        CARBMP = BitmapDescriptorWithIDFactory.fromResource(R.drawable.car_marker);
        STUDENTBMP = BitmapDescriptorWithIDFactory.fromResource(R.drawable.student_marker);
    }


    public static BitmapDescriptorWithID getBuildingBMP() {
        return BUILDINGBMP;
    }

    public static BitmapDescriptorWithID getFoodBMP() {
        return FOODBMP;
    }

    public static BitmapDescriptorWithID getBikeBMP() {
        return BIKEBMP;
    }

    public static BitmapDescriptorWithID getCarBMP() {
        return CARBMP;
    }

    public static BitmapDescriptorWithID getStudentBMP() {
        return STUDENTBMP;
    }

    public static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }

    public static JSONObject getBaseObj(AssetManager assetManager, String file) throws Exception {
        InputStream input = assetManager.open(file);
        String food = Util.convertStreamToString(input);
        return new JSONObject(food);
    }

    public static String[] toStringArray(JSONArray array) {
        if (array == null)
            return null;

        String[] arr = new String[array.length()];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = array.optString(i);
        }
        return arr;
    }

}
