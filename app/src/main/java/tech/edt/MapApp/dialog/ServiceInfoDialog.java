package tech.edt.MapApp.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;

import tech.edt.MapApp.feature.StudentService;

/**
 * Created by Murad on 11/3/17.
 */

public class ServiceInfoDialog extends Dialog implements android.view.View.OnClickListener {
    public ServiceInfoDialog(Activity a, StudentService s) {
        super(a);
    }

    @Override
    public void onClick(View view) {

    }
}
