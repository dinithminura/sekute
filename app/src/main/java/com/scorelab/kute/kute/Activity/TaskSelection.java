package com.scorelab.kute.kute.Activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.scorelab.kute.kute.R;

/**
 * Created by nrv on 2/8/17.
 */

public class TaskSelection extends AppCompatActivity {

    ImageView trackMeImage;
    ImageView PublishMeImage;
    AlertDialog dialogSelectVehicle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.taskselection);

        trackMeImage=(ImageView)findViewById(R.id.trackVehicle);
        PublishMeImage=(ImageView)findViewById(R.id.UpdateMe);

        trackMeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        PublishMeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }




}
