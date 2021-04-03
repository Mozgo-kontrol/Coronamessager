package com.alifmrdb.coronamessangeruni;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alifmrdb.coronamessangeruni.common.MApplication;
import com.alifmrdb.coronamessangeruni.common.NodeNames;
import com.alifmrdb.coronamessangeruni.services.MyNavigationService;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import static com.google.firebase.database.DatabaseReference.goOffline;
import static com.google.firebase.database.DatabaseReference.goOnline;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private FirebaseAuth _firebaseAuth;
    private DatabaseReference _databaseReference;
    private FirebaseUser _currentUserAuth;
    //Start search button
    private static final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 11;

    boolean FromChildActivity = false;
    private TextView _tvNickName;
    private ImageView _ivProfile;
    private Uri _serverFileUri;
    private TextView _textAboutMe;

    //References
    SharedPreferences firstTimeStartPreferences;
    private SharedPreferences.Editor editor;
    public static final String FIRST_TIME_START_PREFERENCES = "firstTimeStart";

    private SharedPreferences _SharedPreferences;
    //analistic
    private FirebaseAnalytics mFirebaseAnalytics;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        _ivProfile = findViewById(R.id.mainProfileImage);
        _tvNickName=findViewById(R.id.tv_nickname_main);
        _textAboutMe = findViewById(R.id.textViewAboutMe);
        _SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        firstTimeStartPreferences = getSharedPreferences(FIRST_TIME_START_PREFERENCES,
                Context.MODE_PRIVATE);

        editor = firstTimeStartPreferences.edit();

        // Initialize Firebase Auth
        _firebaseAuth = FirebaseAuth.getInstance();
        _currentUserAuth = _firebaseAuth.getCurrentUser();
        _databaseReference = FirebaseDatabase.getInstance().getReference();

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        //if user was not initialized go to LoginActivity
        if (  _currentUserAuth == null)
        {
            // Not signed in, launch the Sign In activity
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }


        if (_currentUserAuth!=null) {   //upload the name from server
            _serverFileUri = _currentUserAuth.getPhotoUrl();
            _tvNickName.setText(_currentUserAuth.getDisplayName());
            //upload the Photo from server if user sign up with the photo
            if (_serverFileUri != null) {
                Glide.with(this).load(_serverFileUri)
                        .placeholder(R.drawable.default_profile)
                        .error(R.drawable.default_profile)
                        .into(_ivProfile);
            }
        }

        if (_currentUserAuth != null) {
            _databaseReference.child(NodeNames.USERS).child(_currentUserAuth.getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                            try{
                                _textAboutMe.setText(String.valueOf(dataSnapshot.child(NodeNames.ABOUT_ME).getValue()), TextView.BufferType.EDITABLE);
                            }
                            catch (Exception e){

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }


        if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_FINE_LOCATION);

            Toast.makeText(MainActivity.this, getString(R.string.make_permission), Toast.LENGTH_SHORT).show();
        }

        startService(new Intent(this, MyNavigationService.class));
        MApplication.setOnlineListener(_currentUserAuth);
    }


    public void btnSearch(View view) {
        //analytic
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.METHOD, "btnSearsh");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SEARCH, bundle);
        usersActivity();
    }


    private void usersActivity() {
        Intent intent = new Intent(this, UsersActivity.class);
        Bundle extras = new Bundle();
        intent.putExtras(extras);
        startActivity(intent);

    }

    @Override
    protected void onResume() {
        super.onResume();
        startService(new Intent(this, MyNavigationService.class));

    }

    @Override
    protected void onPause() {
        super.onPause();
        stopService(new Intent(this, MyNavigationService.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        goOffline();
        stopService(new Intent(this, MyNavigationService.class));
    }
    /***
     * Toolbar menu initialisation
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }
    /***
     * Toolbar menu with many options
     *
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.menu_sign_out) {
            _databaseReference.child(NodeNames.USERS).child(_firebaseAuth.getCurrentUser().getUid()).child(NodeNames.USERONLINE).setValue(false);
            _databaseReference.child(NodeNames.USERS).child(_firebaseAuth.getCurrentUser().getUid()).child(NodeNames.LASTONLINE).setValue(ServerValue.TIMESTAMP);
           _firebaseAuth.signOut();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
        if (item.getItemId() == R.id.menu_settings){
            Intent intent = new Intent(this, ProfileSettingActivity.class);
            startActivity(intent);
        }

        if (item.getItemId() == R.id.menu_search) {
            Intent intent = new Intent(this, SearchSettingActivity.class);
            startActivity(intent);
        }
        if (item.getItemId() == R.id.menu_delete_account) {

            Toast.makeText(MainActivity.this, "TO DO",
                    Toast.LENGTH_SHORT).show();
        }
        return true;
    }
//-----------------------------------------------------------------------------------------


   @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

       if (requestCode == MY_PERMISSIONS_REQUEST_FINE_LOCATION) {
           if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
               Toast.makeText(MainActivity.this, "The permission FINE_LOCATION is granted",
                       Toast.LENGTH_SHORT).show();
           } else {
               Toast.makeText(MainActivity.this, "make a permission",
                       Toast.LENGTH_SHORT).show();
           }
       }
    }

//-------------------------------------------------------------------------------------------------

}