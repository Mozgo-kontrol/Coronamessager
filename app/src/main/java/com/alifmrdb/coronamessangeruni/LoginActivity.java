package com.alifmrdb.coronamessangeruni;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;

import org.w3c.dom.Text;

public class LoginActivity extends AppCompatActivity {

    private final String TAG = "LoginActivity";
    private FirebaseAuth _mAuth;
    private Button signUpButton;
    private Button loginButton;
    private EditText inputEmail;
    private EditText inputPassword;

    private FirebaseAnalytics mFirebaseAnalytics;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        signUpButton = findViewById(R.id.signUpButton);
        loginButton = findViewById(R.id.loginButton);
        inputEmail = findViewById(R.id.editTextTextEmailAddress);
        inputPassword = findViewById(R.id.editTextTextPassword);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Button signUpButton = findViewById(R.id.signUpButton);




        signUpButton.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
            finish();
        });

        loginButton.setOnClickListener(v -> {




            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.METHOD, "signInWithEmailAndPassword");
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, bundle);

            String _email = inputEmail.getText().toString();
            String _password = inputPassword.getText().toString();
            if (isValid(_email, _password)) {
                //--------------------------------------------
                Bundle bundle1 = new Bundle();
                bundle1.putString(FirebaseAnalytics.Param.METHOD, "signInWithEmailAndPassword");
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, bundle1);
               //--------------------------------------------
                signInWithEmailAndPassword(_email, _password);
            } else {
                AlertDialog delete_entry = new AlertDialog.Builder(LoginActivity.this).setTitle("Error").setMessage("Your email or password is not valid.")


                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Continue with delete operation
                            }
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }




        });

        this.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }


    private boolean isValid(String _email, String _password) {
        if (Patterns.EMAIL_ADDRESS.matcher(_email).matches()) {
            System.out.println("_email");
            Log.d(TAG, _email);
        }
        return _password.length() >= 6 && Patterns.EMAIL_ADDRESS.matcher(_email).matches();
    }


    /** signIn method
     *  which takes in an email address and password, validates them,
     *  and then signs a user in with the signInWithEmailAndPassword method.
     */
    private void signInWithEmailAndPassword(String email, String password) {
        _mAuth = FirebaseAuth.getInstance();
        _mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {

                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, getString(R.string.sign_in_success));
                        goToMainActivity();

                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, getString(R.string.login_with_email_failure), task.getException());

                        AlertDialog delete_entry = new AlertDialog.Builder(LoginActivity.this).setTitle("Error").setMessage("Your email or password is wrong.")

                                // Specifying a listener allows you to take an action before dismissing the dialog.
                                // The dialog is automatically dismissed when a dialog button is clicked.
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Continue with delete operation
                                    }
                                })

                                // A null listener allows the button to dismiss the dialog and take no further action.
                                .setNegativeButton(android.R.string.no, null)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();

                    }
                });

    }
    private void goToMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}

