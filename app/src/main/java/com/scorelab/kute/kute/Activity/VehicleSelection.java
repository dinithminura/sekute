package com.scorelab.kute.kute.Activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.scorelab.kute.kute.R;

/**
 * Created by nrv on 2/8/17.
 */

public class VehicleSelection extends AppCompatActivity {
    AutoCompleteTextView ACTV;
    String vehtype;
    Button vehselect;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vehicle_select_dialog);
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        ACTV= (AutoCompleteTextView)findViewById(R.id.vehiclename);
        ACTV.setVisibility(View.INVISIBLE);

        final ArrayAdapter<String> autoCompleteTrains = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1);
        database.child("Trains").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                autoCompleteTrains.clear();
                ACTV.clearListSelection();

                for (DataSnapshot suggestionSnapshot : dataSnapshot.getChildren()){
                    String suggestion = suggestionSnapshot.child("TrainName").getValue(String.class);
                    autoCompleteTrains.add(suggestion);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        final ArrayAdapter<String> autoCompleteBus = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1);
        database.child("Bus").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                autoCompleteBus.clear();
                ACTV.clearListSelection();
                for (DataSnapshot suggestionSnapshot : dataSnapshot.getChildren()){
                    String suggestion = suggestionSnapshot.child("BusrootNo").getValue(String.class);
                    autoCompleteBus.add(suggestion);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        vehselect=(Button)findViewById(R.id.vehicleselectiondone);
        vehselect.setEnabled(false);


        Button vehcancel=(Button)findViewById(R.id.vehicleselectioncancel);
        vehcancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_CANCELED,returnIntent);
                finish();
            }
        });




        ImageView busicon=(ImageView)findViewById(R.id.Bus);
        busicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vehtype="bus";
                ACTV.setVisibility(View.VISIBLE);
                ACTV.setAdapter(autoCompleteBus);
                vehselect.setEnabled(true);
            }
        });

        ImageView trainicon=(ImageView)findViewById(R.id.Train);
        trainicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vehtype="train";
                ACTV.setVisibility(View.VISIBLE);
                ACTV.setAdapter(autoCompleteTrains);
                vehselect.setEnabled(true);
            }
        });

        vehselect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("type",vehtype);
                returnIntent.putExtra("vehname",ACTV.getText().toString());
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
            }
        });

    }
}
