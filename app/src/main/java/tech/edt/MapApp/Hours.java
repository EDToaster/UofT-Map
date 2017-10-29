package tech.edt.MapApp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

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

    public class Interval {
        private SimpleDateFormat localDateFormat = new SimpleDateFormat("hh:mm a");
        private boolean closed;
        private long open, close;

        protected Interval(JSONObject interval) throws JSONException {
            localDateFormat.setTimeZone(TimeZone.getTimeZone("EDT"));
            this.closed = interval.getBoolean("closed");
            this.open = (long) interval.getInt("open") * 1000;
            this.close = (long) interval.getInt("close") * 1000;
        }

        public String toString() {
            return closed ? "Closed" : localDateFormat.format(open) + " -> " + localDateFormat.format(close);
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

    public String toString() {
        String s = "Sun:\t" + sunday.toString() +
                "\nMon:\t" + monday.toString() +
                "\nTue:\t" + tuesday.toString() +
                "\nWed:\t" + wednesday.toString() +
                "\nThu:\t" + thursday.toString() +
                "\nFri:\t" + friday.toString() +
                "\nSat:\t" + saturday.toString();

        return s;
    }


}
