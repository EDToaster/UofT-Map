package tech.edt.MapApp.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import tech.edt.MapApp.R;
import tech.edt.MapApp.feature.GreenSpace;

/**
 * Created by Murad on 11/3/17.
 */

public class GreenSpaceDialog extends Dialog implements View.OnClickListener {

    private GreenSpace f;
    private TextView title, main_box;
    private Button exit_button;

    public GreenSpaceDialog(Activity a, GreenSpace f) {
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
        main_box.setText(f.getDialogText());

        exit_button = (Button) findViewById(R.id.basic_exit);
        exit_button.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        dismiss();
    }
}



