package tech.edt.MapApp.feature;

import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;

/**
 * Created by Murad on 11/3/17.
 */

public class SafetyMisc extends  Safety implements SearchSuggestion {
    SafetyMisc(double lat, double lng, String name) {
        super(lat, lng, name, true);
    }
}
