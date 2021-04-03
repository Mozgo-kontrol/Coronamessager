package com.alifmrdb.coronamessangeruni;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.alifmrdb.coronamessangeruni.common.DatabaseIntentServiceResultReceiver;
import com.alifmrdb.coronamessangeruni.common.NodeNames;
import com.alifmrdb.coronamessangeruni.common.User;
import com.alifmrdb.coronamessangeruni.services.MyNavigationService;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * Created by IF on 14.01
 * UsersActivity
 *
 * */

public class UsersActivity extends AppCompatActivity implements UsersAdapter.UsersClickListener, DatabaseIntentServiceResultReceiver.Receiver  {

    private static final String TAG = "UsersActivity";

    private ArrayList <User> usersList;

    private RecyclerView recyclerView;
    private UsersAdapter mAdapter;

    private DatabaseReference _databaseReference;
    private FirebaseUser _currentUser;
    private String _currentUserId;

    ValueEventListener myListener;

    private double _currentUserLatitude;
    private double _currentUserLongitude;

    //--------------------------------
    private SharedPreferences searchPreferences;
    private SharedPreferences.Editor editor;

    private int preferenceDistance;
   // private int preferenceAgeMin;
   // private int preferenceAgeMax;
    private int preferenceSex;
    //--------------------------------
    //analytic
    private FirebaseAnalytics mFirebaseAnalytics;

    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);
        recyclerView =  findViewById(R.id.recycler_view);
        _currentUser = FirebaseAuth.getInstance().getCurrentUser();
        _currentUserId = _currentUser.getUid();

        usersList = new ArrayList();
        mAdapter = new UsersAdapter(usersList, this, true, UsersActivity.this);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());

        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setAdapter(mAdapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                mLayoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        _databaseReference = FirebaseDatabase.getInstance().getReference();
        //analistic
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        //------------------------------------------------------------------------------------------
        searchPreferences = getSharedPreferences(NodeNames.SEARCH_PREFERENCES, Context.MODE_PRIVATE);
        editor = searchPreferences.edit();
        preferenceDistance=  searchPreferences.getInt(NodeNames.DISTANCE_PREFERENCES, 20);
        preferenceSex =  searchPreferences.getInt(NodeNames.SEX_PREFERENCES, 0);
       // preferenceAgeMin = searchPreferences.getInt(NodeNames.AGE_MIN, 18);
       // preferenceAgeMax = searchPreferences.getInt(NodeNames.AGE_MAX, 99);
        //------------------------------------------------------------------------------------------
        _databaseReference.child(NodeNames.USERS).child(_currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                double currentUserTatitude;
                double currentUserLongitude;
                try {
                    currentUserTatitude  =(double) dataSnapshot.child(NodeNames.LATITUDE).getValue();
                }
                catch (Exception e) {
                    currentUserTatitude = 0;
                }
                _currentUserLatitude = currentUserTatitude;
                try {
                    currentUserLongitude = (double)dataSnapshot.child(NodeNames.LONGITUDE).getValue();
                }
                catch (Exception e) {
                    currentUserLongitude = 0 ;
                }
                _currentUserLongitude = currentUserLongitude;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "databaseError: UsersActivity currentPosition"+databaseError.getMessage());
            }
        });

        _databaseReference = FirebaseDatabase.getInstance().getReference().child(NodeNames.USERS);
       // orderByChild(NodeNames.NICKNAME)
        Query query =_databaseReference.orderByChild(NodeNames.USERID).limitToFirst(100);
        query.addValueEventListener(myListener = new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usersList.clear();
                String userId;
                String nickname = "";
                int age;
                boolean sex;
                double userLatitude;
                double userLongitude;
                boolean isUserOnline;
                long lastUserOnline;
                int newMessage;
                double userDistance;
                String photoName;

               for (DataSnapshot ds : snapshot.getChildren()){

                   userId = ds.getKey();
                   assert userId != null;
                   if(userId.equals(_currentUserId)){
                      continue;
                  }
                  if(ds.child(NodeNames.NICKNAME).getValue()!=null){

                      nickname = ds.child(NodeNames.NICKNAME).getValue().toString();

                   }
                   try {
                       age = Integer.parseInt(String.valueOf(ds.child(NodeNames.AGE).getValue()));
                   }

                   catch (Exception e) {
                       age = 18;
                   }
                   try {
                       sex = Boolean.parseBoolean(String.valueOf(ds.child(NodeNames.SEX).getValue()));
                   }
                   catch (Exception e) {
                       sex = true;
                   }
                   try {
                       userLatitude = Double.parseDouble(String.valueOf(ds.child(NodeNames.LATITUDE).getValue()));
                   }
                   catch (Exception e) {
                       userLatitude = 0;
                   }
                   try {
                       userLongitude = Double.parseDouble(String.valueOf(ds.child(NodeNames.LONGITUDE).getValue()));
                   }
                   catch (Exception e) {
                       userLongitude = 0;
                   }
                   try {
                       isUserOnline = Boolean.parseBoolean(String.valueOf(ds.child(NodeNames.USERONLINE).getValue()));
                   }
                   catch (Exception e) {
                       isUserOnline = false;
                   }

                   try {
                       lastUserOnline = Long.parseLong(String.valueOf(ds.child(NodeNames.LASTONLINE).getValue()));
                   }
                   catch (Exception e) {
                       lastUserOnline = 0;
                   }
                   try {
                       newMessage = Integer.parseInt(String.valueOf(ds.child(NodeNames.NEWMESSAGE).getValue()));
                       // newMessage = Integer.parseInt(String.valueOf(ds.child(NodeNames.NEWMESSAGE).child(_currentUserId).getValue()));
                   }
                   catch (Exception e) {
                       newMessage = 0;
                   }
                   try {
                     String Uri = String.valueOf(ds.child(NodeNames.PHOTO).getValue());
                       photoName = finduserUri(Uri);

                   }
                   catch (Exception e) {
                       photoName = null;
                   }
                       userDistance = calculateDistance(userLatitude, userLongitude);

                  if (userDistance/1000<= preferenceDistance) {

                              if (sex && preferenceSex == 0)
                              {
                                  Log.w(TAG, "new usersex 1 if user sex: "+sex + preferenceSex);
                                  User user = new User(nickname, age, sex, userId, userLatitude, userLongitude, isUserOnline, lastUserOnline, newMessage,photoName);
                                  user.setUserDistance(myRound(userDistance, 3));
                                  Log.w(TAG, "new user was added Distance" + myRound(userDistance, 3));
                                  user.setUserPhoto(photoName);
                                  Log.w(TAG, "new user was added Photo" + photoName);
                                  usersList.add(user);
                                  mAdapter.notifyDataSetChanged();

                              }
                              else if(!sex && preferenceSex == 1){
                                  Log.w(TAG, "new usersex 2 if user sex: "+sex + preferenceSex);
                                  User user = new User(nickname, age, sex, userId, userLatitude, userLongitude, isUserOnline, lastUserOnline, newMessage,photoName);
                                  user.setUserDistance(myRound(userDistance, 3));
                                  Log.w(TAG, "new user was added Distance" + myRound(userDistance, 3));
                                  user.setUserPhoto(photoName);
                                  Log.w(TAG, "new user was added Photo" + photoName);
                                  usersList.add(user);
                                  mAdapter.notifyDataSetChanged();
                              }
                             else if(preferenceSex == 2) {
                                  Log.w(TAG, "new usersex else user sex: "+sex + preferenceSex);
                                  User user = new User(nickname, age, sex, userId, userLatitude, userLongitude, isUserOnline, lastUserOnline, newMessage,photoName);
                                  user.setUserDistance(myRound(userDistance, 3));
                                  Log.w(TAG, "new user was added Distance" + myRound(userDistance, 3));
                                  user.setUserPhoto(photoName);
                                  Log.w(TAG, "new user was added Photo" + photoName);
                                  usersList.add(user);
                                  mAdapter.notifyDataSetChanged();
                              }
                              else {
                                  Log.w(TAG, " usersex not edded user sex: "+sex + preferenceSex);
                              }
                  }

               }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "databaseError: UsersActivity currentPosition"+error.getMessage());
            }
        });

    }


    private String finduserUri(String s){
        String[] words = s.split("images/");
        return  words[1];

    }

    static double myRound(double wert, int stellen) {
        BigDecimal b = new BigDecimal(wert);
        return  b.setScale(stellen,BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startService(new Intent(this, MyNavigationService.class));
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopService(new Intent(this, MyNavigationService.class));
       if (myListener != null) {
            _databaseReference.child(NodeNames.USERS).removeEventListener(myListener);
       }
    }

    @Override
    protected void onDestroy() {
        stopService(new Intent(this, MyNavigationService.class));
        super.onDestroy();
    }

    //true: km, false: mi
    public double calculateDistance(double latitude, double longitude) {
        float[] results = new float[1];
        Location.distanceBetween(latitude, longitude, _currentUserLatitude, _currentUserLongitude, results);
        return results[0];
    }

    @Override
    public void onUserItemClick(int clickedItemIndex) {

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Chat with user");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM, bundle);


        Intent intent = new Intent(this, ChatActivity.class);
        Bundle extras = new Bundle();

        String userId2 = usersList.get(clickedItemIndex).getUserId();
        String username = usersList.get(clickedItemIndex).getUserName();
        int userage = usersList.get(clickedItemIndex).getUserAge();
        boolean usersex = usersList.get(clickedItemIndex).getUserSex();

        extras.putString("USER_ID1", _currentUserId);
        extras.putString("USER_ID2", userId2);
        extras.putString("USER_NAME2", username);
        extras.putInt("USER_AGE2", userage);
        extras.putBoolean("USER_SEX2", usersex);
        intent.putExtras(extras);
        startActivity(intent);
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
      mAdapter.notifyDataSetChanged();
    }
}