package com.alifmrdb.coronamessangeruni;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.alifmrdb.coronamessangeruni.common.NodeNames;


public class SearchSettingActivity extends AppCompatActivity {

    private static final String TAG = "SearchSettingActivity";
    private SharedPreferences searchPreferences;
    private SharedPreferences.Editor editor;

    private RadioGroup _radioGroup;
    private RadioButton _searchMen;
    private RadioButton _searchWomen;
    private RadioButton _searchMenWomen;
    private SeekBar _distance;
    private TextView _distanceNumber;
    private Button _saveChanges;


    private int preferenceDistance = 2000;
    private int preferenceAge_min = 18;
    private int  preferenceAge_max=99;
    private int preferenceSex = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_setting);
        _radioGroup = findViewById(R.id.radioGroup2);
        _searchMen = findViewById(R.id.searchMen);
        _searchWomen = findViewById(R.id.searchWomen);
        _searchMenWomen = findViewById(R.id.searchMenWomen);
        _distance = findViewById(R.id.seekBarDistance);
        _distanceNumber = findViewById(R.id.textView_distance);
        _saveChanges = findViewById(R.id.save_changes);


        searchPreferences = getSharedPreferences(NodeNames.SEARCH_PREFERENCES, Context.MODE_PRIVATE);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            System.out.println("David");
            _distance.setProgress(searchPreferences.getInt(NodeNames.DISTANCE_PREFERENCES, 10), false);
        }

        int pref =  searchPreferences.getInt(NodeNames.SEX_PREFERENCES, 2);
        if(pref == 0){
            _searchMen.setChecked(true);
        }else if(pref == 1){
            _searchWomen.setChecked(true);
        }else {
            _searchMenWomen.setChecked(true);
        }
        _distance.setMax(NodeNames.MAX_SEARCH_DISTANCE);

        int distPref = searchPreferences.getInt(NodeNames.DISTANCE_PREFERENCES, 20);

        _distance.setProgress(distPref);
        _distanceNumber.setText(_distance.getProgress() + " km");
        _distance.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                _distanceNumber.setText(progress+" km");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


    }

    public void saveSearchSettings(View view){
        editor = searchPreferences.edit();
        editor.putInt(NodeNames.DISTANCE_PREFERENCES, _distance.getProgress());
        int pref;
        if(_searchMen.isChecked()){
            pref = 0;
        }else if(_searchWomen.isChecked()){
            pref = 1;
        }else {
            pref = 2;
        }
        editor.putInt(NodeNames.SEX_PREFERENCES, pref);

        editor.apply();

        Intent intent = new Intent(SearchSettingActivity.this, MainActivity.class);
        startActivity(intent);
        finish();

    }
}