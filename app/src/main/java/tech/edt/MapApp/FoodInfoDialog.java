package tech.edt.MapApp;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by class on 2017-10-26.
 * Building Info Dialog
 */

public class FoodInfoDialog extends Dialog implements
        android.view.View.OnClickListener {

    private TextView title, main_box, hours_box;
    private Button exit_button;
    private ImageView image;
    private Food f;
    private Bitmap bmp;
    private Activity a;

    public FoodInfoDialog(Activity a, Food f) {
        super(a);
        this.a = a;
        this.f = f;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.food_card_layout);

        title = (TextView) findViewById(R.id.food_title);
        title.setText(f.getName());

        main_box = (TextView) findViewById(R.id.food_main);
        main_box.setText(f.getDesc());

        hours_box = (TextView) findViewById(R.id.food_hours);
        hours_box.setText(f.getHours().toString());

        image = (ImageView) findViewById(R.id.food_image);
        bmp = f.getImage();
        if (bmp != null)
            image.setImageBitmap(bmp);

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ImageInfoDialog(a).show();
            }
        });

        exit_button = (Button) findViewById(R.id.food_exit);
        exit_button.setOnClickListener(this);
    }

    private class ImageInfoDialog extends Dialog implements android.view.View.OnClickListener {
        ImageInfoDialog(Activity a) {
            super(a);
        }

        private Button exit_button;
        private ImageView image_image;
        private TextView text;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.image_card_layout);


            image_image = (ImageView) findViewById(R.id.image_image);
            image_image.setImageBitmap(bmp);

            text = (TextView) findViewById(R.id.image_title);
            text.setText(f.getName());

            exit_button = (Button) findViewById(R.id.image_exit);
            exit_button.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            dismiss();
        }
    }

    @Override
    public void onClick(View view) {
        dismiss();
    }
}
