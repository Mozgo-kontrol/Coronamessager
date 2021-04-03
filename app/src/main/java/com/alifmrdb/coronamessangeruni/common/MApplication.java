package com.alifmrdb.coronamessangeruni.common;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;



/**
 * Created by IF on 14.01
 * Online listener to update users status in Firebase
 *
 * */
public class MApplication extends Application {

    private static final String TAG = "MApplication";
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

    }
    public MApplication(){}
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

    }

    public static void setOnlineListener(FirebaseUser currentUser) {
        DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        if (currentUser != null) {
            final DatabaseReference isUserOnlineRef = mDatabaseReference.child(NodeNames.USERS).child(currentUser.getUid()).child(NodeNames.USERONLINE);
            final DatabaseReference lastOnlineRef = mDatabaseReference.child(NodeNames.USERS).child(currentUser.getUid()).child(NodeNames.LASTONLINE);
            final DatabaseReference connectedRef = database.getReference(".info/connected");
            connectedRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NotNull DataSnapshot snapshot) {
                    boolean connected = snapshot.getValue(Boolean.class);

                    if (connected) {
                        isUserOnlineRef.setValue(true);
                        isUserOnlineRef.onDisconnect().setValue(false);
                        lastOnlineRef.onDisconnect().setValue(ServerValue.TIMESTAMP);
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Log.e(TAG,"Listener was cancelled at .info/connected" );

                }
            });
        }
    }
}
