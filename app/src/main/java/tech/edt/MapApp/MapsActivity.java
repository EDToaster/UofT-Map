package tech.edt.MapApp;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Gravity;
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
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

//TODO: Move to different threads
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FloatingSearchView mSearchView;
    public final String CREATOR = "Howard Chen";

    private ArrayList<Feature> UTSG;
    private ArrayList<Feature> UTM;
    private ArrayList<Feature> UTSC;
    private ArrayList<Feature> current_selected;
    private ArrayList<Feature> all_markers;

    private static LatLng UTSGLL;
    private static LatLng UTMLL;
    private static LatLng UTSCLL;

    private static final int MY_PERMISSIONS_FINE_LOCATION = 101;
    private static final float FOCUSED_ZOOM = 18f;
    private static final float DEFAULT_ZOOM = 15f;

    //feature visibilities
    private boolean buildingVisible = false;
    private boolean foodVisible = false;
    private boolean carparkVisible = false;
    private boolean bikeparkVisible = false;
    private boolean isHybrid = true;

    private Feature persistent;

    private GetResultsTask current_task;
    private Drawer result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapsInitializer.initialize(this);
        Util.init();

        //Data Crunching
        setUpFeatures();

        Comparator cmp = new Comparator<Feature>() {
            public int compare(Feature f1, Feature f2) {
                return f1.toString().compareTo(f2.toString());
            }
        };

        Collections.sort(UTSG, cmp);
        Collections.sort(UTM, cmp);
        Collections.sort(UTSC, cmp);
        current_selected = UTSG;
        //End Data Crunching

        //Default

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        mSearchView = (FloatingSearchView) findViewById(R.id.floating_search_view);
        UTSGLL = new LatLng(43.6644, -79.3923);
        UTMLL = new LatLng(43.5479, -79.6609);
        UTSCLL = new LatLng(43.7841, -79.1868);

        mSearchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
            @Override
            public void onSearchTextChanged(String oldQuery, final String newQuery) {
                if (current_task != null)
                    current_task.cancel(true);

                current_task = new GetResultsTask();
                current_task.execute(newQuery);
                //pass them on to the search view


            }

        });
        mSearchView.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSuggestionClicked(SearchSuggestion searchSuggestion) {
                if (searchSuggestion instanceof Feature) {
                    Feature suggestion = (Feature) searchSuggestion;
                    mSearchView.setSearchFocused(false);
                    mSearchView.setSearchText(suggestion.toShortString());
                    LatLng ll = suggestion.getLatLng();

                    Marker tempMarker = suggestion.getMarker(mMap);
                    tempMarker.setVisible(true);
                    tempMarker.showInfoWindow();
                    persistent = suggestion;
                    goToNinja(ll, FOCUSED_ZOOM);
                }
            }

            @Override
            public void onSearchAction(String currentQuery) {
//                if (suggestions.size() == 1) // if only one suggestion left, choose that
//                    this.onSuggestionClicked(suggestions.get(0));

            }
        });

