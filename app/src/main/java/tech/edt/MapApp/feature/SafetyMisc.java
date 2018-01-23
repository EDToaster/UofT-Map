package tech.edt.MapApp.feature;

import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;

/**
 * Created by Murad on 11/3/17.
 * Other safety features
 */
//TODO: Consider removing and making safety non-abstract
public class SafetyMisc extends  Safety implements SearchSuggestion {


    SafetyMisc(double lat, double lng, String name) {
        super(lat, lng, name, true);
    }
}
