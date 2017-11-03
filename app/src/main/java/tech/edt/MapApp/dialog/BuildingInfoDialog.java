package tech.edt.MapApp.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import tech.edt.MapApp.R;
import tech.edt.MapApp.feature.Building;

/**
 * Created by class on 2017-10-26.
 * Building Info Dialog
 */

public class BuildingInfoDialog extends Dialog implements
        android.view.View.OnClickListener {

    private TextView title, main_box;
    private Button exit_button;
    private Building f;

    public BuildingInfoDialog(Activity a, Building f) {
        super(a);
        this.f = f;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.basic_card_layout);

        title = (TextView) findViewById(R.id.basic_title);
        title.setText(f.getName());

        main_box = (TextView) findViewById(R.id.basic_main);
        main_box.setText(f.getAddress());

        exit_button = (Button) findViewById(R.id.basic_exit);
        exit_button.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        dismiss();
    }
}
