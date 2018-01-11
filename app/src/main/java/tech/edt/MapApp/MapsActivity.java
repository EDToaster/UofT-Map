package tech.edt.MapApp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.HashMap;
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

import static tech.edt.MapApp.SettingsActivity.*;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FloatingSearchView mSearchView;

    @FieldVariableDoc("The current university object")
    private University uni;
    /**
     * Constants
     */
    public final String CREATOR = "Howard Chen";
    private static final int MY_PERMISSIONS_FINE_LOCATION = 101;
    private static final float FOCUSED_ZOOM = 18f;
    private static final float DEFAULT_ZOOM = 15f;

    /**
     * Non - persistent feature visibilities
     */
    private HashMap<String, Boolean> featureVisibilty = new HashMap<>();
    private boolean isHybrid = true;

    /**
     * Drawer global variables
     */
    private ArrayList<PrimaryDrawerItem> campusDrawerItems;
    private ArrayList<SecondaryDrawerItem> hybridDrawerItems;

    private Drawer result;

    /**
     * Settings / Preferences
     */
    SharedPreferences preferences;
    SharedPreferences.OnSharedPreferenceChangeListener listener;
    private HashMap<String, Boolean> appSettings;

    /**
     * Current state variables
     * Will change constantly
     */
    private Polygon buildingPolygon;
    private ArrayList<Feature> persistent = new ArrayList<>();
    private GetSearchResultsTask current_task;


    /**
     * Called when the map activity is created
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapsInitializer.initialize(this);

        setAllFeatureVisibility(false);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        setUpSearchBar();
        setUpDrawers();

        setUpPreferencesAndUniversity();
    }

    /**
     * Sets up the setting for visibility of all features
     *
     * @param visible set visible/invisible
     */
    private void setAllFeatureVisibility(boolean visible) {
        featureVisibilty.put(T_BUILDING, visible);
        featureVisibilty.put(T_FOOD, visible);
        featureVisibilty.put(T_SS, visible);
        featureVisibilty.put(T_CAR, visible);
        featureVisibilty.put(T_BIKE, visible);
        featureVisibilty.put(T_GREEN, visible);
        featureVisibilty.put(T_COM, visible);
        featureVisibilty.put(T_SAFETY, visible);
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
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.setPadding(0, 170, 0, 0);

        refreshMapUI("all");

        goToNinja(uni.getCurrentSelected().getLatLng(), DEFAULT_ZOOM, false);
        toast("Select your campus\nfrom side bar");

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
     * Sets the university object and initializes it with the default campus specified in the
     * preference file
     */
    private void setUpPreferencesAndUniversity() {
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        listener =
                new SharedPreferences.OnSharedPreferenceChangeListener() {
                    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                        updateAppSettings();
                        refreshMapUI(key);
                    }
                };

        appSettings = new HashMap<>();

        updateAppSettings();

        String sg = "UTSG";
        String m = "UTM";
        String sc = "UTSC";

        //Data Crunching
        uni = new University(getBaseContext(), sg, m, sc).setUpFeatures(getAssets());
        //Default Campus
        uni.setCurrentSelected(getPreference(KEY_DEFAULT_CAMPUS, "UTSG"));
        setCampusUISelected(uni.getCurrentSelected().getDrawerTag());
        preferences.registerOnSharedPreferenceChangeListener(listener);
    }

    /**
     * Updates the appSettings from the preference file
     */
    private void updateAppSettings() {
        appSettings.put(KEY_POLYGON_VISIBLE, preferences.getBoolean(KEY_POLYGON_VISIBLE, true));
        appSettings.put(KEY_START_HYBRID, preferences.getBoolean(KEY_START_HYBRID, false));
        appSettings.put(KEY_SHOW_ZOOM, preferences.getBoolean(KEY_SHOW_ZOOM, true));
    }

    /**
     * Writes a specific preference to the shared preference file
     *
     * @param key   the key to write
     * @param value the value corresponding with the key
     */
    private void writePreference(String key, String value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    /**
     * Gets a String value from the shared preference file
     *
     * @param key         the key to get
     * @param default_val the value to return if key does not exist in the file
     * @return the key value
     */
    private String getPreference(String key, String default_val) {
        String pref = preferences.getString(key, null);
        if (pref == null) {
            writePreference(key, default_val);
            return default_val;
        }
        return pref;
    }

    /**
     * Sets up the search bar
     */
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


                    goToNinja(ll, FOCUSED_ZOOM, true);
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

    public static final String T_UTSG = "c_UTSG";
    public static final String T_UTSC = "c_UTSC";
    public static final String T_UTM = "c_UTM";

    private static final String T_FOOD = "f_food";
    private static final String T_BUILDING = "f_building";
    private static final String T_BIKE = "f_bikepark";
    private static final String T_CAR = "f_carpark";
    private static final String T_SS = "f_student-service";
    private static final String T_SAFETY = "f_safety";
    private static final String T_GREEN = "f_green";
    private static final String T_COM = "f_community";

    private static final String T_SETTINGS = "s_settings";
    private static final String T_FEEDBACK = "s_feedback";
    private static final String T_ABOUT = "s_about";

    private static final String T_HYBRID = "m_hybrid";
    private static final String T_NORMAL = "m_normal";

    /**
     * Sets up the navigation drawer
     */
    private void setUpDrawers() {
        //nav drawer
        new DrawerBuilder().withActivity(this).build();

        //all the items
        final PrimaryDrawerItem itemSG = new PrimaryDrawerItem().withIdentifier(1)
                .withName(R.string.drawer_item_UTSG).withSelectable(false).withTag(T_UTSG);
        final PrimaryDrawerItem itemSC = new PrimaryDrawerItem().withIdentifier(5)
                .withName(R.string.drawer_item_UTSC).withSelectable(false).withTag(T_UTSC);
        final PrimaryDrawerItem itemM = new PrimaryDrawerItem().withIdentifier(6)
                .withName(R.string.drawer_item_UTM).withSelectable(false).withTag(T_UTM);

        final SecondaryDrawerItem food = new SecondaryDrawerItem().withIdentifier(21)
                .withName("Food").withSelectable(false).withIcon(R.drawable.food_marker).withTag(T_FOOD);

        final SecondaryDrawerItem building = new SecondaryDrawerItem().withIdentifier(22)
                .withName("Buildings").withSelectable(false).withIcon(R.drawable.building_marker).withTag(T_BUILDING);

        final SecondaryDrawerItem bike = new SecondaryDrawerItem().withIdentifier(24)
                .withName("Bike Racks").withSelectable(false).withIcon(R.drawable.bike_marker).withTag(T_BIKE);

        final SecondaryDrawerItem car = new SecondaryDrawerItem().withIdentifier(23)
                .withName("Parking").withSelectable(false).withIcon(R.drawable.car_marker).withTag(T_CAR);

        final SecondaryDrawerItem studentService = new SecondaryDrawerItem().withIdentifier(25)
                .withName("Student Services").withSelectable(false).
                        withIcon(R.drawable.student_marker).withTag(T_SS);
        final SecondaryDrawerItem safety = new SecondaryDrawerItem().withIdentifier(26)
                .withName("Safety").withSelectable(false).withIcon(GoogleMaterial.
                        Icon.gmd_local_hospital).withTag(T_SAFETY);
        final SecondaryDrawerItem green = new SecondaryDrawerItem().withIdentifier(27)
                .withName("Green Spaces").withSelectable(false).withIcon(GoogleMaterial.
                        Icon.gmd_local_florist).withTag(T_GREEN);
        final SecondaryDrawerItem community = new SecondaryDrawerItem().withIdentifier(28)
                .withName("Community Features").withSelectable(false).withIcon(GoogleMaterial.
                        Icon.gmd_star).withTag(T_COM);


        SecondaryDrawerItem settings = new SecondaryDrawerItem().withIdentifier(12)
                .withName("Settings").withSelectable(false).withIcon(GoogleMaterial.
                        Icon.gmd_settings).withTag(T_SETTINGS);
        final SecondaryDrawerItem feedback = new SecondaryDrawerItem().withIdentifier(13)
                .withName("Feedback").withSelectable(false).withIcon(GoogleMaterial.
                        Icon.gmd_feedback).withTag(T_FEEDBACK);
        SecondaryDrawerItem about = new SecondaryDrawerItem().withIdentifier(14)
                .withName("About").withSelectable(false).withIcon(GoogleMaterial.
                        Icon.gmd_info).withTag(T_ABOUT);

        final SecondaryDrawerItem hybrid = new SecondaryDrawerItem().withIdentifier(71)
                .withName("Hybrid").withSelectable(false).withIcon(GoogleMaterial.
                        Icon.gmd_satellite).withTag(T_HYBRID);
        final SecondaryDrawerItem normal = new SecondaryDrawerItem().withIdentifier(72)
                .withName("Normal").withSelectable(false).withIcon(GoogleMaterial.
                        Icon.gmd_map).withTag(T_NORMAL);

        campusDrawerItems = new ArrayList<>();
        campusDrawerItems.add(itemSG);
        campusDrawerItems.add(itemM);
        campusDrawerItems.add(itemSC);

        hybridDrawerItems = new ArrayList<>();
        hybridDrawerItems.add(hybrid);
        hybridDrawerItems.add(normal);


        result = new DrawerBuilder() //result is a global navbar
                .withActivity(this)
                .addDrawerItems(
                        //removed campus header.
                        itemSG,
                        itemM,
                        itemSC,

                        new SectionDrawerItem().withName("Layers").withTextColor(Color.BLUE),
                        building,
                        food,
                        bike,
                        car,
                        studentService,
                        safety,
                        green,
                        community,

                        new SectionDrawerItem().withName("Map").withTextColor(Color.BLUE),
                        hybrid,
                        normal,

                        new DividerDrawerItem(),
                        settings,
                        about,
                        feedback
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {


                        String tag = (String) drawerItem.getTag();
                        if (tag.startsWith("f_")) {
                            drawerItem.withSetSelected(!drawerItem.isSelected());
                            setVisibilityAndUpdateMarkers(tag,
                                    drawerItem.isSelected());
                            updateDrawerItem(drawerItem);
                        } else if (tag.startsWith("s_")) {
                            switch (tag) {
                                case T_FEEDBACK:
                                    openLink("https://docs.google.com/forms/d/11kMs5L2V" +
                                            "tIeVsLFnllGzxuR6jch28Pe76UF7nmDnYXU\"");
                                    break;
                                case T_SETTINGS:
                                    Intent intent = new Intent(getApplicationContext(),
                                            SettingsActivity.class);
                                    startActivity(intent);
                                    //return true;
                                    break;
                                case T_ABOUT:
                                    toast("Unofficial " +
                                            "University of Toronto Map app." +
                                            "\n\nDesigned and Developed by Howard Chen and "
                                            + "Murad Akhundov in 2018.");
                                    break;
                            }
                        } else if (tag.startsWith("c_")) {
                            String camp = tag.substring(2).trim();
                            if (uni.setCurrentSelected(camp)) {
                                goToNinja(uni.getCurrentSelected().getLatLng(), DEFAULT_ZOOM, true);
                                writePreference(KEY_DEFAULT_CAMPUS, camp);
                                setCampusUISelected(tag);
                            }
                            result.closeDrawer();

                            //reset current marker
                            persistent.clear();
                            refreshMarkers();
                        } else if (tag.startsWith("m_")) {
                            setHybridUISelected(tag.equals(T_HYBRID));
                            setHybrid(hybrid.isSelected());
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
     * In the navigation drawer, set the selected campus
     *
     * @param camp the campus to select:
     *             "UTSG", "UTM", "UTSC"
     */
    private void setCampusUISelected(String camp) {
        for (PrimaryDrawerItem item : campusDrawerItems) {
            if (item.getTag().equals(camp))
                item.withSetSelected(true);
            else
                item.withSetSelected(false);
            updateDrawerItem(item);
        }
    }

    /**
     * In the navigation drawer, set hybrid map selection
     *
     * @param isHybrid is the map hybrid or normal
     */
    private void setHybridUISelected(boolean isHybrid) {
        for (SecondaryDrawerItem item : hybridDrawerItems) {
            if (item.getTag().equals("m_hybrid"))
                item.withSetSelected(isHybrid);
            else
                item.withSetSelected(!isHybrid);
            updateDrawerItem(item);
        }
    }

    /**
     * Updates the drawer with the new appSettings of a drawer item
     *
     * @param item the item to update
     */
    private void updateDrawerItem(IDrawerItem item) {
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
     * @param flag set hybrid to true/false
     */
    private void setHybrid(boolean flag) {
        isHybrid = flag;
        if (isHybrid)
            mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        else
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }

    /**
     * Sets visibility of all markers of a specific type
     *
     * @param type       type of the feature
     *                   types:"building", "food", "carpark", "bikepark"
     * @param isSelected the value to set the tag to
     **/
    private void setVisibilityAndUpdateMarkers(String type, boolean isSelected) {
        switch (type) {
            case "layers":
                setAllFeatureVisibility(isSelected);
                break;
            default:
                featureVisibilty.put(type, isSelected);

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
                place.getMarker(mMap).setVisible(featureVisibilty.get(T_BUILDING));
            else if (place instanceof Food)
                place.getMarker(mMap).setVisible(featureVisibilty.get(T_FOOD));
            else if (place instanceof BikePark)
                place.getMarker(mMap).setVisible(featureVisibilty.get(T_BIKE));
            else if (place instanceof CarPark)
                place.getMarker(mMap).setVisible(featureVisibilty.get(T_CAR));
            else if (place instanceof StudentService)
                place.getMarker(mMap).setVisible(featureVisibilty.get(T_SS));
            else if (place instanceof CommunityFeature)
                place.getMarker(mMap).setVisible(featureVisibilty.get(T_COM));
            else if (place instanceof GreenSpace)
                place.getMarker(mMap).setVisible(featureVisibilty.get(T_GREEN));
            else if (place instanceof Safety)
                place.getMarker(mMap).setVisible(featureVisibilty.get(T_SAFETY));
        }

        for (Feature p : persistent)
            p.getMarker(mMap).setVisible(true);
    }

    /**
     * replaces the polygon on the map with a new selected building polygon
     *
     * @param buildings the buildings to draw the outline of
     */
    private void setPolygon(Feature... buildings) {
        removePolygon();
        if (appSettings.get("polygon_visible")) {
            for (Feature b : buildings) {
                try {
                    Building c = (Building) b;
                    PolygonOptions rectOptions = new PolygonOptions();
                    rectOptions.addAll(c.getPolygon());
                    rectOptions.strokeColor(Color.CYAN);
                    rectOptions.strokeWidth(5);
                    buildingPolygon = mMap.addPolygon(rectOptions);
                } catch (Exception e) {
                }

            }
        }
    }

    /**
     * Removes the last polygon
     */
    private void removePolygon() {
        if (buildingPolygon != null)
            buildingPolygon.remove();
    }

    /**
     * refreshes the map UI after any app setting changes
     * Call this whenever you alter appSettings
     *
     * @param key "all" to refresh all:
     *            "hybrid", "zoom", "campus"
     */
    private void refreshMapUI(String key) {
        if (key.equals(KEY_SHOW_ZOOM) || key.equals("all"))
            mMap.getUiSettings().setZoomControlsEnabled(appSettings.get(KEY_SHOW_ZOOM));

        if (key.equals(KEY_DEFAULT_CAMPUS) || key.equals("all"))
            setCampusUISelected(uni.getCurrentSelected().getDrawerTag());

        if (key.equals(KEY_START_HYBRID) || key.equals("all")) {
            setHybrid(appSettings.get(KEY_START_HYBRID));
            setHybridUISelected(appSettings.get(KEY_START_HYBRID));
        }

        if (key.equals(KEY_POLYGON_VISIBLE) || key.equals("all"))
            setPolygon(persistent.toArray(new Feature[]{}));

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
            goToNinja(ll, FOCUSED_ZOOM, true);
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
    public void goToNinja(LatLng ll, float zoom, boolean animate) {
        CameraUpdate up;
        if (zoom == -1) up = CameraUpdateFactory.newLatLng(ll);
        else up = CameraUpdateFactory.newLatLngZoom(ll, zoom);

        if (animate)
            mMap.animateCamera(up, 2000, null);
        else
            mMap.moveCamera(up);
    }

    /**
     * Makes a toast widget appear
     *
     * @param text the text to display
     */
    public void toast(String text) {
        Toast.makeText(getApplicationContext(), text,
                Toast.LENGTH_LONG).show();
    }
}

/**
 * Ignore this
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.FIELD)
@interface FieldVariableDoc {
    String value();
}
