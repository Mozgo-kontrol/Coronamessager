package com.alifmrdb.coronamessangeruni;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.alifmrdb.coronamessangeruni.common.NodeNames;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

public class ProfileSettingActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 22;
    private final String TAG ="ProfileSettingActivity";
    private String _nickname;
    private Integer _age;
    private boolean _sex;
    private ImageView _ivProfile;
    private AlertDialog first_entry_of_user;
    private EditText _nicknameField;
    private EditText _ageTextField;
    private Button _saveBtn;
    private EditText _aboutMe;
    RadioGroup _radioGroup;
    RadioButton _sexMale;
    RadioButton _sexFemale;
    private Uri _localFileUri, _serverFileUri;
    //Firebase
    private FirebaseAuth _firebaseAuth;
    private FirebaseUser _firebaseUser;
    private DatabaseReference _databaseReference;
    // Reference for Storage
    private StorageReference _fileStorage;
    private String _userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profile_setting);
        _nicknameField = findViewById(R.id.nicknameField);
        _saveBtn = findViewById(R.id.saveButton);
        _ageTextField = findViewById(R.id.ageTextField);
        _ivProfile = findViewById(R.id.profileImageView);
        _radioGroup = findViewById(R.id.radioGroup);
        _sexFemale = findViewById(R.id.SexFemale);
        _sexMale = findViewById(R.id.SexMale);
        _aboutMe = findViewById(R.id.editTextAboutMe);
        //initial Firebase
        _firebaseAuth = FirebaseAuth.getInstance();
        _firebaseUser = _firebaseAuth.getCurrentUser();

        _userId = _firebaseUser.getUid();
        //initial the Storage reference
        _databaseReference = FirebaseDatabase.getInstance().getReference();
        //initial the Storage reference
        _fileStorage = FirebaseStorage.getInstance().getReference();

        if (_firebaseUser!=null) {   //upload the name from server
            //_etName.setText(_firebaseUser.getDisplayName());
            _serverFileUri = _firebaseUser.getPhotoUrl();

            //upload the Photo from server if user sign up with the photo
            if (_serverFileUri != null) {
                Glide.with(this).load(_serverFileUri)
                        .placeholder(R.drawable.default_profile)
                        .error(R.drawable.default_profile)
                        .into(_ivProfile);
            }
        }

        _databaseReference.child(NodeNames.USERS).child(_userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                try {
                    _nickname = String.valueOf(dataSnapshot.child(NodeNames.NICKNAME).getValue());
                    _nicknameField.setText(_nickname, TextView.BufferType.EDITABLE);
                }
                catch (Exception e) {
                    _nickname ="put your name";
                }
                //TO DO update user name to UI
                try {
                    _age = Integer.parseInt(String.valueOf(dataSnapshot.child(NodeNames.AGE)
                            .getValue()));
                    _ageTextField.setText(String.valueOf(_age), TextView.BufferType.EDITABLE);
                }
                catch (Exception e) {
                    _age =  18;
                }
                //TO DO update user Age  to UI
                try {
                    _sex = Boolean.parseBoolean(String.valueOf(dataSnapshot.child(NodeNames.SEX)
                            .getValue()));
                    if(_sex) {
                        _sexMale.setChecked(true);
                        _sexFemale.setChecked(false);
                    }
                    else  {
                        _sexMale.setChecked(false);
                        _sexFemale.setChecked(true);
                    }
                }
                catch (Exception e) {
                    _sexMale.setChecked(false);
                    _sexFemale.setChecked(true);
                }
                try{
                    _aboutMe.setText(String.valueOf(dataSnapshot.child(NodeNames.ABOUT_ME).getValue()), TextView.BufferType.EDITABLE);
                    }
                catch (Exception e){

                }
                //TO DO update user Sex from Firebase to UI
            }
                    @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "DatabaseError: ProfileSettingActivity onCreate");
            }
        });

        _saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name =_nicknameField.getText().toString();
                int userage = Integer.parseInt( _ageTextField.getText().toString());

                if (name.isEmpty()) {
                    _nicknameField.setError("Please enter name");
                }

                else if (name.length() <= 5) {
                    _nicknameField.setError("Name must be at least 6 character");
                }

                else if (_ageTextField.getText().toString().isEmpty()) {
                    _ageTextField.setError("Please, enter your age");
                }

                else if  (userage  < 18 &  userage  <= 99){
                    _ageTextField.setError("Please, enter valid age");
                }
                else {
                    storeValues();
                    Log.d(TAG, "Save button");
                    _nickname = name;
                    _age = userage;
                }
            }
        });

        _ivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //permission of user
                if(usePermission())
                {
                selectImage();
                }
            }
        });
    }
   /**
    * User sex Radio Group
    * */
    @SuppressLint("NonConstantResourceId")
    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();
        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.SexMale:
                if (checked)
                  _sex = true;
                    break;

            case R.id.SexFemale:
                if (checked)
                    _sex =false;
                    break;
        }
    }
    // Select Image method

    private void selectImage()
    {
        // Defining Implicit Intent to mobile gallery
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(
                Intent.createChooser(
                        intent,
                        "Select Image from here..."),
                PICK_IMAGE_REQUEST);
    }

    // Override onActivityResult method
    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    Intent data)
    {

        super.onActivityResult(requestCode,
                resultCode,
                data);

        // checking request code and result code
        // if request code is PICK_IMAGE_REQUEST and
        // resultCode is RESULT_OK
        // then set image in the image view
        if (requestCode == PICK_IMAGE_REQUEST
                && resultCode == RESULT_OK
                && data != null
                && data.getData() != null) {

            // Get the Uri of data
            _localFileUri = data.getData();
            try {

                // Setting image on image view using Bitmap
                Bitmap bitmap = MediaStore
                        .Images
                        .Media
                        .getBitmap(
                                getContentResolver(),
                                _localFileUri);

                _ivProfile.setImageBitmap(bitmap);
                uploadImage();
            }

            catch (IOException e) {
                // Log the exception
                e.printStackTrace();
            }
        }
    }

    // UploadImage method
    private void uploadImage() {

        if (_localFileUri != null) {

            // Code for showing progressDialog while uploading
            ProgressDialog progressDialog
                    = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            // Defining the child of storageReference
            StorageReference ref
                    = _fileStorage
                    .child(
                            "images/"
                                    + UUID.randomUUID().toString());

            // adding listeners on upload
            // or failure of image
            ref.putFile(_localFileUri)
                    .addOnSuccessListener(
                            new OnSuccessListener<UploadTask.TaskSnapshot>() {

                                @Override
                                public void onSuccess(
                                        UploadTask.TaskSnapshot taskSnapshot) {
                                    // Image uploaded bei Firebase, update Reference photo
                                   // -----------------------------------------------------------
                                    ref.getDownloadUrl().addOnSuccessListener(uri ->
                                            _serverFileUri = uri);
                                    // -----------------------------------------------------------
                                    // Image uploaded successfully
                                    // Dismiss dialog
                                    progressDialog.dismiss();
                                    Toast
                                            .makeText(ProfileSettingActivity.this,
                                                    "Image Uploaded!!",
                                                    Toast.LENGTH_SHORT)
                                            .show();
                                }
                            })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            // Error, Image not uploaded
                            progressDialog.dismiss();
                            Toast
                                    .makeText(ProfileSettingActivity.this,
                                            "Failed " + e.getMessage(),
                                            Toast.LENGTH_SHORT)
                                    .show();
                        }
                    })
                    .addOnProgressListener(
                            new OnProgressListener<UploadTask.TaskSnapshot>() {

                                // Progress Listener for loading
                                // percentage on the dialog box
                                @Override
                                public void onProgress(
                                        UploadTask.TaskSnapshot taskSnapshot) {
                                    double progress
                                            = (100.0
                                            * taskSnapshot.getBytesTransferred()
                                            / taskSnapshot.getTotalByteCount());
                                    progressDialog.setMessage(
                                            "Uploaded "
                                                    + (int) progress + "%");
                                }
                            });
        }
    }
    //------------------------------Permission-----------------------------------------------------
    /**
     * check user's permission
     *  for read from local Storage
     *
     * */
    private boolean usePermission(){

        String permission = Manifest.permission.READ_EXTERNAL_STORAGE;

        boolean result = false;
        result = ActivityCompat.checkSelfPermission(this, permission) == PackageManager
                .PERMISSION_GRANTED;
        if (result)
        {
            return result;
        }
        else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 102);
        }
        return result;
    }
    /**
     * check result user's permission
     *
     *
     * */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 102){
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
             //permissions was granted
            }
            else {
                Toast.makeText(this, R.string.access_permision_is_required,Toast.LENGTH_SHORT).show();
            }
        }

    }
    //--------------------------------Activity-for-result-----------------------------------------

    private void storeValues() {

        UserProfileChangeRequest request;
      if(_serverFileUri!=null) {
          request = new UserProfileChangeRequest.Builder()
                  .setDisplayName(_nicknameField.getText().toString().trim())
                  .setPhotoUri(_serverFileUri)
                  .build();
      }
      else {
          request = new UserProfileChangeRequest.Builder()
                  .setDisplayName(_nicknameField.getText().toString().trim())
                  .build();
      }

        _firebaseUser.updateProfile(request).addOnCompleteListener(task1 -> {
            if(task1.isSuccessful()){
                //User get the ID
                String userID =  _firebaseUser.getUid();
                //Create a tree in DataBase
                _databaseReference = FirebaseDatabase.getInstance()
                        .getReference().child(NodeNames.USERS);
                //Create a tree in DataBase
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put(NodeNames.NICKNAME, _nickname);
                hashMap.put(NodeNames.AGE,  _age);
                hashMap.put(NodeNames.SEX, _sex);
                hashMap.put(NodeNames.ABOUT_ME, _aboutMe.getText().toString());


                if(_serverFileUri!=null){
                    hashMap.put(NodeNames.PHOTO, _serverFileUri.getPath());
                }
                else {  hashMap.put(NodeNames.PHOTO, "null");}
                //with photo

                //if  User tree is created successfully
                _databaseReference.child(userID).updateChildren(hashMap).addOnCompleteListener(task2 -> {
                    Log.d(TAG, getString(R.string.user_profile_was_updated_succesful));

                    goToMainActivity();

                });

            }
            else {
                Toast.makeText(ProfileSettingActivity.this
                        , getString(R.string.failed_to_update_username,task1.getException()),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void goToMainActivity(){

        Intent intent = new Intent(ProfileSettingActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }


}