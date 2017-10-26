package tech.edt.MapApp;

import android.media.Image;
import android.os.Parcel;

import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by class on 2017-10-24.
 * Extension of Feature class, every Building is a Search suggestion
 */

public class Food extends Feature implements SearchSuggestion {

    private String address;
    private String short_name;


    private String url;
    private String desc;
    private String[] tags;
    private Hours hours;
    private BitmapDescriptor image;


    public Food(double lat, double lng, String name, String address, String short_name, String url, String imageURL, String desc, Hours hours, String[] tags) {
        super(lat, lng, name);
        this.address = address;
        this.short_name = short_name;
        this.hours = hours;
        this.tags = tags;
        this.url = url;
        this.desc = desc;
        this.image = BitmapDescriptorFactory.fromPath(imageURL);
    }

    public BitmapDescriptor getIcon() {
        return Util.getFoodBMP();
    }

    public String getAddress(String code) {
        return address;
    }

    /**
     * Search suggestion stuff
     */
    public static final String CREATOR = "EDT";

    public String getUrl() {
        return url;
    }

    public String getDesc() {
        return desc;
    }

    public String[] getTags() {
        return tags;
    }

    public Hours getHours() {
        return hours;
    }

    public BitmapDescriptor getImage() {
        return image;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public String toShortString() {
        //rename all short_strings in food.json and replace this with return short_name
        return getName();
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

    }

    @Override
    public String getSnippet() {
        String weekDay;
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.US);

        Calendar calendar = Calendar.getInstance();
        weekDay = dayFormat.format(calendar.getTime());

        Hours.Interval i = this.hours.getHours(weekDay);
        if (i.isClosed())
            return "Closed on " + weekDay;
        return i.toString();
    }
}
