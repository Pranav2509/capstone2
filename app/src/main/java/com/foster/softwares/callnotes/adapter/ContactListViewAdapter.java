package com.foster.softwares.callnotes.adapter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.foster.softwares.callnotes.R;
import com.foster.softwares.callnotes.baseutils.Utils;

/**
 * Created by Pranav on 26/07/16.
 */
public class ContactListViewAdapter extends RecyclerView.Adapter<ContactDetailsViewHolder> {

    Cursor mCursor;
    int mPosition;
    LayoutInflater inflater;
    Context mContext;
    Activity mActivity;
    String TAG = "ContactListViewAdapter";


    public ContactListViewAdapter(Context context, int position, Activity activity, String keywords) {
        mContext = context;
        mActivity = activity;

        String search="";
        if(position == 0){

            search = CallLog.Calls.CACHED_NAME + " LIKE '%"+keywords+"%' OR "+
                    CallLog.Calls.NUMBER + " LIKE '%" + keywords + "%'";

            if (ActivityCompat.checkSelfPermission(context,
                    Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {

            }else {

                mCursor = mContext.getContentResolver().query(CallLog.Calls.CONTENT_URI, null,
                        search, null, CallLog.Calls.DATE + "  DESC limit 30;");
            }
        }else{
            if (ActivityCompat.checkSelfPermission(context,
                    Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {

            }else {
                search = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " LIKE '%" + keywords +
                        "%' OR " + ContactsContract.CommonDataKinds.Phone.NUMBER + " LIKE '%" + keywords + "%'";
                mCursor = mContext.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null, search, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC ");
            }
        }
        Log.i(TAG, "ContactListViewAdapter constructor");
        mPosition = position;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public ContactDetailsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = inflater.inflate(R.layout.calllogs_and_contacts_single_layout, parent, false);

        ContactDetailsViewHolder holder = new ContactDetailsViewHolder(v);

        Log.i(TAG, "onCreateViewHolder");

        return holder;
    }

    @Override
    public void onBindViewHolder(ContactDetailsViewHolder holder, int position) {

        Log.i(TAG, "onBindViewHolder " + mCursor.getCount());

        mCursor.moveToPosition(position);
        if(mPosition==0) {

            if(mCursor.getString(mCursor.getColumnIndex(CallLog.Calls.CACHED_NAME))!=null){
                holder.contact_name.setText(mCursor.getString(mCursor.getColumnIndex(CallLog.Calls.CACHED_NAME)));
                holder.base_ll.setTag(R.string.name, mCursor.getString(mCursor.getColumnIndex(CallLog.Calls.CACHED_NAME)));
            }else{
                holder.contact_name.setText(mCursor.getString(mCursor.getColumnIndex(CallLog.Calls.NUMBER)));
                holder.base_ll.setTag(R.string.name, mCursor.getString(mCursor.getColumnIndex(CallLog.Calls.NUMBER)));

            }
            holder.contact_call_details.setText(mCursor.getString(mCursor.getColumnIndex(CallLog.Calls.DURATION)));
            holder.contact_call_type.setText(mCursor.getString(mCursor.getColumnIndex(CallLog.Calls.TYPE)));

            holder.base_ll.setTag(R.string.number, mCursor.getString(mCursor.getColumnIndex(CallLog.Calls.NUMBER)));
        }else{
            holder.contact_name.setText(mCursor.getString(mCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME )));
            holder.base_ll.setTag(R.string.number, mCursor.getString(mCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
            holder.base_ll.setTag(R.string.name, mCursor.getString(mCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME )));
        }

        holder.base_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent returnIntent = new Intent();
                returnIntent.putExtra("name",v.getTag(R.string.name).toString());
                returnIntent.putExtra("number",v.getTag(R.string.number).toString());
                mActivity.setResult(Activity.RESULT_OK,returnIntent);
                mActivity.finish();

            }
        });


    }

    @Override
    public int getItemCount() {
        Log.i(TAG, "getItemCount " + mCursor.getCount());
        return mCursor.getCount();
    }
}

class ContactDetailsViewHolder extends RecyclerView.ViewHolder{

    //ImageView contact_iv;
    TextView contact_name;
    TextView contact_call_details;
    TextView contact_call_type;
    RelativeLayout base_ll;


    public ContactDetailsViewHolder(View view) {
        super(view);

        //contact_iv = (ImageView) view.findViewById(R.id.contact_iv);
        contact_name = (TextView) view.findViewById(R.id.contact_name);
        contact_call_details = (TextView) view.findViewById(R.id.contact_call_details);
        contact_call_type = (TextView) view.findViewById(R.id.contact_call_type);
        base_ll = (RelativeLayout) view.findViewById(R.id.base_ll);

    }
}