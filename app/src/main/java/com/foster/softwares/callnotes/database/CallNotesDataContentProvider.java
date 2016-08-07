package com.foster.softwares.callnotes.database;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by Pranav on 26/07/16.
 */
public class CallNotesDataContentProvider extends ContentProvider {

    public static final String PROVIDER_NAME = "com.foster.softwares.callnotes.database";
    public static final String CALL_NOTES_CONTENT = "content://"+ PROVIDER_NAME+"/call_notes";

    public static final Uri CALL_NOTES_URI = Uri.parse(CALL_NOTES_CONTENT);
    private static final String TAG = "CallContentProvider";

    Context mContext;
    CallNotesSQLliteHelper callNotesSQLliteHelper;
    SQLiteDatabase sqLiteDatabase;



    @Override
    public boolean onCreate() {

        mContext = getContext();
        callNotesSQLliteHelper = new CallNotesSQLliteHelper(mContext);
        sqLiteDatabase = callNotesSQLliteHelper.getWritableDatabase();


        return sqLiteDatabase==null ? false : true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        Cursor cursor = sqLiteDatabase.query(CallNotesDBContract.CALL_NOTES_TABLE,
                projection, selection, selectionArgs, null, null, sortOrder);
        getContext().getContentResolver().notifyChange(CALL_NOTES_URI, null);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {

        long id = 0L;

        id = sqLiteDatabase.insert(CallNotesDBContract.CALL_NOTES_TABLE, null, values);
        getContext().getContentResolver().notifyChange(CALL_NOTES_URI, null);
        Log.i(TAG, "inserted in table"+ id);

        return Uri.parse(CALL_NOTES_URI + "/" + id);

    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        Log.i(TAG, selection);
        sqLiteDatabase.delete(CallNotesDBContract.CALL_NOTES_TABLE, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(CALL_NOTES_URI, null);
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {



        sqLiteDatabase.update(CallNotesDBContract.CALL_NOTES_TABLE, values, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(CALL_NOTES_URI, null);
        Log.i(TAG, "updated in table");
        return 0;
    }
}
