package tech.edt.MapApp.feature;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Parcel;
import android.util.Log;

import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.google.android.gms.maps.model.BitmapDescriptor;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import tech.edt.MapApp.Hours;
import tech.edt.MapApp.util.BitmapDescriptorWithID;
import tech.edt.MapApp.util.Util;

/**
 * Created by class on 2017-10-24.
 * Extension of Feature class, every Building is a Search suggestion
 */

public class Food extends Feature implements SearchSuggestion {

    private String address;
    private String url;
    private String desc;
    private String[] tags;
    private Hours hours;
    private Bitmap image;


    public Food(double lat, double lng, String name, String address, String short_name, String url,
                String imageURL, String desc, Hours hours, String[] tags) {
        super(lat, lng, name, true, true);
        new GetImageTask().execute(imageURL);
        this.address = address;
        this.hours = hours;
        this.tags = tags;
        this.url = url;
        this.desc = desc;
    }


    private class GetImageTask extends AsyncTask<String, Void, Bitmap> {

        protected Bitmap doInBackground(String... urls) {
            try {
                URL im = new URL(urls[0]);
                image = BitmapFactory.decodeStream(im.openStream());
            } catch (MalformedURLException e) {
                Log.e("getImageTask", "MalformedURLException", e);

            } catch (IOException e) {
                Log.e("getImageTask", "IOException", e);

            }
            return image;
        }

        protected void onPostExecute(Bitmap bitmap) {
            // TODO: check this.exception
            // TODO: do something with the feed
        }
    }

    public BitmapDescriptorWithID getBitmapDescriptor() {
        return Util.getFoodBMP();
    }

    public String getAddress() {
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


    public Bitmap getImage() {
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
