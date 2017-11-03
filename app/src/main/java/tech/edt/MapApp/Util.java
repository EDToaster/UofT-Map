package tech.edt.MapApp;

import android.content.res.AssetManager;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by class on 2017-10-24.
 * Utility class for map app
 */

public class Util {
    private static BitmapDescriptor BUILDINGBMP;
    private static BitmapDescriptor FOODBMP;
    private static BitmapDescriptor BIKEBMP;
    private static BitmapDescriptor CARBMP;
    private static BitmapDescriptor STUDENTBMP;

    static {
        BUILDINGBMP = BitmapDescriptorFactory.fromResource(R.drawable.building_marker);
        FOODBMP = BitmapDescriptorFactory.fromResource(R.drawable.food_marker);
        BIKEBMP = BitmapDescriptorFactory.fromResource(R.drawable.bike_marker);
        CARBMP = BitmapDescriptorFactory.fromResource(R.drawable.car_marker);
        STUDENTBMP = BitmapDescriptorFactory.fromResource(R.drawable.student_marker);
    }


    public static BitmapDescriptor getBuildingBMP() {
        return BUILDINGBMP;
    }

    public static BitmapDescriptor getFoodBMP() {
        return FOODBMP;
    }

    public static BitmapDescriptor getBikeBMP() {
        return BIKEBMP;
    }

    public static BitmapDescriptor getCarBMP() {
        return CARBMP;
    }

    public static BitmapDescriptor getStudentBMP() {
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
