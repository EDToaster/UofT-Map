package tech.edt.MapApp;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
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

    private ArrayList<Feature> features;
    public static ArrayList<Feature> values;

    private static LatLng CAMPUSLATLNG;

    private static final int MY_PERMISSIONS_FINE_LOCATION = 101;
    private static final float FOCUSED_ZOOM = 18f;
    private static final float DEFAULT_ZOOM = 15f;

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

        setUpFeatures();

        values = (ArrayList<Feature>) features.clone();

        Collections.sort(values, new Comparator<Feature>() {
            public int compare(Feature f1, Feature f2) {
                return f1.toString().compareTo(f2.toString());
            }
        });

        mSearchView = (FloatingSearchView) findViewById(R.id.floating_search_view);
        suggestions = new ArrayList<>();
        CAMPUSLATLNG = new LatLng(43.6644, -79.3923);

        mSearchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
            @Override
            public void onSearchTextChanged(String oldQuery, final String newQuery) {
                suggestions.clear();
                if (!newQuery.equals(""))
                    for (Feature i : values) {
                        String[] toks = newQuery.toLowerCase().trim().split("(\\s+)");
                        boolean put = true;
                        for (String tok : toks) {
                            if (!i.getStrippedMatchString().contains(tok)) {
                                put = false;
                                break;
                            }
                        }
                        if (put)
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
                    LatLng ll = suggestion.getLatLng();

                    suggestion.getMarker(mMap).setVisible(true);

                    goToNinja(ll, FOCUSED_ZOOM);
                }
            }

            @Override
            public void onSearchAction(String currentQuery) {
                if (!suggestions.isEmpty())
                    this.onSuggestionClicked(suggestions.get(suggestions.size() - 1));
            }
        });

        mSearchView.setOnMenuItemClickListener(new FloatingSearchView.OnMenuItemClickListener() {

            @Override
            public void onActionMenuItemSelected(MenuItem item) {
                if (item.getItemId() == R.id.action_location) {
                    centerOnMe();

                } else if (item.getItemId() == R.id.action_food) {
                    //toggle food

                } else if (item.getItemId() == R.id.action_building) {
                    //toggle buildings
                }
            }
        });
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
//        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
//            @Override
//            public boolean onMarkerClick(Marker marker) {
//
//            }
//        });

        goToNinja(CAMPUSLATLNG, DEFAULT_ZOOM);

        for (Feature i : this.features)
            i.getMarker(mMap); //create the marker for each feature


        // enable my location
        if (ActivityCompat.checkSelfPermission(
                this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_FINE_LOCATION);
            }
        }

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

            @Override
            public void onInfoWindowClick(Marker marker) {
                Feature f = (Feature) marker.getTag();
                if (f instanceof Building) {
                    BuildingInfoDialog bid = new BuildingInfoDialog(MapsActivity.this, (Building) f);
                    bid.show();
                }
            }
        });
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {

                LinearLayout info = new LinearLayout(getBaseContext());
                info.setOrientation(LinearLayout.VERTICAL);

                TextView title = new TextView(getBaseContext());
                title.setTextColor(Color.BLACK);
                title.setGravity(Gravity.CENTER);
                title.setTypeface(null, Typeface.BOLD);
                title.setText(marker.getTitle());

                TextView snippet = new TextView(getBaseContext());
                snippet.setTextColor(Color.GRAY);
                snippet.setText(marker.getSnippet());

                info.addView(title);
                info.addView(snippet);

                return info;
            }
        });
    }

    private void centerOnMe() {
        try {
            LocationManager locationManager = (LocationManager)
                    getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();

            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.
                    ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.
                    checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            Location location = locationManager.getLastKnownLocation(locationManager
                    .getBestProvider(criteria, false));
            double lat = location.getLatitude();
            double lng = location.getLongitude();
            LatLng ll = new LatLng(lat, lng);
            goToNinja(ll, FOCUSED_ZOOM);
        } catch (Exception e) {//don't know the name whoops
            Toast.makeText(getApplicationContext(), "Error fetching location",
                    Toast.LENGTH_LONG).show();

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_FINE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this,
                            android.Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        mMap.setMyLocationEnabled(true);
                    }
                } else {
                    Toast.makeText(getApplicationContext(),
                            "This app requires location permissions to be granted",
                            Toast.LENGTH_LONG).show();
                    finish();
                }
                break;

        }
    }

    public static boolean implementsInterface(Object object, Class interf) {
        return interf.isInstance(object);
    }


    private void setUpFeatures() {
        this.features = new ArrayList<>();

        AssetManager assetManager = getAssets();
        try {
            setUpBuildings(assetManager);
            setUpFood(assetManager);
        } catch (Exception e) {
            Log.e("setUpFeatures", "Exception", e);
        }
    }

    private void setUpBuildings(AssetManager assetManager) throws Exception {
        //Buildings
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
            String street = address.getString("street");
            String s = street + "\n" +
                    address.getString("city") + " " +
                    address.getString("province") + " " +
                    address.getString("country") + "\n" +
                    address.getString("postal");

            this.features.add(new Building(lat, lng, name, code, street, s, short_name));

        }
    }

    private void setUpFood(AssetManager assetManager) throws Exception {

        //Food
        InputStream input = assetManager.open("food.json");
        String food = Util.convertStreamToString(input);
        JSONObject obj = new JSONObject(food);
        JSONArray arr = obj.getJSONArray("food");

        // lat,  lng,  name, address,  short_name,  url,  imageURL,  desc,  hours,  tags
        for (int i = 0; i < arr.length(); i++) {
            JSONObject ij = arr.getJSONObject(i);
            if (!ij.getString("campus").equals("UTSG"))
                continue;
            double lat = ij.getDouble("lat");
            double lng = ij.getDouble("lng");
            String name = ij.getString("name");
            String short_name = ij.getString("short_name");
            String address = ij.getString("address");
            String url = ij.getString("url");
            String imageURL = ij.getString("image");
            String desc = ij.getString("description");
            JSONObject h = null;
            try {
                h = ij.getJSONObject("hours");
            } catch (JSONException e) {
                Log.e("Tag", name, e);
            }
            Hours hours = new Hours(h);
            String[] tags = Util.toStringArray(ij.getJSONArray("tags"));

            this.features.add(new Food(lat, lng, name, address, short_name, url, imageURL, desc, hours, tags));

        }
    }

    public void goToNinja(LatLng ll, float zoom) {
        CameraUpdate up;
        if (zoom == -1) up = CameraUpdateFactory.newLatLng(ll);
        else up = CameraUpdateFactory.newLatLngZoom(ll, zoom);
        mMap.animateCamera(up, 2000, null);
    }


}
