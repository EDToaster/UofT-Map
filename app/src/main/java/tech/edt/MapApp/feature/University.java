package tech.edt.MapApp.feature;

import com.google.android.gms.maps.model.LatLng;

import java.security.KeyException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by class on 2017-10-29.
 */


//TODO: reimplement to fit hierarchy
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


}
