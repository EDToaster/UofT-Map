package tech.edt.MapApp;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FloatingSearchView mSearchView;
    public final String CREATOR = "Howard Chen";

    private HashMap<String, Feature> features;
    public static String[] keys;
    public static ArrayList<Feature> values;

    private final static int MY_PERMISSIONS_FINE_LOCATION = 101;
    private ArrayList<Feature> suggestions;

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

        values = new ArrayList<Feature>(features.values());

        Collections.sort(values, new Comparator<Feature>() {
            public int compare(Feature f1, Feature f2) {
                return f1.toString().compareTo(f2.toString());
            }
        });

        mSearchView = (FloatingSearchView) findViewById(R.id.floating_search_view);
        suggestions = new ArrayList<>();

        mSearchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
            @Override
            public void onSearchTextChanged(String oldQuery, final String newQuery) {
                suggestions.clear();
                if (!newQuery.equals(""))
                    for (Feature i : values) {
                        if (i.getStrippedMatchString().contains(newQuery.toLowerCase()))
                            suggestions.add(i);
                    }

                //pass them on to the search view
                mSearchView.swapSuggestions(suggestions);

            }

        });
        mSearchView.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSuggestionClicked(SearchSuggestion searchSuggestion) {
                if (searchSuggestion instanceof Feature) {
                    Feature suggestion = (Feature) searchSuggestion;
                    mSearchView.setSearchText(suggestion.toShortString());
                    mSearchView.setSearchFocused(false);
                    mMap.clear();
                    LatLng ll = suggestion.getLatLng();
                    mMap.addMarker(suggestion.getMarkerOptions());
                    goToNinja(ll, -1f);
                }
            }

            @Override
            public void onSearchAction(String currentQuery) {
                if (!suggestions.isEmpty())
                    this.onSuggestionClicked(suggestions.get(suggestions.size() - 1));
            }
        });

    }

    private void centerOnMe() {
        LocationManager locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = locationManager.getLastKnownLocation(locationManager
                .getBestProvider(criteria, false));
        double lat = location.getLatitude();
        double lng = location.getLongitude();
        LatLng cords = new LatLng(lat, lng);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(cords, 18));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_FINE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        mMap.setMyLocationEnabled(true);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "This app requires location permissions to be granted", Toast.LENGTH_LONG).show();
                    finish();
                }
                break;

        }
    }

    public static boolean implementsInterface(Object object, Class interf) {
        return interf.isInstance(object);
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

        goToNinja(new LatLng(43.6644, -79.3923), 15f);
//        for (Feature i : this.features.values()) {
//            mMap.addMarker(new MarkerOptions().position(i.getLatLng()).icon(i.getIcon()));
//        }
// enable my location
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_FINE_LOCATION);
            }
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
            String short_name = ij.getString("short_name");

            JSONObject address = ij.getJSONObject("address");
            String s = address.getString("street") + "\n" + address.getString("city") + address.getString("province") + address.getString("country") + "\n" + address.getString("postal");

            this.features.put(code + " - " + name, new Building(lat, lng, name, code, s, short_name));

        }

    }

    public void goToNinja(LatLng ll, float zoom) {
        CameraUpdate up;
        if (zoom == -1) up = CameraUpdateFactory.newLatLng(ll);
        else up = CameraUpdateFactory.newLatLngZoom(ll, zoom);
        mMap.animateCamera(up, 2000, null);
    }


}