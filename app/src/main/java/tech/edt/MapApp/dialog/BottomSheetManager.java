package tech.edt.MapApp.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import tech.edt.MapApp.R;
import tech.edt.MapApp.feature.Building;
import tech.edt.MapApp.feature.Feature;
import tech.edt.MapApp.feature.Food;
import tech.edt.MapApp.feature.GreenSpace;
import tech.edt.MapApp.feature.StudentService;

/**
 * Created by murad on 1/25/18.
 */

public class BottomSheetManager extends BottomSheetDialogFragment {
    public Feature f;

    public void setFeature(Feature f) {
        this.f = f;

    }

    @SuppressLint("RestrictedApi")
    public void setupDialog(final Dialog dialog, int style) {
        View contentView = null;

        if (f instanceof Building) {
            contentView = View.inflate(getContext(), R.layout.bottomsheet_basic, null);
            dialog.setContentView(contentView);
            super.setupDialog(dialog, style);

            Building feature = (Building) f;


            TextView main_box = (TextView) contentView.findViewById(R.id.basic_main);
            main_box.setText(feature.getAddress());


        } else if (f instanceof Food) {
            contentView = View.inflate(getContext(), R.layout.bottomsheet_food, null);
            dialog.setContentView(contentView);
            super.setupDialog(dialog, style);
            Food feature = (Food) f;




            TextView main_box = (TextView) contentView.findViewById(R.id.food_main);
            main_box.setText(feature.getDesc());

            TextView hours_box = (TextView) contentView.findViewById(R.id.food_hours);
            hours_box.setText(feature.getHours().toString());

            ImageView image = (ImageView) contentView.findViewById(R.id.food_image);
            Bitmap bmp = feature.getImage();
            if (bmp != null)
                image.setImageBitmap(bmp);


        } else if (f instanceof StudentService) {
            contentView = View.inflate(getContext(), R.layout.bottomsheet_basic, null);
            dialog.setContentView(contentView);
            super.setupDialog(dialog, style);
            StudentService feature = (StudentService) f;


            TextView main_box = (TextView) contentView.findViewById(R.id.basic_main);
            main_box.setText(feature.getDescription());


        } else if (f instanceof GreenSpace) {

            contentView = View.inflate(getContext(), R.layout.bottomsheet_basic, null);
            dialog.setContentView(contentView);
            super.setupDialog(dialog, style);
            GreenSpace feature = (GreenSpace) f;


            TextView main_box = (TextView) contentView.findViewById(R.id.basic_main);
            main_box.setText(feature.getDialogText());

        }

        ImageView image = (ImageView) contentView.findViewById(R.id.basic_icon);
        image.setImageResource(f.getBitmapDescriptor().getID());

        TextView title = (TextView) contentView.findViewById(R.id.basic_title);
        title.setText(f.getName());


    }
}