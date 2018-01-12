package tech.edt.MapApp;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

/**
 * Created by murad on 1/11/18.
 *
 * A simple about window
 */

public class AboutActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //back button
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        //creates the about page
        View aboutPage = new AboutPage(this)
                .isRTL(false)
                .setDescription("Unofficial University of Toronto Map" +
                        " app. Designed and Developed by Howard C" +
                        "hen and Murad Akhundov.\n\n2017-2018")
                .setImage(R.drawable.uoft)
                .addItem(new Element().setTitle("Version 0.1"))
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

