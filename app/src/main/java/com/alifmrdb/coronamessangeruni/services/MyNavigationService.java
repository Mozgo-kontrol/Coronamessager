package com.alifmrdb.coronamessangeruni.services;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.alifmrdb.coronamessangeruni.SignUpActivity;
import com.alifmrdb.coronamessangeruni.common.NodeNames;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.concurrent.TimeUnit;
/**
 *
 * 17.01.2021
 *
 *
 * */
public class MyNavigationService extends Service {

    private static final String TAG = "MyNavigationService";
    private boolean FlagforWhile = true;
    private FirebaseUser _firebaseUser;
    private FirebaseAuth _firebaseAuth;
    private double count = 0.1;
    private DatabaseReference _databaseReference;
    private String _userID;
    public MyNavigationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    private LocationServiceInterface locationService;

    @Override
    public void onCreate() {
        //initial the Storage reference
        _firebaseAuth = FirebaseAuth.getInstance();
        _firebaseUser = _firebaseAuth.getCurrentUser();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        locationService = new LocationService(this);
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();
        Log.w(TAG, "service starting");
        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job
        if( _firebaseUser!=null){

            _userID =  _firebaseUser.getUid();
            updateLocation();
            Log.w(TAG, "updateLocation, user is not null");
        }

        else {Log.w(TAG, "current user is null");}
        // If we get killed, after returning from here, restart
        return START_STICKY;
    }
    @Override
    public void onDestroy() {
        FlagforWhile = false;
        Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
        Log.w(TAG, "service done");
    }

    void updateLocation() {
        new Thread(() -> {
            while (FlagforWhile) {

                CoordinateWrapper coordinates = locationService.getUsersLocation();
                if (coordinates.getLatitude() != 0.0) {
                    updateUserPositionFireBase(coordinates.getLatitude(), coordinates.getLongitude());
                    Log.w(TAG, "landitude was updated bei user :"+coordinates.getLatitude()+" : "+coordinates.getLongitude() +_userID);
                }
                try {
                    //every 15 second
                    TimeUnit.SECONDS.sleep(15);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void updateUserPositionFireBase(double latitude,double longitude){
        _databaseReference = FirebaseDatabase.getInstance()
                .getReference();
        _databaseReference.child(NodeNames.USERS).child(_userID).child(NodeNames.LATITUDE).setValue(latitude).addOnCompleteListener(task ->
                Log.w(TAG, "landitude was updated bei user :"+ _userID)).addOnFailureListener(e ->
                Log.e(TAG, "landitude failure user :"+ _userID + e.getMessage()));

        _databaseReference.child(NodeNames.USERS).child(_userID).child(NodeNames.LONGITUDE).setValue(longitude).addOnCompleteListener(task ->
                Log.w(TAG, "longitude was updated bei user :"+ _userID)).addOnFailureListener(e ->
                Log.e(TAG, "longitude failure user :"+ _userID + e.getMessage()));

    }

}