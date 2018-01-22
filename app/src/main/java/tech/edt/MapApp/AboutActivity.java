package tech.edt.MapApp;


import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

/**
 * Created by murad on 1/11/18.
 * <p>
 * A simple about window
 */

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //back button
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        //creates the about page
        View aboutPage = new AboutPage(this)
                .isRTL(false)
                .setDescription("Unofficial University of Toronto map app. Designed and " +
                        "Developed by Howard Chen and Murad Akhundov in 2018")
                .setImage(R.drawable.uoft)
                .addItem(new Element().setTitle("Version " + BuildConfig.VERSION_NAME))
                .addGitHub("https://github.com/EDToaster/MapAppRepo", "Github")
                .create();

        setContentView(aboutPage);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}

