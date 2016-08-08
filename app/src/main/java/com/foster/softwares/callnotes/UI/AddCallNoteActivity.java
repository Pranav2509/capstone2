package com.foster.softwares.callnotes.UI;

import android.Manifest;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.foster.softwares.callnotes.CallNotesApplication;
import com.foster.softwares.callnotes.R;
import com.foster.softwares.callnotes.baseutils.Utils;
import com.foster.softwares.callnotes.database.CallNotesDBContract;
import com.foster.softwares.callnotes.database.CallNotesDataContentProvider;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Pranav on 26/07/16.
 */
public class AddCallNoteActivity extends AppCompatActivity implements View.OnClickListener{

    private static final int REQUEST_RESULT_FOR_PERMISSION = 1;
    ImageView save_note_iv, delete_note_iv;
    Button select_contact_button;
    private int REQUEST_CODE = 111;

    private String mNumber;
    private String mName;
    private String TAG = "AddCallNoteActivity";
    private String mNote;
    private EditText note_et;

    String currentDateandTime = Utils.sdf.format(new Date());
    private Tracker mTracker;
    private String inId;
    private EditText name_number_tv;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_call_note_layout);

        save_note_iv = (ImageView) findViewById(R.id.save_note_iv);
        save_note_iv.setOnClickListener(this);
        delete_note_iv = (ImageView) findViewById(R.id.delete_note_iv);
        delete_note_iv.setOnClickListener(this);
        select_contact_button = (Button) findViewById(R.id.select_contact_details);
        select_contact_button.setOnClickListener(this);
        name_number_tv = (EditText) findViewById(R.id.name_number_tv);

        if ((ActivityCompat.checkSelfPermission(getBaseContext(),
                Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED)
                || (ActivityCompat.checkSelfPermission(getBaseContext(),
                Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED)) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CALL_LOG, Manifest.permission.READ_CONTACTS},
                    REQUEST_RESULT_FOR_PERMISSION);
        }else{
            name_number_tv.setVisibility(View.GONE);
            select_contact_button.setVisibility(View.VISIBLE);
        }


        note_et = (EditText) findViewById(R.id.note_et);
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        Intent in = getIntent();

        mName = in.hasExtra("name") ? in.getStringExtra("name") : "";
        mNumber = in.hasExtra("number") ? in.getStringExtra("number") : "";
        mNote = in.hasExtra("note") ? in.getStringExtra("note") : "";
        inId = in.hasExtra("id") ? in.getStringExtra("id") : "0";

        if(!mName.equals("")) {
            if (mName.equals("Unknown")) {
                select_contact_button.setText(mNumber);
            } else {
                select_contact_button.setText(mName);
            }
        }

        if(!mNote.equals("")){
            note_et.setText(mNote);
        }
        CallNotesApplication application = (CallNotesApplication) getApplication();
        mTracker = application.getDefaultTracker();
        mTracker.setScreenName("Image~" + TAG);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CODE && data!=null){
            mName = data.hasExtra("name") ? data.getStringExtra("name") : null;
            mNumber = data.hasExtra("number") ? data.getStringExtra("number") : null;
            select_contact_button.setText(mName);
        }
    }

    @Override
    public void onClick(View v) {


        if(name_number_tv.getVisibility() == View.VISIBLE){
            mName = name_number_tv.getText().toString();
            mNumber = name_number_tv.getText().toString();
        }


        switch (v.getId()){

            case R.id.select_contact_details :
                Intent in = new Intent(AddCallNoteActivity.this, ShowCallLogsAndContactActivity.class);



                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {

                    ActivityOptionsCompat transitionActivityOptions =
                            ActivityOptionsCompat.makeSceneTransitionAnimation(this);

                    startActivityForResult(in, REQUEST_CODE,transitionActivityOptions.toBundle());

                }else{
                    startActivityForResult(in, REQUEST_CODE);
                }

                break;

            case R.id.delete_note_iv :

                Toast.makeText(getBaseContext(), getString(R.string.note_not_saved), Toast.LENGTH_LONG).show();
                finish();
                break;

            case R.id.save_note_iv :

                if(name_number_tv.getVisibility() == View.VISIBLE) {
                    if(name_number_tv.getText().toString().trim().equals("") ||
                            name_number_tv.getText().toString().trim().length() == 0){
                        Toast.makeText(getBaseContext(), getString(R.string.please_enter_contact_details), Toast.LENGTH_LONG).show();
                        return;
                    }

                }else{
                    if (select_contact_button.getText().toString().equals(getString(R.string.add_contact_details))) {
                        Toast.makeText(getBaseContext(), getString(R.string.please_enter_contact_details), Toast.LENGTH_LONG).show();
                        return;
                    }
                }

                if(note_et.getText().toString().trim().equals("") || note_et.getText().toString().trim().length() == 0){
                    Toast.makeText(getBaseContext(), getString(R.string.please_enter_a_note), Toast.LENGTH_LONG).show();
                    return;
                }



                ContentValues contentValues = new ContentValues();

                if(inId.equals("0")) {
                    contentValues.put(CallNotesDBContract.CALL_NOTES_COL2, mName);
                    contentValues.put(CallNotesDBContract.CALL_NOTES_COL3, mNumber);
                    contentValues.put(CallNotesDBContract.CALL_NOTES_COL4, note_et.getText().toString());
                    contentValues.put(CallNotesDBContract.CALL_NOTES_COL6, "Foster Softwares");
                    contentValues.put(CallNotesDBContract.CALL_NOTES_COL7, "");
                    contentValues.put(CallNotesDBContract.CALL_NOTES_COL8, currentDateandTime);
                    getContentResolver().insert(CallNotesDataContentProvider.CALL_NOTES_URI, contentValues);
                    Toast.makeText(getBaseContext(), getString(R.string.note_Saved), Toast.LENGTH_LONG).show();
                    finish();
                }else{

                    String []ids = {inId};

                    contentValues.put(CallNotesDBContract.CALL_NOTES_COL2, mName);
                    contentValues.put(CallNotesDBContract.CALL_NOTES_COL3, mNumber);
                    contentValues.put(CallNotesDBContract.CALL_NOTES_COL4, note_et.getText().toString());
                    getContentResolver().update(CallNotesDataContentProvider.CALL_NOTES_URI,
                            contentValues, CallNotesDBContract.CALL_NOTES_COL1 + "=?", ids);
                    Toast.makeText(getBaseContext(), getString(R.string.note_updated), Toast.LENGTH_LONG).show();
                    finish();
                }
                break;

        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {

            case REQUEST_RESULT_FOR_PERMISSION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                    name_number_tv.setVisibility(View.GONE);
                    select_contact_button.setVisibility(View.VISIBLE);

                } else {
                    name_number_tv.setVisibility(View.VISIBLE);
                    select_contact_button.setVisibility(View.GONE);
                }
            }
        }
    }
}
