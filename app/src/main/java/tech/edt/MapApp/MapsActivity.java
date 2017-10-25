package tech.edt.MapApp;

import android.content.Context;
import android.content.res.AssetManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private HashMap<String, Feature> features;
    private SimpleCursorAdapter myAdapter;

    private String[] strArrData = {"No Suggestions"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapsInitializer.initialize(this);
        Util.init();


        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        try {
            setUpFeatures();
        } catch (Exception e) {
            System.exit(1);
        }
    }

    public void changeToSearch(View view) {
        setContentView(R.layout.activity_search);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        goToNinja(43.6644, -79.3923, 15f);
        for (Feature i : this.features.values()) {
            mMap.addMarker(new MarkerOptions().position(i.getLatLng()).icon(i.getIcon()));
        }
    }

    private void setUpFeatures() throws Exception {
        this.features = new HashMap<>();

        AssetManager assetManager = getAssets();
        InputStream input = assetManager.open("buildings.json");
        String buildings = Util.convertStreamToString(input);
        JSONObject obj = new JSONObject(buildings);
        JSONArray arr = obj.getJSONArray("buildings");


        for (int i = 0; i < arr.length(); i++) {
            JSONObject ij = arr.getJSONObject(i);
            if (!ij.getString("campus").equals("UTSG"))
                continue;
            double lat = ij.getDouble("lat");
            double lng = ij.getDouble("lng");
            String name = ij.getString("name");
            String code = ij.getString("code");

            JSONObject address = ij.getJSONObject("address");
            String s = address.getString("street") + "\n" + address.getString("city") + address.getString("province") + address.getString("country") + "\n" + address.getString("postal");

            this.features.put(code + " - " + name, new Building(lat, lng, name, code, s));

        }

    }


//    public void jumpTo(View view) throws IOException {
//        EditText et = (EditText) findViewById(R.id.text_search);
//        TextView tv = (TextView) findViewById(R.id.search_results);
//        tv.setText("");
//
//        String loc = et.getText().toString();
//        Geocoder gc = new Geocoder(this);
//        List<Address> list = gc.getFromLocationName(loc, 10);
//
//        for (Address i : list) tv.append(i.getLocality() + "\n");
//        tv.append(String.valueOf(list.size()));
//
//        Address address = list.get(0);
//        String locality = address.getLocality();
//
//        Toast.makeText(this, locality, Toast.LENGTH_SHORT).show();
//        double lat = address.getLatitude();
//        double lng = address.getLongitude();
//
//        goToLocation(locality, lat, lng, 15f);

//    }

    public void goToNinja(double lat, double lng, float zoom) {
        LatLng ll = new LatLng(lat, lng);
        CameraUpdate up = CameraUpdateFactory.newLatLngZoom(ll, zoom);
        mMap.moveCamera(up);
    }


//    public void goToLocation(String locality, double lat, double lng, float zoom) {
//        LatLng ll = new LatLng(lat, lng);
//        mMap.addMarker(new MarkerOptions().position(ll).title(locality));
//        CameraUpdate up = CameraUpdateFactory.newLatLngZoom(ll, zoom);
//        mMap.moveCamera(up);
//    }
//
//    public void goToLocation(String locality, double lat, double lng) {
//        this.goToLocation(locality, lat, lng, 1.0f);
//    }


}
