package com.alifmrdb.coronamessangeruni.services;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.alifmrdb.coronamessangeruni.LoginActivity;
import com.alifmrdb.coronamessangeruni.MainActivity;
import com.alifmrdb.coronamessangeruni.R;

import java.util.concurrent.TimeUnit;

import static androidx.core.content.ContextCompat.getSystemService;

public class LocationService implements LocationServiceInterface {
    Context myContext;
    LocationManager mLocationManager;

    private double _longitude;
    private double _latitude;

    public LocationService(Context myContext) {
        this.myContext = myContext;

        mLocationManager = (LocationManager) myContext.getSystemService(Context.LOCATION_SERVICE);

        Handler handler = new Handler();

        Runnable runnable = new Runnable() {
            public void run() {
                if (ActivityCompat.checkSelfPermission(myContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(myContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                float LOCATION_REFRESH_DISTANCE = 1;
                long LOCATION_REFRESH_TIME = 100;

                LocationListener mlocationListener = location -> {
                    Log.d("Location", "Location did changed");
                    _longitude = location.getLongitude();
                    _latitude = location.getLatitude();
                };

                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,LOCATION_REFRESH_TIME, LOCATION_REFRESH_DISTANCE, mlocationListener);
            }
        };

        runnable.run();
    }

    public CoordinateWrapper getUsersLocation() {
        return new CoordinateWrapper(_latitude, _longitude);
    }
}

