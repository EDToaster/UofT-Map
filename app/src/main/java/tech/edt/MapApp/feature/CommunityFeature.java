package tech.edt.MapApp.feature;

import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;

/**
 * Created by Murad on 11/3/17.
 * A feature submitted by the uoft community
 */

public class CommunityFeature extends Feature implements SearchSuggestion{

    private String address;
    private String type;
    private String description;
    private String url;


    CommunityFeature(double lat, double lng, String name, String address,
                     String type, String description) {
        super(lat, lng, name, true, true);

        this.address = address;
        this.type = type;
        this.description = description;

    }

    CommunityFeature(double lat, double lng, String name, String address,
                     String type, String description, String url) {
        super(lat, lng, name, true, true);

        this.address = address;
        this.type = type;
        this.description = description;
        this.url = url;


    }


    //discuss implementation
}
