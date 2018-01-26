package tech.edt.MapApp.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.View;
import android.widget.TextView;

import tech.edt.MapApp.R;
import tech.edt.MapApp.feature.Building;

/**
 * Created by murad on 1/25/18.
 */

public class BottomSheetBuilding extends BottomSheetDialogFragment  {


    @SuppressLint("RestrictedApi")
    public void setupDialog(final Dialog dialog, int style, Building feature, Activity a) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.bottomsheet_food, null);
        dialog.setContentView(contentView);

        TextView title = (TextView) a.findViewById(R.id.basic_title);
        title.setText(feature.getName());

        TextView main_box = (TextView) a.findViewById(R.id.basic_main);
        main_box.setText(feature.getAddress());
    }
}