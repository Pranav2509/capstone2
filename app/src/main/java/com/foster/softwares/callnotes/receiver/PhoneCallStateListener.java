package com.foster.softwares.callnotes.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.support.v4.app.NotificationCompat;
import android.telephony.CellInfo;
import android.telephony.CellLocation;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.RemoteViews;

import com.foster.softwares.callnotes.R;
import com.foster.softwares.callnotes.UI.AddCallNoteActivity;
import com.foster.softwares.callnotes.baseutils.Utils;

import java.util.List;

/**
 * Created by Pranav on 29/07/16.
 */
public class PhoneCallStateListener extends PhoneStateListener{

    private String TAG = "PhoneCallStateListener";

    Intent mIntent;
    Context mContext;
    private int NOTIFICATION_ID = 0;
    private NotificationManager mNotificationManager;

    public PhoneCallStateListener(Intent in, Context context){
        this.mIntent = in;
        this.mContext = context;
    }

    @Override
    public void onCallStateChanged(int state, String incomingNumber) {
        super.onCallStateChanged(state, incomingNumber);
        if(incomingNumber.length()>2)
            Log.i(TAG, "onCallStateChanged" + state + " : " + incomingNumber + " : " + getContactDisplayNameByNumber(incomingNumber));
        else
            return;

        NOTIFICATION_ID = Utils.getData("NOTIFICATION_ID", 0, mContext);
        Utils.saveData("NOTIFICATION_ID", NOTIFICATION_ID+1, mContext);

        mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        String contact_name = getContactDisplayNameByNumber(incomingNumber);
        if(contact_name.equals("?"))
            contact_name="Unknown";

        switch(state){

            case TelephonyManager.CALL_STATE_IDLE :

            case TelephonyManager.CALL_STATE_RINGING :

            case TelephonyManager.CALL_STATE_OFFHOOK :




        }
    }

    public String getContactDisplayNameByNumber(String number) {
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
        String name = "?";

        ContentResolver contentResolver = mContext.getContentResolver();
        Cursor contactLookup = contentResolver.query(uri, new String[] {BaseColumns._ID,
                ContactsContract.PhoneLookup.DISPLAY_NAME }, null, null, null);

        try {
            if (contactLookup != null && contactLookup.getCount() > 0) {
                contactLookup.moveToNext();
                name = contactLookup.getString(contactLookup.getColumnIndex(ContactsContract.Data.DISPLAY_NAME));
                //String contactId = contactLookup.getString(contactLookup.getColumnIndex(BaseColumns._ID));
            }
        } finally {
            if (contactLookup != null) {
                contactLookup.close();
            }
        }

        return name;
    }
}
