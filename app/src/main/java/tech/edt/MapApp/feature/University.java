package tech.edt.MapApp.feature;

import android.content.res.AssetManager;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import tech.edt.MapApp.Hours;
import tech.edt.MapApp.Util;

/**
 * A University object with 3 campus objects
 */
public class University {
    private HashMap<String, Campus> campuses;
    private Campus current_selected;

    private static final LatLng UTSGLL = new LatLng(43.6644, -79.3923);
    private static final LatLng UTMLL = new LatLng(43.5479, -79.6609);
    private static final LatLng UTSCLL = new LatLng(43.7841, -79.1868);

    private Feature next;

    public University(String... campusNames) {
        campuses = new HashMap<String, Campus>();

        campuses.put("UTSG", new Campus(UTSGLL, campusNames[0]));
        campuses.put("UTM", new Campus(UTMLL, campusNames[1]));
        campuses.put("UTSC", new Campus(UTSCLL, campusNames[2]));


        Comparator cmp = new Comparator<Feature>() {
            public int compare(Feature f1, Feature f2) {
                return f1.toString().compareTo(f2.toString());
            }
        };
        for (Campus i : campuses.values())
            Collections.sort(i.getFeatures(), cmp);
    }

    public HashMap<String, Campus> getCampuses() {
        return campuses;
    }

    public boolean setCurrentSelected(String campus) {
        Campus c = this.campuses.get(campus);
        if (c == null)
            return false;
        this.current_selected = c;
        return true;
    }

    public Campus getCurrentSelected() {
        return current_selected;
    }

    public ArrayList<Feature> getAllFeatures() {
        ArrayList<Feature> to_return = new ArrayList<>();
        for (Campus i : campuses.values())
            to_return.addAll(i.getFeatures());
        return to_return;
    }

    public University setUpFeatures(AssetManager assetManager) {
        try {
            setUpBuildings(assetManager);
            setUpFood(assetManager);
            setUpBikes(assetManager);
            setUpCars(assetManager);
        } catch (Exception e) {
            Log.e("setUpFeatures", "Exception", e);
            System.exit(1);
        }
        return this;
    }

    private void setUpBuildings(AssetManager assetManager) throws Exception {

        JSONArray arr = Util.getBaseObj(assetManager, "buildings.json")
                .getJSONArray("buildings");

        for (int i = 0; i < arr.length(); i++) {
            try {
                JSONObject ij = arr.getJSONObject(i);

                double lat = ij.getDouble("lat");
                double lng = ij.getDouble("lng");
                String name = ij.getString("name");
                String code = ij.getString("code");
                String short_name = ij.getString("short_name");
                ArrayList<LatLng> polygon = new ArrayList();
                JSONArray json_polygon = ij.getJSONArray("polygon");
                if (json_polygon != null) {
                    int len = json_polygon.length();
                    for (int j = 0; j < len; j++) {
                        JSONArray temp = json_polygon.getJSONArray(j);
                        try {
                            LatLng cords = new LatLng(temp.getDouble(0), temp.getDouble(1));
                            polygon.add(cords);
                        } catch (Exception e) {
                        }
                    }
                }

                JSONObject address = ij.getJSONObject("address");
                String street = address.getString("street");
                String s = street + "\n" +
                        address.getString("city") + " " +
                        address.getString("province") + " " +
                        address.getString("country") + "\n" +
                        address.getString("postal");

                Building b = new Building(lat, lng, name, code, street, s, short_name, polygon);

                getCampuses().get(ij.getString("campus")).addFeature(b);
            } catch (JSONException e) {
                Log.e("setUpBuildings", "BUILDING EXCEPTION", e);
            }
        }
    }

    private void setUpFood(AssetManager assetManager) throws Exception {


        JSONArray arr = Util.getBaseObj(assetManager, "food.json")
                .getJSONArray("food");

        // lat,  lng,  name, address,  short_name,  url,  imageURL,  desc,  hours,  tags
        for (int i = 0; i < arr.length(); i++) {
            try {
                JSONObject ij = arr.getJSONObject(i);
                double lat = ij.getDouble("lat");
                double lng = ij.getDouble("lng");
                String name = ij.getString("name");
                String short_name = ij.getString("short_name");
                String address = ij.getString("address");
                String url = ij.getString("url");
                String imageURL = ij.getString("image");
                String desc = ij.getString("description");
                JSONObject h = ij.getJSONObject("hours");

                Hours hours = new Hours(h);
                String[] tags = Util.toStringArray(ij.getJSONArray("tags"));

                Food f = new Food(lat, lng, name, address, short_name, url, imageURL, desc, hours,
                        tags);
                getCampuses().get(ij.getString("campus")).addFeature(f);
            } catch (JSONException e) {
                Log.e("setUpFood", "FOOD EXCEPTION", e);
            }
        }
    }

    private void setUpBikes(AssetManager assetManager) throws Exception {
        JSONArray arr = Util.getBaseObj(assetManager, "bicycle-racks.json")
                .getJSONArray("markers");

        for (int i = 0; i < arr.length(); i++) {
            try {
                JSONObject ij = arr.getJSONObject(i);
                double lat = ij.getDouble("lat");
                double lng = ij.getDouble("lng");
                String name = ij.getString("title");
                String buildingCode = ij.getString("buildingCode");

                String desc = ij.getString("desc");

                BikePark b = new BikePark(lat, lng, name, buildingCode, desc);

                //TODO: Change check to look for <64> in <sublayer> json array
                if (!name.contains("BIXI"))  //get rid of bikeshare, at least for now.
                    getCampuses().get("UTSG").addFeature(b);

            } catch (JSONException e) {
                Log.e("setUpBikes", "BIKE EXCEPTION", e);
            }
        }
    }

    private void setUpCars(AssetManager assetManager) throws Exception {
        JSONArray arr = Util.getBaseObj(assetManager, "parking-lots.json")
                .getJSONArray("markers");

        for (int i = 0; i < arr.length(); i++) {
            try {
                JSONObject ij = arr.getJSONObject(i);
                double lat = ij.getDouble("lat");
                double lng = ij.getDouble("lng");
                String name = ij.getString("title");
//                String aka = ij.getString("aka");
                String address = ij.getString("address");
//                String access = ij.getString("access");
                String buildingCode = ij.getString("buildingCode");
                String phone = ij.getString("phone");

                String desc = ij.getString("desc");

                CarPark c = new CarPark(lat, lng, name, buildingCode, address, desc, phone);

                getCampuses().get("UTSG").addFeature(c);

            } catch (JSONException e) {
                Log.e("setUpCars", "CAR EXCEPTION", e);
            }
        }
    }
}
