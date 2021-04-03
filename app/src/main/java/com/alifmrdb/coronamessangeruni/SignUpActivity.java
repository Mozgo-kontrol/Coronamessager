package com.alifmrdb.coronamessangeruni;


import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.alifmrdb.coronamessangeruni.common.NodeNames;
import com.alifmrdb.coronamessangeruni.common.User;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;



import java.util.Date;
import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {

    private final String  TAG = "SignUpActivity";
    private EditText _etEmail, _etName, _etPassword, _etConfirmPassword;
    private String _email, _name, _password, _confirmPassWord;
    private FirebaseUser _firebaseUser;
    private FirebaseAuth _firebaseAuth;
    private DatabaseReference _databaseReference;
    //analistic
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        //initial the Storage reference
        _firebaseAuth = FirebaseAuth.getInstance();
        _etEmail = findViewById(R.id.signUpMailField);
        _etName = findViewById(R.id.signUpNameField);
        _etPassword = findViewById(R.id.signUpPasswordField);
        _etConfirmPassword = findViewById(R.id.signUpPasswordConfirmField);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

    }

    public void btnSignupCklick(View v){
        _email =_etEmail.getText().toString();
        _name = _etName.getText().toString();
        _password =_etPassword.getText().toString();
        _confirmPassWord =_etConfirmPassword.getText().toString();

        if(_name.isEmpty()||_name.length()<6){
            _etName.setError(getString(R.string.please_enter_valid_name));
        }
        else if(_email.isEmpty()){
            _etEmail.setError(getString(R.string.please_enter_email));
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(_email).matches()){
            _etEmail.setError(getString(R.string.please_enter_correct_email));
        }
        else if(_password.isEmpty()){
            _etPassword.setError(getString(R.string.please_enter_password));
        }
        else if(_confirmPassWord.isEmpty()){
            _etPassword.setError(getString(R.string.please_enter_confirm_password));
        }
        else if (!isPasswordValid(_password)){
            _etPassword.setError(getString(R.string.password_must_be_longer));
        }
        else if (!_password.equals(_confirmPassWord)){
            _etConfirmPassword.setError(getString(R.string.please_enter_korrect_password));
        }
        else {
            createAccount(_email,_password,_name);

        }
    }

    /**
     *  A placeholder password validation check
     * */
        private boolean isPasswordValid(String password) {
            return password != null && password.trim().length() > 5;
        }

    /**
     *
     * Create new user bei Firebase
     * with valid email, name and password!
     * @param email String
     * @param password String
     * @param name String
     */

    private void createAccount(String email, String password, String name){

        _firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        _firebaseUser = _firebaseAuth.getCurrentUser();
                        if( _firebaseUser==null){

                            Log.w(TAG, getString(R.string.current_user));
                        }
                        Bundle bundle = new Bundle();
                        bundle.putString(FirebaseAnalytics.Param.METHOD, "createUserWithEmailAndPassword");
                        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SIGN_UP, bundle);
                        // update UI with the signed-in user's information
                        updateUserprofile(_firebaseUser,name);
                        Toast.makeText(this, getString(R.string.user_sign_up_successful),Toast.LENGTH_SHORT).show();
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.e(TAG, "User is not created : failure", task.getException());
                        Toast.makeText(SignUpActivity.this,getString(R.string.sign_up_failed,
                                task.getException()), Toast.LENGTH_SHORT).show();

                    }
                });
    }


    /**
     * Update user info bei Firebase
     * @param user FirebaseUser
     * @param name String
     *
     * */
    public void updateUserprofile(FirebaseUser user,String name) {

        //A Object to make an request for firebase
        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build();
        //update user name
        user.updateProfile(request).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                Log.w(TAG, getString(R.string.update_profile_success));
                //currentUser get the ID
                String userID = user.getUid();
                writeNewUser(name, userID);

            }
            else {

                Log.e(TAG, getString(R.string.failed_to_update_username,task.getException()));
                Toast.makeText(SignUpActivity.this
                        , getString(R.string.failed_to_update_username,task.getException()),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
    /**
     * write new tree of users info bei Firebase
     * @param name String
     * @param id String
     *
     * */
    private void writeNewUser(String name, String id) {

            User user = new User(name, 18, true, id, 0, 0, true, new Date().getTime(), 0, "null");
            _databaseReference = FirebaseDatabase.getInstance()
                    .getReference().child(NodeNames.USERS);
            _databaseReference.child(id).setValue(user).addOnCompleteListener(task -> {
                Log.w(TAG, getString(R.string.write_data_to_firebase));

                ProfileSettingActivity();
            });
    }

    private void ProfileSettingActivity(){

        Intent intent = new Intent(SignUpActivity.this, ProfileSettingActivity.class);
        startActivity(intent);
        finish();
    }
}