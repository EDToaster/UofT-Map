package tech.edt.MapApp;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

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

    public static void init() {
        BUILDINGBMP = BitmapDescriptorFactory.fromResource(R.drawable.building);
    }


    public static BitmapDescriptor getBuildingBMP() {
        return BUILDINGBMP;
    }

    public static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }

//    public static String getStringFromFile(String filePath) throws Exception {
//        File fl = new File(filePath);
//        FileInputStream fin = new FileInputStream(fl);
//        String ret = convertStreamToString(fin);
//        //Make sure you close all streams.
//        fin.close();
//        return ret;
//    }
}
