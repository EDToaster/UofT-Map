package tech.edt.MapApp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

/**
 * Created by class on 2017-10-25.
 * Search interface for the map
 */

public class SearchActivity extends AppCompatActivity {
    TextView ac;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ac = (TextView) findViewById(R.id.search_bar);

    }

}
