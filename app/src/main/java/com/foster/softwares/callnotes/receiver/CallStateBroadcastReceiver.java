package com.foster.softwares.callnotes.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.support.v4.app.NotificationCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.foster.softwares.callnotes.R;
import com.foster.softwares.callnotes.UI.AddCallNoteActivity;
import com.foster.softwares.callnotes.baseutils.Utils;

/**
 * Created by Pranav on 29/07/16.
 */
public class CallStateBroadcastReceiver extends BroadcastReceiver {
    private String TAG = "CallStateBroadcastReceiver";
    private NotificationManager mNotificationManager;
    private int NOTIFICATION_ID=0;

    @Override
    public void onReceive(Context context, Intent intent) {



        String newPhoneState = intent.hasExtra(TelephonyManager.EXTRA_STATE) ? intent.getStringExtra(TelephonyManager.EXTRA_STATE) : null;
        String number = intent.hasExtra(TelephonyManager.EXTRA_INCOMING_NUMBER) ? intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER) : null;
        //String outgping = intent.hasExtra(TelephonyManager.) ? intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER) : null;

        Log.i(TAG, "onreceive" + intent + " : "+ newPhoneState + " : " + number);

        if(number=="null" && number.length()<2)
            return;

        NOTIFICATION_ID = Utils.getData("NOTIFICATION_ID", 0, context);
        Utils.saveData("NOTIFICATION_ID", NOTIFICATION_ID+1, context);

        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String contact_name = getContactDisplayNameByNumber(number, context);
        if(contact_name.equals("?"))
            contact_name="Unknown";

        
        switch (newPhoneState){
            
            case "OFFHOOK" :

            case "IDLE" :

            case "RINGING" :

                Intent in = new Intent(context, AddCallNoteActivity.class);
                in.putExtra("number", number);
                in.putExtra("name", contact_name);


                PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                        in, PendingIntent.FLAG_UPDATE_CURRENT);

                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(context)
                                .setSmallIcon(R.mipmap.ic_call_black_24dp)
                                .setContentText("Tap to add note for "+number)
                                .setAutoCancel(true)
                                .setContentTitle(contact_name)
                                .setDefaults(Notification.DEFAULT_SOUND);

                mBuilder.setContentIntent(contentIntent);
                mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
                break;
            
        }
    }


    public String getContactDisplayNameByNumber(String number, Context context) {
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
        String name = "?";

        ContentResolver contentResolver = context.getContentResolver();
        Cursor contactLookup = contentResolver.query(uri, new String[] {BaseColumns._ID,
                ContactsContract.PhoneLookup.DISPLAY_NAME }, null, null, null);

        try {
            if (contactLookup != null && contactLookup.getCount() > 0) {
                contactLookup.moveToNext();
                name = contactLookup.getString(contactLookup.getColumnIndex(ContactsContract.Data.DISPLAY_NAME));
            }
        } finally {
            if (contactLookup != null) {
                contactLookup.close();
            }
        }

        return name;
    }
}
