package com.foster.softwares.callnotes.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Pranav on 26/07/16.
 */
public class CallNotesSQLliteHelper extends SQLiteOpenHelper {


    private static final String TAG = "CallNotesSQLliteHelper";

    public CallNotesSQLliteHelper(Context context) {
        super(context, CallNotesDBContract.CALL_NOTES_DB, null, CallNotesDBContract.DATBASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CallNotesDBContract.CALL_NOTES_CREATE_TABLE_QUERY);
        Log.i(TAG, "Create table called");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(oldVersion!=newVersion){
            db.execSQL("DROP TABLE IF EXISTS " + CallNotesDBContract.CALL_NOTES_TABLE);
            Log.i(TAG, "onUpgrade table called");
            onCreate(db);
        }
    }
}
