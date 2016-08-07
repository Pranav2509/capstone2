package com.foster.softwares.callnotes.receiver;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.foster.softwares.callnotes.R;
import com.foster.softwares.callnotes.UI.AddCallNoteActivity;
import com.foster.softwares.callnotes.UI.MainActivity;
import com.foster.softwares.callnotes.service.CallNotesWidgetService;

/**
 * Created by Pranav on 30/07/16.
 */
public class CallNotesWidgetProvider extends AppWidgetProvider {

    public static String ACTION_WIDGET_ABOUT = "ActionReceiverAbout";
    private String TAG = "CallNotesWidgetProvider";

    private static final String REFRESH_ACTION = "REFRESH";

    public static void sendRefreshBroadcast(Context context) {
        Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intent.setComponent(new ComponentName(context, CallNotesWidgetProvider.class));
        context.sendBroadcast(intent);
    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        final String action = intent.getAction();
        Log.i(TAG, "onreceive" + action);

        if (AppWidgetManager.ACTION_APPWIDGET_UPDATE.equals(action)) {
            // refresh all your widgets
            Log.i(TAG, "onreceive rfresh");
            AppWidgetManager mgr = AppWidgetManager.getInstance(context);
            ComponentName cn = new ComponentName(context, CallNotesWidgetProvider.class);
            //onUpdate(context, mgr, mgr.getAppWidgetIds(cn));
            mgr.notifyAppWidgetViewDataChanged(mgr.getAppWidgetIds(cn), R.id.list);
        }
        super.onReceive(context, intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.i(TAG, "onUpdate");

        for (int i = 0; i < appWidgetIds.length; ++i) {

            Intent intent = new Intent(context, CallNotesWidgetService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);

            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
            RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget_listview);
            rv.setRemoteAdapter(appWidgetIds[i], R.id.list, intent);

            rv.setEmptyView(R.id.list, R.id.empty_view);

            Intent startActivityIntent = new Intent(context, AddCallNoteActivity.class);
            PendingIntent startActivityPendingIntent = PendingIntent.getActivity(context, 0,
                    startActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            rv.setPendingIntentTemplate(R.id.list, startActivityPendingIntent);


            /*Intent toastIntent = new Intent(context, CallNotesWidgetProvider.class);
            toastIntent.setAction(ACTION_WIDGET_ABOUT);

            PendingIntent toastPendingIntent = PendingIntent.getBroadcast(context, 0, toastIntent,
                    0);
            rv.setOnClickPendingIntent(R.id.base_ll, toastPendingIntent);*/

            appWidgetManager.updateAppWidget(appWidgetIds[i], rv);
        }
    }



}
