package com.foster.softwares.callnotes.UI;

import android.app.LoaderManager;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.foster.softwares.callnotes.CallNotesApplication;
import com.foster.softwares.callnotes.ContentOberserCallBack;
import com.foster.softwares.callnotes.R;
import com.foster.softwares.callnotes.adapter.CallNotesListAdapter;
import com.foster.softwares.callnotes.baseutils.Utils;
import com.foster.softwares.callnotes.database.CallNotesDBContract;
import com.foster.softwares.callnotes.database.CallNotesDataContentProvider;
import com.foster.softwares.callnotes.receiver.CallNotesWidgetProvider;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        LoaderManager.LoaderCallbacks<Cursor>{

    String currentDateandTime = Utils.sdf.format(new Date());
    final String TAG = "MainActivity";
    FloatingActionButton add_new_note;
    RecyclerView list_rv;
    Cursor c;
    CallNotesListAdapter callNotesListAdapter;
    private MenuItem mSearchAction;
    private boolean isSearchOpened = false;
    private EditText edtSeach;
    private ImageButton back_button, canel_button;

    private Toolbar mToolbar;
    private Tracker mTracker;
    private final int URL_LOADER = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MobileAds.initialize(getApplicationContext(), getString(R.string.banner_ad_unit_id));

        list_rv = (RecyclerView) findViewById(R.id.list_rv);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);

        CallNotesApplication application = (CallNotesApplication) getApplication();
        mTracker = application.getDefaultTracker();
        mTracker.setScreenName("Image~" + TAG);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        getLoaderManager().initLoader(URL_LOADER, null, this);

        add_new_note = (FloatingActionButton) findViewById(R.id.add_new_note);
        add_new_note.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        Intent in = new Intent(MainActivity.this, AddCallNoteActivity.class);
        startActivity(in);

    }

    @Override
    protected void onResume() {
        super.onResume();

        refreshList(0);
        CallNotesWidgetProvider.sendRefreshBroadcast(getBaseContext());
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public void refreshList(int position){

        c = getContentResolver().query(CallNotesDataContentProvider.CALL_NOTES_URI, null, null, null,
                CallNotesDBContract.CALL_NOTES_COL8 + " DESC");
        if(list_rv!=null){
            callNotesListAdapter = new CallNotesListAdapter(getBaseContext(), c, this);
            list_rv.setLayoutManager(new LinearLayoutManager(this));
            list_rv.setAdapter(callNotesListAdapter);
        }
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

            action.setDisplayShowCustomEnabled(true); //enable it to display a
            // custom view in the action bar.

            View v = LayoutInflater.from(getBaseContext()).inflate(R.layout.search_bar, null);
            mSearchAction.setVisible(false);


            action.setCustomView(v);//add the custom view
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

            //open the keyboard focused in the edtSearch
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(edtSeach, InputMethodManager.SHOW_IMPLICIT);
            isSearchOpened = true;
        }

    }

    private void dosearch(CharSequence s) {

        String selection = CallNotesDBContract.CALL_NOTES_COL4 + " LIKE '%" + s + "%' OR "
                + CallNotesDBContract.CALL_NOTES_COL2+ " LIKE '%" + s + "%'";

        c = getContentResolver().query(CallNotesDataContentProvider.CALL_NOTES_URI, null, selection, null,
                CallNotesDBContract.CALL_NOTES_COL8 + " DESC");
        if(list_rv!=null){
            callNotesListAdapter = new CallNotesListAdapter(getBaseContext(), c, this);
            list_rv.setLayoutManager(new LinearLayoutManager(this));
            list_rv.setAdapter(callNotesListAdapter);
        }


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

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {


        switch (id) {
            case URL_LOADER:
                // Returns a new CursorLoader
                return new CursorLoader(
                        this,   // Parent activity context
                        CallNotesDataContentProvider.CALL_NOTES_URI,        // Table to query
                        null,     // Projection to return
                        null,            // No selection clause
                        null,            // No selection arguments
                        CallNotesDBContract.CALL_NOTES_COL8 + " DESC"             // Default sort order
                );
            default:
                // An invalid id was passed in
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(list_rv!=null){
            callNotesListAdapter = new CallNotesListAdapter(getBaseContext(), data, this);
            list_rv.setLayoutManager(new LinearLayoutManager(this));
            list_rv.setAdapter(callNotesListAdapter);
            CallNotesWidgetProvider.sendRefreshBroadcast(getBaseContext());
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
