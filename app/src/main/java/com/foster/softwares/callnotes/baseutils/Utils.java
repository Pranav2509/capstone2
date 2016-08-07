package com.foster.softwares.callnotes.baseutils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Pranav on 26/07/16.
 */
public class Utils {

    public static final String DATE_TIME_FORMAT = "dd/MM/yyyy HH:mm:ss";
    public static final SimpleDateFormat sdf = new SimpleDateFormat(Utils.DATE_TIME_FORMAT);
    public static final String CallLogs = "CallLogs";
    public static final String Contacts = "Contacts";
    public static int THREE_DAYS_TIME_IN_MILLIS = 3*24*60*60*100;


    public static int getData(String key, int defaultValue, Context context){
        SharedPreferences preferences = getPreferencesInstance(null, context);
        return  preferences.getInt(key, defaultValue);
    }

    public static void saveData(String key, int value, Context context){
        SharedPreferences preferences = getPreferencesInstance(null, context);
        preferences.edit().putInt(key, value);
    }

    public static SharedPreferences getPreferencesInstance(SharedPreferences preferences, Context context) {

        if (preferences == null) {
            return PreferenceManager.getDefaultSharedPreferences(context);
        }
        return preferences;
    }
}
