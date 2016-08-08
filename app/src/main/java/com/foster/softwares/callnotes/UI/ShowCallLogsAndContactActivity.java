package com.foster.softwares.callnotes.UI;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.Fade;
import android.transition.Slide;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.foster.softwares.callnotes.CallNotesApplication;
import com.foster.softwares.callnotes.R;
import com.foster.softwares.callnotes.adapter.CallLogsBaseAdapter;
import com.foster.softwares.callnotes.adapter.CallNotesListAdapter;
import com.foster.softwares.callnotes.adapter.ContactListViewAdapter;
import com.foster.softwares.callnotes.baseutils.Utils;
import com.foster.softwares.callnotes.database.CallNotesDBContract;
import com.foster.softwares.callnotes.database.CallNotesDataContentProvider;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;

/**
 * Created by Pranav on 26/07/16.
 */
public class ShowCallLogsAndContactActivity extends AppCompatActivity {

    private static final String TAG = "CallLogContactActivity";
    private int count = 0;

    ViewPager viewPager;
    private RecyclerView contact_list_rv;
    ArrayList<String> list = new ArrayList<String>();
    private Toolbar mToolbar;
    private MenuItem mSearchAction;
    private boolean isSearchOpened = false;
    private EditText edtSeach;
    private Tracker mTracker;
    private ImageButton canel_button, back_button;
    private TextView contact_list_tv, call_logs_tv;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_calllogs_and_contact_layout);


        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        contact_list_tv = (TextView) findViewById(R.id.contact_list_tv);
        call_logs_tv = (TextView) findViewById(R.id.call_logs_tv);
        setSupportActionBar(mToolbar);

        CallNotesApplication application = (CallNotesApplication) getApplication();
        mTracker = application.getDefaultTracker();
        mTracker.setScreenName("Image~" + TAG);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        list.add("a");
        list.add("b");

        viewPager = (ViewPager) findViewById(R.id.logs_view_pager);

        CallLogsBaseAdapter callLogsBaseAdapter = new CallLogsBaseAdapter(this, list, "");
        viewPager.setAdapter(callLogsBaseAdapter);

        selectCallLogs();

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 0:
                        selectCallLogs();
                        break;

                    case 1:
                        selectContactList();
                        break;


                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        contact_list_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectContactList();
                viewPager.setCurrentItem(1);
            }
        });

        call_logs_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectCallLogs();
                viewPager.setCurrentItem(0);
            }
        });

    }


    private void selectContactList() {
        contact_list_tv.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.white));
        call_logs_tv.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.seventy_white));
        contact_list_tv.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.black));
        call_logs_tv.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.black_seventy_background));
        call_logs_tv.setTextSize(13.0f);
        contact_list_tv.setTextSize(18.0f);
    }

    private void selectCallLogs() {
        contact_list_tv.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.seventy_white));
        call_logs_tv.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.white));
        contact_list_tv.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.black_seventy_background));
        call_logs_tv.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.black));
        call_logs_tv.setTextSize(18.0f);
        contact_list_tv.setTextSize(13.0f);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_option, menu);

        MenuItem searchItem = menu.findItem(R.id.search);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id){

            case R.id.search:
                handleMenuSearch();
                break;
        }
        return super.onOptionsItemSelected(item);


    }

    private void handleMenuSearch() {

        ActionBar action = getSupportActionBar(); //get the actionbar

        if(isSearchOpened){ //test if the search is open

            action.setDisplayShowCustomEnabled(false); //disable a custom view inside the actionbar
            action.setDisplayShowTitleEnabled(true); //show the title in the action bar

            //hides the keyboard
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(edtSeach.getWindowToken(), 0);

            //add the search icon in the action bar
            mSearchAction.setIcon(getResources().getDrawable(R.mipmap.ic_search_white_24dp));
            mSearchAction.setVisible(true);

            isSearchOpened = false;
        } else { //open the search entry

            mSearchAction.setVisible(false);
            action.setDisplayShowCustomEnabled(true); //enable it to display a
            // custom view in the action bar.
            action.setCustomView(R.layout.search_bar);//add the custom view
            action.setDisplayShowTitleEnabled(false); //hide the title

            edtSeach = (EditText)action.getCustomView().findViewById(R.id.edtSearch); //the text editor
            back_button = (ImageButton)action.getCustomView().findViewById(R.id.back_button);
            canel_button = (ImageButton) action.getCustomView().findViewById(R.id.canel_button);

            canel_button.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {

                    View view = getCurrentFocus();
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                    onBackPressed();

                }
            });

            back_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    View view = getCurrentFocus();
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                    onBackPressed();
                }
            });

            //this is a listener to do a search when the user clicks on search button
            edtSeach.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        Log.i(TAG, "v : " + v.getText());
                        return true;
                    }
                    return false;
                }
            });

            edtSeach.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    Log.i(TAG, "onTextChanged : " + s);
                    dosearch(s);
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });


            edtSeach.requestFocus();

            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(edtSeach, InputMethodManager.SHOW_IMPLICIT);

            isSearchOpened = true;
        }

    }

    private void dosearch(CharSequence s) {

        CallLogsBaseAdapter callLogsBaseAdapter = new CallLogsBaseAdapter(this, list, s.toString());
        viewPager.setAdapter(callLogsBaseAdapter);


    }

    @Override
    public void onBackPressed() {

        if(isSearchOpened) {
            handleMenuSearch();
            return;
        }
        super.onBackPressed();

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        mSearchAction = menu.findItem(R.id.search);
        return super.onPrepareOptionsMenu(menu);
    }
}