//        mSearchView.setOnMenuItemClickListener(new FloatingSearchView.OnMenuItemClickListener() {
//
//            @Override
//            public void onActionMenuItemSelected(MenuItem item) {
//                if (item.getItemId() == R.id.action_location) {
//                    centerOnMe();
//                } else if (item.getItemId() == R.id.action_food) {
//                    setVisibilityAndUpdateMarkers("food");
//                } else if (item.getItemId() == R.id.action_building) {
//                    setVisibilityAndUpdateMarkers("building");
//                } else if (item.getItemId() == R.id.action_hybrid) {
//                    setHybrid();
//                }
//            }
//
//
//        });


        //TODO: Implement the Navbar
        //nav drawer
        new DrawerBuilder().withActivity(this).build();

        final PrimaryDrawerItem itemSG = new PrimaryDrawerItem().withIdentifier(1)
                .withName(R.string.drawer_item_UTSG).withSelectable(false);
        final PrimaryDrawerItem itemSC = new PrimaryDrawerItem().withIdentifier(5)
                .withName(R.string.drawer_item_UTSC).withSelectable(false);
        final PrimaryDrawerItem itemM = new PrimaryDrawerItem().withIdentifier(6)
                .withName(R.string.drawer_item_UTM).withSelectable(false);

        final SecondaryDrawerItem food = new SecondaryDrawerItem().withIdentifier(21)
                .withName("Food").withSelectable(false).withIcon(R.drawable.food_marker);
        final SecondaryDrawerItem building = new SecondaryDrawerItem().withIdentifier(22)
                .withName("Buildings").withSelectable(false).withIcon(R.drawable.building_marker);
        final SecondaryDrawerItem car = new SecondaryDrawerItem().withIdentifier(23)
                .withName("Parking").withSelectable(false);
        final SecondaryDrawerItem bike = new SecondaryDrawerItem().withIdentifier(24)
                .withName("Bike Racks").withSelectable(false);
        final SecondaryDrawerItem accessibility = new SecondaryDrawerItem().withIdentifier(25)
                .withName("Accessibility").withSelectable(false);
        final SecondaryDrawerItem safety = new SecondaryDrawerItem().withIdentifier(26)
                .withName("Safety").withSelectable(false);
        final SecondaryDrawerItem green = new SecondaryDrawerItem().withIdentifier(27)
                .withName("Green Spaces").withSelectable(false);
        final SecondaryDrawerItem community = new SecondaryDrawerItem().withIdentifier(28)
                .withName("Community Features").withSelectable(false);


        SecondaryDrawerItem settings = new SecondaryDrawerItem().withIdentifier(12)
                .withName("Settings").withSelectable(false);
        SecondaryDrawerItem feedback = new SecondaryDrawerItem().withIdentifier(13)
                .withName("Feedback").withSelectable(false);
        SecondaryDrawerItem about = new SecondaryDrawerItem().withIdentifier(14)
                .withName("About").withSelectable(false);

        final SecondaryDrawerItem hybrid = new SecondaryDrawerItem().withIdentifier(71)
                .withName("Hybrid").withSelectable(false);
        final SecondaryDrawerItem mapview = new SecondaryDrawerItem().withIdentifier(72)
                .withName("Normal").withSelectable(false);


        result = new DrawerBuilder() //result is a global navbar
                .withActivity(this)
                .addDrawerItems(
                        //removed campus header.
                        itemSG.withTag("c_UTSG").withSetSelected(true),
                        itemM.withTag("c_UTM"),
                        itemSC.withTag("c_UTSC"),

                        new SectionDrawerItem().withName("Layers").withTextColor(Color.BLUE),
                        building.withTag("f_building"),
                        food.withTag("f_food"),
                        bike.withTag("f_bikepark"),
                        car.withTag("f_carpark"),
                        accessibility.withTag("f_accessibility"),
                        safety.withTag("f_safety"),
                        green.withTag("f_green"),
                        community.withTag("f_community"),
                        new SectionDrawerItem().withName("Map").withTextColor(Color.BLUE),
                        hybrid.withTag("m_hybrid"),
                        mapview.withTag("m_map"),

                        new DividerDrawerItem(),

                        settings.withTag("s_settings"),
                        about.withTag("s_about"),
                        feedback.withTag("s_feedback")
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {


                        String tag = (String) drawerItem.getTag();
                        if (tag.startsWith("f_")) {
                            drawerItem.withSetSelected(!drawerItem.isSelected());
                            setVisibilityAndUpdateMarkers(tag.substring(2).trim(), drawerItem.isSelected());
                            updateResult(drawerItem);

                        } else if (tag.startsWith("s_")) {
                            if (tag.substring(2).equals("feedback")) {
                                Uri uri = Uri.parse("http://www.example.com");
                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                startActivity(intent);
                            } else if (tag.substring(2).equals("settings")) {
                                Toast.makeText(getApplicationContext(), "coming soon",
                                        Toast.LENGTH_LONG).show();
                            } else if (tag.substring(2).equals("about")) {
                                Toast.makeText(getApplicationContext(), "Unofficial University"
                                                + " of Toronto Map app." +
                                                "\n\nDesigned and Developed by Howard Chen and " +
                                                "Murad Akhundov in 2017.",
                                        Toast.LENGTH_LONG).show();
                            }


                        } else if (tag.startsWith("c_")) {
                            if (tag.substring(2).trim().equals("UTSG")) {
                                current_selected = UTSG;
                                goToNinja(UTSGLL, DEFAULT_ZOOM);
                                itemM.withSetSelected(false);
                                itemSC.withSetSelected(false);
                                itemSG.withSetSelected(true);

                            } else if (tag.substring(2).trim().equals("UTM")) {
                                current_selected = UTM;
                                goToNinja(UTMLL, DEFAULT_ZOOM);
                                itemM.withSetSelected(true);
                                itemSC.withSetSelected(false);
                                itemSG.withSetSelected(false);
                            } else if (tag.substring(2).trim().equals("UTSC")) {
                                current_selected = UTSC;
                                goToNinja(UTSCLL, DEFAULT_ZOOM);
                                itemM.withSetSelected(false);
                                itemSC.withSetSelected(true);
                                itemSG.withSetSelected(false);
                            }
                            updateResult(itemM);
                            updateResult(itemSC);
                            updateResult(itemSG);
                            result.closeDrawer();
                            //reset current marker
                            persistent = null;
                            setVisibilityAndUpdateMarkers("layers", false);

                            //enable all selected layers
                            setVisibilityAndUpdateMarkers("building",
                                    building.isSelected());
                            setVisibilityAndUpdateMarkers("food",
                                    food.isSelected());
                            setVisibilityAndUpdateMarkers("accessibility",
                                    accessibility.isSelected());
                            setVisibilityAndUpdateMarkers("green",
                                    green.isSelected());
                            setVisibilityAndUpdateMarkers("carpark",
                                    car.isSelected());
                            setVisibilityAndUpdateMarkers("bikepark",
                                    bike.isSelected());
                            setVisibilityAndUpdateMarkers("safety",
                                    safety.isSelected());
                            setVisibilityAndUpdateMarkers("community",
                                    community.isSelected());

                        } else if (tag.startsWith("m_")) {
                            if (tag.substring(2).equals("map")) {
                                mapview.withSetSelected(!mapview.isSelected());
                                hybrid.withSetSelected(!mapview.isSelected());
                            } else if (tag.substring(2).equals("hybrid")) {
                                mapview.withSetSelected(!mapview.isSelected());
                                hybrid.withSetSelected(!hybrid.isSelected());
                            }
                            setHybrid(hybrid.isSelected());
                            updateResult(mapview);
                            updateResult(hybrid);
                        }
                        return true;
                    }
                })
                .build();
        itemSG.withSetSelected(true); //SG selected first
        mapview.withSetSelected(true);
        result.updateItem(itemSG);
        result.updateItem(mapview);


        mSearchView.setOnLeftMenuClickListener(
                new FloatingSearchView.OnLeftMenuClickListener() {
                    @Override
                    public void onMenuOpened() {
                        result.openDrawer();
                    }

                    //FIXME: Hamburger icon only!
                    @Override
                    public void onMenuClosed() {
                        result.openDrawer();
                    }
                });


    }

    private void updateResult(IDrawerItem item) {
        result.updateItem(item);
    }

    private class GetResultsTask extends AsyncTask<String, Void, ArrayList<Feature>> {

        private Exception exception;
        private ArrayList<Feature> suggestions;

        protected ArrayList<Feature> doInBackground(String... args) {
            String newQuery = args[0];

            suggestions = new ArrayList<>();

            if (!newQuery.equals(""))
                for (Feature i : current_selected) {
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

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mSearchView.hasFocusable())
                        mSearchView.swapSuggestions(suggestions);
                    else
                        mSearchView.clearSuggestions();
                }
            });
            return suggestions;
        }

    }

    private void setHybrid(boolean flag) {
        isHybrid = flag;
        if (isHybrid)
            mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        else
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }


    /**
     * Toggles visibility of all markers of a specific type
     *
     * @param: String type
     * type of the feature
     * typecodes:"building", "food", "carpark", "bikepark"
     **/
    //NOTE: I changed this because the feature visibility was getting messed up when changing campuses
    private void setVisibilityAndUpdateMarkers(String type, boolean isSelected) {
        if (type.equals("building") || type.equals("layers"))
            buildingVisible = isSelected;
        if (type.equals("food") || type.equals("layers"))
            foodVisible = isSelected;
        if (type.equals("carpark") || type.equals("layers"))
            carparkVisible = isSelected;
        if (type.equals("bikepark") || type.equals("layers"))
            bikeparkVisible = isSelected;
        refreshMarkers();

    }

    private void refreshMarkers() {
        for (Feature place : all_markers)
            place.getMarker(mMap).setVisible(false);

        for (Feature place : current_selected) {
            if (place instanceof Building)
                place.getMarker(mMap).setVisible(buildingVisible);
            else if (place instanceof Food)
                place.getMarker(mMap).setVisible(foodVisible);
        }

        if (persistent != null)
            persistent.getMarker(mMap).setVisible(true);
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
        setHybrid(false);

        goToNinja(UTSGLL, DEFAULT_ZOOM);

        refreshMarkers();
        for (Feature i : this.all_markers)
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
                } else if (f instanceof Food) {
                    FoodInfoDialog fid = new FoodInfoDialog(MapsActivity.this, (Food) f);
                    fid.show();
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

    private void setUpFeatures() {
        this.UTSG = new ArrayList<>();
        this.UTM = new ArrayList<>();
        this.UTSC = new ArrayList<>();
        this.all_markers = new ArrayList<>();

        AssetManager assetManager = getAssets();
        try {
            setUpBuildings(assetManager);
            setUpFood(assetManager);
        } catch (Exception e) {
            Log.e("setUpFeatures", "Exception", e);
        }

        this.all_markers.addAll(UTSG);
        this.all_markers.addAll(UTM);
        this.all_markers.addAll(UTSC);
    }

    private void setUpBuildings(AssetManager assetManager) throws Exception {
        //Buildings
        InputStream input = assetManager.open("buildings.json");
        String buildings = Util.convertStreamToString(input);
        JSONObject obj = new JSONObject(buildings);
        JSONArray arr = obj.getJSONArray("buildings");


        for (int i = 0; i < arr.length(); i++) {
            try {
                JSONObject ij = arr.getJSONObject(i);

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

                Building b = new Building(lat, lng, name, code, street, s, short_name);

                if (ij.getString("campus").equals("UTSG"))
                    this.UTSG.add(b);
                else if (ij.getString("campus").equals("UTM"))
                    this.UTM.add(b);
                else if (ij.getString("campus").equals("UTSC"))
                    this.UTSC.add(b);
            } catch (JSONException e) {

            }
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

                Food f = new Food(lat, lng, name, address, short_name, url, imageURL, desc, hours, tags);

                if (ij.getString("campus").equals("UTSG"))
                    this.UTSG.add(f);
                else if (ij.getString("campus").equals("UTM"))
                    this.UTM.add(f);
                else if (ij.getString("campus").equals("UTSC"))
                    this.UTSC.add(f);
            } catch (JSONException e) {
                Log.e("setUpFood", "FOOD EXCEPTION", e);
            }
        }
    }

    public void goToNinja(LatLng ll, float zoom) {
        CameraUpdate up;
        if (zoom == -1) up = CameraUpdateFactory.newLatLng(ll);
        else up = CameraUpdateFactory.newLatLngZoom(ll, zoom);
        mMap.animateCamera(up, 2000, null);
    }

}
