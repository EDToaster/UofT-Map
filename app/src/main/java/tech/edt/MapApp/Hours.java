package tech.edt.MapApp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by class on 2017-10-26.
 * Hours class for Features with hour limitations
 */

public class Hours {

    public final Interval sunday, monday, tuesday, wednesday, thursday, friday, saturday;


    public Hours(JSONObject hours) throws JSONException {
        this.sunday = new Interval(hours.getJSONObject("sunday"));
        this.monday = new Interval(hours.getJSONObject("monday"));
        this.tuesday = new Interval(hours.getJSONObject("tuesday"));
        this.wednesday = new Interval(hours.getJSONObject("wednesday"));
        this.thursday = new Interval(hours.getJSONObject("thursday"));
        this.friday = new Interval(hours.getJSONObject("friday"));
        this.saturday = new Interval(hours.getJSONObject("saturday"));
    }

    protected class Interval {
        SimpleDateFormat localDateFormat = new SimpleDateFormat("HH:mm");
        private boolean closed;
        private Date open, close;

        protected Interval(JSONObject interval) throws JSONException {
            this.closed = interval.getBoolean("closed");
            this.open = new Date((long) interval.getInt("open") * 1000);
            this.close = new Date((long) interval.getInt("close") * 1000);
        }

        public String toString() {
            return localDateFormat.format(open) + " -> " + localDateFormat.format(close);
        }

        public boolean isClosed() {
            return closed;
        }
    }


    /**
     * returns the corresponding interval based on the day of the week it is
     * takes full day names
     **/
    public Interval getHours(String day) {

        switch (day) {
            case "Monday":
                return monday;
            case "Tuesday":
                return tuesday;
            case "Wednesday":
                return wednesday;
            case "Thursday":
                return thursday;
            case "Friday":
                return friday;
            case "Saturday":
                return saturday;
            case "Sunday":
                return sunday;
        }
        return null;
    }


}
