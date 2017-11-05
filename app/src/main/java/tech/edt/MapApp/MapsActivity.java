package tech.edt.MapApp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
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
import android.support.v7.preference.PreferenceManager;
import android.text.util.Linkify;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
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
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.util.ArrayList;
import java.util.regex.Pattern;

import tech.edt.MapApp.dialog.BuildingInfoDialog;
import tech.edt.MapApp.dialog.FoodInfoDialog;
import tech.edt.MapApp.dialog.GreenSpaceDialog;
import tech.edt.MapApp.dialog.ServiceInfoDialog;
import tech.edt.MapApp.feature.BikePark;
import tech.edt.MapApp.feature.Building;
import tech.edt.MapApp.feature.CarPark;
import tech.edt.MapApp.feature.CommunityFeature;
import tech.edt.MapApp.feature.Feature;
import tech.edt.MapApp.feature.Food;
import tech.edt.MapApp.feature.GreenSpace;
import tech.edt.MapApp.feature.Safety;
import tech.edt.MapApp.feature.StudentService;
import tech.edt.MapApp.feature.University;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FloatingSearchView mSearchView;
    public final String CREATOR = "Howard Chen";

    private static final int MY_PERMISSIONS_FINE_LOCATION = 101;
    private static final float FOCUSED_ZOOM = 18f;
    private static final float DEFAULT_ZOOM = 15f;

    //feature visibilities
    private boolean buildingVisible = false;
    private boolean foodVisible = false;
    private boolean carparkVisible = false;
    private boolean bikeparkVisible = false;
    private boolean studentVisible = false;
    private boolean greenVisible = false;
    private boolean communityVisible = false;
    private boolean safetyVisible = false;
    private boolean isHybrid = true;
    private Polygon buildingPolygon;
    private boolean polygonEnabled;

    private University uni;

    private ArrayList<Feature> persistent = new ArrayList<>();

    private GetSearchResultsTask current_task;
    private Drawer result;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapsInitializer.initialize(this);

        setUpPreferencesAndUniversity();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        setUpSearchBar();
        setUpDrawers();
        final SharedPreferences mSharedPreference= PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        polygonEnabled = mSharedPreference.getBoolean("polygon_visible", true);
    }

    private void setUpPreferencesAndUniversity() {

        String sg = getString(R.string.drawer_item_UTSG);
        String m = getString(R.string.drawer_item_UTM);
        String sc = getString(R.string.drawer_item_UTSC);

        //Data Crunching
        uni = new University(getBaseContext(), sg, m, sc).setUpFeatures(getAssets());
        //Default Campus
        uni.setCurrentSelected(getPreference("def_campus", "UTSG"));
    }

    private void writePreference(String key, String value) {

        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.apply();
    }

    private String getPreference(String key, String default_val) {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        String pref = sharedPref.getString(key, null);
        if (pref == null) {
            writePreference(key, default_val);
            return default_val;
        }
        return pref;
    }

    private void setUpSearchBar() {
        mSearchView = (FloatingSearchView) findViewById(R.id.floating_search_view);

        mSearchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
            @Override
            public void onSearchTextChanged(String oldQuery, final String newQuery) {
                new GetSearchResultsTask().execute(newQuery);
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
                    persistent.clear();
                    persistent.add(suggestion);
                    refreshMarkers();
                    tempMarker.showInfoWindow();

                    if (searchSuggestion instanceof Building) {
                        setPolygon((Building) searchSuggestion);
                    }


                    goToNinja(ll, FOCUSED_ZOOM);
                }
            }

            @Override
            public void onSearchAction(String currentQuery) {
                //if (suggestions.size() == 1) // if only one suggestion left, choose that
                //this.onSuggestionClicked(suggestions.get(0));
                //TODO: implement
            }
        });


        mSearchView.setOnMenuItemClickListener(new FloatingSearchView.OnMenuItemClickListener() {
            @Override
            public void onActionMenuItemSelected(MenuItem item) {
                if (item.getItemId() == R.id.action_location) {
                    centerOnMe();
                }
                if (item.getItemId() == R.id.action_submit) {
                    openLink("https://goo.gl/forms/tTxBcOQAOGpY8Ci33");
                }
            }
        });


        mSearchView.setOnLeftMenuClickListener(
                new FloatingSearchView.OnLeftMenuClickListener() {
                    @Override
                    public void onMenuOpened() {
                        result.openDrawer();
                        mSearchView.closeMenu(true);
                    }

                    @Override
                    public void onMenuClosed() {
                        result.openDrawer();

                    }
                });

    }

    private void setUpDrawers() {
        //nav drawer
        new DrawerBuilder().withActivity(this).build();

        //all the items
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

        final SecondaryDrawerItem bike = new SecondaryDrawerItem().withIdentifier(24)
                .withName("Bike Racks").withSelectable(false).withIcon(R.drawable.bike_marker);

        final SecondaryDrawerItem car = new SecondaryDrawerItem().withIdentifier(23)
                .withName("Parking").withSelectable(false).withIcon(R.drawable.car_marker);

        final SecondaryDrawerItem studentService = new SecondaryDrawerItem().withIdentifier(25)
                .withName("Student Services").withSelectable(false).
                        withIcon(R.drawable.student_marker);
        final SecondaryDrawerItem safety = new SecondaryDrawerItem().withIdentifier(26)
                .withName("Safety").withSelectable(false).withIcon(GoogleMaterial.
                        Icon.gmd_local_hospital);
        final SecondaryDrawerItem green = new SecondaryDrawerItem().withIdentifier(27)
                .withName("Green Spaces").withSelectable(false).withIcon(GoogleMaterial.
                        Icon.gmd_local_florist);
        final SecondaryDrawerItem community = new SecondaryDrawerItem().withIdentifier(28)
                .withName("Community Features").withSelectable(false).withIcon(GoogleMaterial.
                        Icon.gmd_star);


        SecondaryDrawerItem settings = new SecondaryDrawerItem().withIdentifier(12)
                .withName("Settings").withSelectable(false).withIcon(GoogleMaterial.
                        Icon.gmd_settings);
        final SecondaryDrawerItem feedback = new SecondaryDrawerItem().withIdentifier(13)
                .withName("Feedback").withSelectable(false).withIcon(GoogleMaterial.
                        Icon.gmd_feedback);
        SecondaryDrawerItem about = new SecondaryDrawerItem().withIdentifier(14)
                .withName("About").withSelectable(false).withIcon(GoogleMaterial.
                        Icon.gmd_info);

        final SecondaryDrawerItem hybrid = new SecondaryDrawerItem().withIdentifier(71)
                .withName("Hybrid").withSelectable(false).withIcon(GoogleMaterial.
                        Icon.gmd_satellite);
        final SecondaryDrawerItem normal = new SecondaryDrawerItem().withIdentifier(72)
                .withName("Normal").withSelectable(false).withIcon(GoogleMaterial.
                        Icon.gmd_map);


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
                        studentService.withTag("f_student-service"),
                        safety.withTag("f_safety"),
                        green.withTag("f_green"),
                        community.withTag("f_community"),
                        new SectionDrawerItem().withName("Map").withTextColor(Color.BLUE),
                        hybrid.withTag("m_hybrid"),
                        normal.withTag("m_map"),

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
                            setVisibilityAndUpdateMarkers(tag.substring(2).trim(),
                                    drawerItem.isSelected());
                            updateResult(drawerItem);

                        } else if (tag.startsWith("s_")) {
                            String option = tag.substring(2).trim();
                            switch (option) {
                                case "feedback":
                                    openLink("https://docs.google.com/forms/d/11kMs5L2V" +
                                            "tIeVsLFnllGzxuR6jch28Pe76UF7nmDnYXU\"");
                                    break;
                                case "settings":
                                    Intent intent = new Intent(getApplicationContext(),
                                            SettingsActivity.class);
                                    startActivity(intent);
                                    //return true;
                                    break;
                                case "about":
                                    Toast.makeText(getApplicationContext(), "Unofficial " +
                                                    "University of Toronto Map app." +
                                                    "\n\nDesigned and Developed by Howard Chen and "
                                                    + "Murad Akhundov in 2017.",
                                            Toast.LENGTH_LONG).show();
                                    break;
                            }


                        } else if (tag.startsWith("c_")) {
                            String camp = tag.substring(2).trim();
                            switch (camp) {
                                case "UTSG":
                                    itemM.withSetSelected(false);
                                    itemSC.withSetSelected(false);
                                    itemSG.withSetSelected(true);
                                    break;
                                case "UTM":
                                    itemM.withSetSelected(true);
                                    itemSC.withSetSelected(false);
                                    itemSG.withSetSelected(false);
                                    break;
                                case "UTSC":
                                    itemM.withSetSelected(false);
                                    itemSC.withSetSelected(true);
                                    itemSG.withSetSelected(false);
                                    break;
                            }
                            if (uni.setCurrentSelected(tag.substring(2).trim())) {
                                goToNinja(uni.getCurrentSelected().getLatLng(), DEFAULT_ZOOM);
                                writePreference("def_campus", camp);
                            }
                            updateResult(itemM);
                            updateResult(itemSC);
                            updateResult(itemSG);
                            result.closeDrawer();
                            //reset current marker
                            persistent.clear();
                            setVisibilityAndUpdateMarkers("layers", false);

                            //enable all selected layers
                            setVisibilityAndUpdateMarkers("building",
                                    building.isSelected());
                            setVisibilityAndUpdateMarkers("food",
                                    food.isSelected());
                            setVisibilityAndUpdateMarkers("student-service",
                                    studentService.isSelected());
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
                                normal.withSetSelected(!normal.isSelected());
                                hybrid.withSetSelected(!normal.isSelected());
                            } else if (tag.substring(2).equals("hybrid")) {
                                normal.withSetSelected(!normal.isSelected());
                                hybrid.withSetSelected(!hybrid.isSelected());
                            }
                            setHybrid(hybrid.isSelected());
                            updateResult(normal);
                            updateResult(hybrid);
                        }
                        return true;
                    }
                })
                .build();
        itemSG.withSetSelected(true); //SG selected first
        normal.withSetSelected(true);
        result.updateItem(itemSG);
        result.updateItem(normal);
    }

    /**
     * replaces the polygon on the map with a new selected building polygon
     *
     * @param building the building to draw the outline of
     */
    private void setPolygon(Building building) {

        if (polygonEnabled) {

            removePolygon();

            PolygonOptions rectOptions = new PolygonOptions();
            rectOptions.addAll(building.getPolygon());
            rectOptions.strokeColor(Color.CYAN);
            rectOptions.strokeWidth(5);
            buildingPolygon = mMap.addPolygon(rectOptions);
        }
    }

    /**
     * Updates the drawer with the new settings of a drawer item
     *
     * @param item the item to update
     */
    private void updateResult(IDrawerItem item) {
        result.updateItem(item);
    }

    /**
     * An asynchronous task to update the results
     * task.execute takes @param newQuery - the new search term
     */
    private class GetSearchResultsTask extends AsyncTask<String, Void, ArrayList<Feature>> {

        private ArrayList<Feature> suggestions;

        /**
         * Cancels the previous task before running this one
         */
        @Override
        protected void onPreExecute() {
            if (current_task != null)
                current_task.cancel(true);
            current_task = this;
        }

        protected ArrayList<Feature> doInBackground(String... args) {
            String newQuery = args[0];

            suggestions = new ArrayList<>();

            if (!newQuery.equals("")) {
                String[] toks = newQuery.toLowerCase().trim().split("(\\s+)");
                ArrayList<Pattern> pats = new ArrayList<>();
                for (String tok : toks) {
                    pats.add(Pattern.compile("(\\b" + tok + ")"));
                }
                for (Feature i : uni.getCurrentSelected().getFeatures()) {
                    //skip if feature is non-searchable
                    if (!i.isSearchable())
                        continue;
                    String get = i.getStrippedMatchString();

                    boolean toPut = true;
                    for (Pattern p : pats) {
                        if (!p.matcher(get).find()) {
                            toPut = false;
                            break;
                        }
                    }
                    if (toPut)
                        suggestions.add(i);
                }
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

    /**
     * Opens a link in the default browser
     *
     * @param link link to the webpage
     */
    private void openLink(String link) {
        Uri uri = Uri.parse(link);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    /**
     * Toggles hybrid/normal map appearance
     *
     * @param flag
     */
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
     * @param type       type of the feature
     *                   types:"building", "food", "carpark", "bikepark"
     * @param isSelected the value to set the tag to
     **/
    private void setVisibilityAndUpdateMarkers(String type, boolean isSelected) {
        //changed to switch-case for improved readability. considering switching to hashmap
        switch (type) {
            case "building":
                buildingVisible = isSelected;
                break;
            case "food":
                foodVisible = isSelected;
                break;
            case "carpark":
                carparkVisible = isSelected;
                break;
            case "bikepark":
                bikeparkVisible = isSelected;
                break;
            case "student-service":
                studentVisible = isSelected;
                break;
            case "community":
                communityVisible = isSelected;
                break;
            case "safety":
                safetyVisible = isSelected;
                break;
            case "green":
                greenVisible = isSelected;
                break;
            case "layers":
                bikeparkVisible = foodVisible = carparkVisible = bikeparkVisible = safetyVisible
                        = communityVisible = safetyVisible = greenVisible = isSelected;
                break;

        }
        refreshMarkers();

    }

    /**
     * Updates the marker visibility based on featureVisible attributes
     */
    private void refreshMarkers() {
        for (Feature place : uni.getAllFeatures())
            place.getMarker(mMap).setVisible(false);

        for (Feature place : uni.getCurrentSelected().getFeatures()) {
            if (place instanceof Building)
                place.getMarker(mMap).setVisible(buildingVisible);
            else if (place instanceof Food)
                place.getMarker(mMap).setVisible(foodVisible);
            else if (place instanceof BikePark)
                place.getMarker(mMap).setVisible(bikeparkVisible);
            else if (place instanceof CarPark)
                place.getMarker(mMap).setVisible(carparkVisible);
            else if (place instanceof StudentService)
                place.getMarker(mMap).setVisible(studentVisible);
            else if (place instanceof CommunityFeature)
                place.getMarker(mMap).setVisible(communityVisible);
            else if (place instanceof GreenSpace)
                place.getMarker(mMap).setVisible(greenVisible);
            else if (place instanceof Safety)
                place.getMarker(mMap).setVisible(safetyVisible);
        }

        for (Feature p : persistent)
            p.getMarker(mMap).setVisible(true);
    }

    /**
     * Removes the recent polygon
     */
    private void removePolygon() {
        if (buildingPolygon != null)
            buildingPolygon.remove();
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
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.setPadding(0, 170, 0, 0);

        setHybrid(false); //starts with a normal map


        //goToNinja(uni.getCurrentSelected().getLatLng(), DEFAULT_ZOOM);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(uni.getCurrentSelected().getLatLng(),
                15));


        refreshMarkers();
        for (Feature i : uni.getAllFeatures())
            i.getMarker(mMap); //create the marker for each feature


        // enable my location
        if (ActivityCompat.checkSelfPermission(
                this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_FINE_LOCATION);
            }
        }

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

            @Override
            public void onInfoWindowClick(Marker marker) {
                Feature f = (Feature) marker.getTag();
                if (f instanceof Building) {
                    BuildingInfoDialog bid = new BuildingInfoDialog(MapsActivity.this,
                            (Building) f);
                    bid.show();
                } else if (f instanceof Food) {
                    FoodInfoDialog fid = new FoodInfoDialog(MapsActivity.this, (Food) f);
                    fid.show();
                } else if (f instanceof StudentService) {
                    ServiceInfoDialog sid = new ServiceInfoDialog(MapsActivity.this,
                            (StudentService) f);
                    sid.show();
                } else if (f instanceof GreenSpace) {
                    GreenSpaceDialog sid = new GreenSpaceDialog(MapsActivity.this,
                            (GreenSpace) f);
                    sid.show();
                }
            }
        });
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng arg0) {
                removePolygon();
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                removePolygon();
                if (marker.getTag() instanceof Building) {
                    setPolygon((Building) marker.getTag());
                }
                return false;
            }
        });
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                //TODO: add link support
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
                snippet.setAutoLinkMask(Linkify.WEB_URLS);

                info.addView(title);
                info.addView(snippet);

                return info;
            }
        });


    }

    /**
     * Centres view on the current location of the user
     */
    private void centerOnMe() {
        try {
            LocationManager locationManager = (LocationManager)
                    getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();

            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.
                    ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.
                    checkSelfPermission(this, android.Manifest.
                            permission.ACCESS_COARSE_LOCATION)
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

    /**
     * Goes to the specified LatLng location at the zoom level.
     * zoom = -1 for current level
     *
     * @param ll   location of the destination
     * @param zoom zoom level for the movement
     *             -1: for current level
     */
    public void goToNinja(LatLng ll, float zoom) {
        CameraUpdate up;
        if (zoom == -1) up = CameraUpdateFactory.newLatLng(ll);
        else up = CameraUpdateFactory.newLatLngZoom(ll, zoom);
        mMap.animateCamera(up, 2000, null);
    }

}
