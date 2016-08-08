package com.foster.softwares.callnotes.service;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.foster.softwares.callnotes.CallNoteDomain;
import com.foster.softwares.callnotes.R;
import com.foster.softwares.callnotes.UI.MainActivity;
import com.foster.softwares.callnotes.database.CallNotesDBContract;
import com.foster.softwares.callnotes.database.CallNotesDataContentProvider;
import com.foster.softwares.callnotes.receiver.CallNotesWidgetProvider;

import java.util.ArrayList;

/**
 * Created by Pranav on 30/07/16.
 */
public class CallNoteWidgetRemoteViewFactory implements RemoteViewsService.RemoteViewsFactory {

    Context mContext;
    Intent mIntent;
    ArrayList<CallNoteDomain> callNoteDomainArrayList = new ArrayList<CallNoteDomain>();
    CallNoteDomain callNoteDomain;
    private String TAG = "ViewFactory";

    public CallNoteWidgetRemoteViewFactory(Context context, Intent intent) {

        mContext = context;
        mIntent = intent;

    }

    @Override
    public void onCreate() {

        Log.i(TAG, "oncreate");

        Cursor c = mContext.getContentResolver().query(CallNotesDataContentProvider.CALL_NOTES_URI,
                null, null, null, CallNotesDBContract.CALL_NOTES_COL8 + " DESC ");

        callNoteDomainArrayList.clear();
        while (c.moveToNext()){
            callNoteDomain = new CallNoteDomain();
            callNoteDomain.setId(c.getString(c.getColumnIndex(CallNotesDBContract.CALL_NOTES_COL1)));
            callNoteDomain.setName(c.getString(c.getColumnIndex(CallNotesDBContract.CALL_NOTES_COL2)));
            callNoteDomain.setNumber(c.getString(c.getColumnIndex(CallNotesDBContract.CALL_NOTES_COL3)));
            callNoteDomain.setNote(c.getString(c.getColumnIndex(CallNotesDBContract.CALL_NOTES_COL4)));

            callNoteDomainArrayList.add(callNoteDomain);
        }

    }

    @Override
    public void onDataSetChanged() {
        Log.i(TAG, "onDataSetChanged");

        Cursor c;

        final long token = Binder.clearCallingIdentity();
        try {
            c = mContext.getContentResolver().query(CallNotesDataContentProvider.CALL_NOTES_URI,
                    null, null, null, CallNotesDBContract.CALL_NOTES_COL8 + " DESC ");
        } finally {
            Binder.restoreCallingIdentity(token);
        }



        callNoteDomainArrayList.clear();
        while (c.moveToNext()){
            callNoteDomain = new CallNoteDomain();
            callNoteDomain.setId(c.getString(c.getColumnIndex(CallNotesDBContract.CALL_NOTES_COL1)));
            callNoteDomain.setName(c.getString(c.getColumnIndex(CallNotesDBContract.CALL_NOTES_COL2)));
            callNoteDomain.setNumber(c.getString(c.getColumnIndex(CallNotesDBContract.CALL_NOTES_COL3)));
            callNoteDomain.setNote(c.getString(c.getColumnIndex(CallNotesDBContract.CALL_NOTES_COL4)));
            callNoteDomainArrayList.add(callNoteDomain);
        }

    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy");


    }

    @Override
    public int getCount() {
        Log.i(TAG, "getCount");
        return callNoteDomainArrayList.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        Log.i(TAG, "getViewAt");


        RemoteViews view = new RemoteViews(mContext.getPackageName(), R.layout.widget_layout_single);

        view.setTextViewText(R.id.name, callNoteDomainArrayList.get(position).getName());

        view.setTextViewText(R.id.note, callNoteDomainArrayList.get(position).getNote());

        Intent fillInIntent = new Intent();
        fillInIntent.putExtra("name",callNoteDomainArrayList.get(position).getName());
        fillInIntent.putExtra("number",callNoteDomainArrayList.get(position).getNumber());
        fillInIntent.putExtra("note",callNoteDomainArrayList.get(position).getNote());
        fillInIntent.putExtra("id",callNoteDomainArrayList.get(position).getId());

        view.setOnClickFillInIntent(R.id.base_ll, fillInIntent);

        return view;
    }

    @Override
    public RemoteViews getLoadingView() {
        Log.i(TAG, "getLoadingView");

        return null;
    }

    @Override
    public int getViewTypeCount() {
        Log.i(TAG, "getViewTypeCount");
        return 1;
    }

    @Override
    public long getItemId(int position) {
        Log.i(TAG, "getItemId");
        return position;
    }

    @Override
    public boolean hasStableIds() {
        Log.i(TAG, "hasStableIds");
        return false;
    }
}
