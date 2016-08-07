package com.foster.softwares.callnotes.adapter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.foster.softwares.callnotes.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pranav on 26/07/16.
 */
public class CallLogsBaseAdapter extends PagerAdapter {

    final String TAG = "CallLogsBaseAdapter";
    private final ArrayList<String> list;
    private final Activity activity;
    Context mContext;
    RecyclerView contact_list_rv;
    TextView topic_tv;
    LayoutInflater inflater;
    String keywords;

    public CallLogsBaseAdapter(Activity activity, ArrayList<String> list, String keywords) {
        this.list = list;
        mContext = activity.getBaseContext();
        this.activity = activity;
        this.keywords = keywords;
        Log.i(TAG, "CallLogsBaseAdapter constructor");
    }

    @Override
    public int getCount() {
        return list.size();
    }


    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        inflater = LayoutInflater.from(mContext);
        final View contact_lv = inflater.inflate(R.layout.contact_lv, container, false);

        Log.i(TAG, "CallLogsBaseAdapter instantiateItem");

        contact_list_rv = (RecyclerView) contact_lv.findViewById(R.id.contact_list_rv);

        ContactListViewAdapter contactListViewAdapter = null;
        contactListViewAdapter = new ContactListViewAdapter(mContext, position, activity, keywords);

        contact_list_rv.setLayoutManager(new LinearLayoutManager(mContext));
        contact_list_rv.setAdapter(contactListViewAdapter);

        container.addView(contact_lv);
        return contact_lv;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view==object;
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }
}
