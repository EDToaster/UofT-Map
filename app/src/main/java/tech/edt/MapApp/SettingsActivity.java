package tech.edt.MapApp;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;

public class SettingsActivity extends AppCompatActivity {

    public static final String KEY_POLYGON_VISIBLE = "polygon_visible";
    public static final String KEY_TIME_FORMAT = "time_format";
    public static final String KEY_WEEK_FORMAT = "week_format";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();

        SharedPreferences sharedPref =
                PreferenceManager.getDefaultSharedPreferences(this);
        Boolean switchPref = sharedPref.getBoolean
                (SettingsActivity.KEY_POLYGON_VISIBLE, true);
    }
}
