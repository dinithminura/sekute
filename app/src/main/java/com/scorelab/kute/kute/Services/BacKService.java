package com.scorelab.kute.kute.Services;

import android.Manifest;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.os.ResultReceiver;
import android.widget.Toast;

import com.scorelab.kute.kute.Util.MessageKey;

/**
 * Created by nrv on 2/9/17.
 */

public class BacKService extends Service implements LocationListener {
    ResultReceiver resultReceiver;

    protected LocationManager locationManager;
    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;
    public boolean canGetLocation = false;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1;
    private static final long MIN_TIME_BW_UPDATES = 1 * 60 * 1;
    Location location;
    private String provider;
    LocationManager m_locationManager;
    String vehkeyindex=null;

    // Create reciever object
    private BroadcastReceiver controlrecver = new ControlMessageRecever();

    // Set When broadcast event will fire.
    private IntentFilter filter = new IntentFilter(MessageKey.activityserviceintentName);

    // Register new broadcast receiver



    public BacKService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.m_locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        this.registerReceiver(controlrecver, filter);

    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        resultReceiver = intent.getParcelableExtra("receiver");
        this.m_locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

        }
        this.m_locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 5, this);
        this.m_locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 500, 5, this);

        return START_STICKY;
    }

    @Override
    public void onLocationChanged(Location location) {

        Bundle bundle = new Bundle();
        bundle.putParcelable("location",location);
        resultReceiver.send(MessageKey.MyLocationUpdate, bundle);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Bundle bundle = new Bundle();
        bundle.putString("provider",provider);
        bundle.putInt("status",status);
        bundle.putBundle("extras",extras);
        resultReceiver.send(MessageKey.onStatusChangedLocation, bundle);
    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    class ControlMessageRecever extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            String vehkey=intent.getStringExtra(MessageKey.vehiclekeyindex); //Firebase index of the vehicle
            vehkeyindex=vehkey;

        }
    }
}
