package tech.edt.MapApp.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import tech.edt.MapApp.R;
import tech.edt.MapApp.feature.StudentService;

/**
 * Created by Murad on 11/3/17.
 */

public class ServiceInfoDialog extends Dialog implements android.view.View.OnClickListener {

    private StudentService f;
    private TextView title, main_box;
    private Button exit_button;

    public ServiceInfoDialog(Activity a, StudentService f) {
        super(a);
        this.f = f;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.service_card_layout);

        title = (TextView) findViewById(R.id.service_title);
        title.setText(f.getName());

        main_box = (TextView) findViewById(R.id.service_main);
        main_box.setText(f.getDialogText());

        exit_button = (Button) findViewById(R.id.service_exit);
        exit_button.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        dismiss();
    }
}



