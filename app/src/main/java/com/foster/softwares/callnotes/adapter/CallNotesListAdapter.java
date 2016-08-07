package com.foster.softwares.callnotes.adapter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.foster.softwares.callnotes.R;
import com.foster.softwares.callnotes.UI.AddCallNoteActivity;
import com.foster.softwares.callnotes.UI.MainActivity;
import com.foster.softwares.callnotes.database.CallNotesDBContract;
import com.foster.softwares.callnotes.database.CallNotesDataContentProvider;

/**
 * Created by Pranav on 28/07/16.
 */
public class CallNotesListAdapter extends RecyclerView.Adapter<CallNotesListHolder> {

    private static final String TAG = "CallNotesListAdapter";
    private final Activity mActivity;
    Context mContext;
    LayoutInflater mInflater;
    Cursor mCursor;


    public CallNotesListAdapter(Context context, Cursor cursor, Activity activity) {
        this.mContext = context;
        mInflater = LayoutInflater.from(context);
        this.mCursor = cursor;
        this.mActivity = activity;
    }


    @Override
    public CallNotesListHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = mInflater.inflate(R.layout.all_call_notes_single_view, parent, false);

        CallNotesListHolder callNotesListHolder = new CallNotesListHolder(v);

        return callNotesListHolder;
    }


    @Override
    public void onBindViewHolder(final CallNotesListHolder holder, int position) {

        mCursor.moveToPosition(position);

        Log.i(TAG, position + " : " + mCursor.getPosition() + " : " + mCursor.isLast() + " : " + mCursor.isFirst());

        holder.user_name_tv.setText(mCursor.getString(mCursor.getColumnIndex(CallNotesDBContract.CALL_NOTES_COL2)));

        holder.date_tv.setText(mCursor.getString(mCursor.getColumnIndex(CallNotesDBContract.CALL_NOTES_COL8)).split(" ")[0]);

        holder.user_call_note.setText(mCursor.getString(mCursor.getColumnIndex(CallNotesDBContract.CALL_NOTES_COL4)));



        holder.user_action_call.setTag(mCursor.getString(mCursor.getColumnIndex(CallNotesDBContract.CALL_NOTES_COL3)));

        holder.user_action_share.setTag(R.string.contact_note, mCursor.getString(mCursor.getColumnIndex(CallNotesDBContract.CALL_NOTES_COL4)));
        holder.user_action_share.setTag(mCursor.getString(mCursor.getColumnIndex(CallNotesDBContract.CALL_NOTES_COL2)));

        holder.user_action_delete.setTag(mCursor.getString(mCursor.getColumnIndex(CallNotesDBContract.CALL_NOTES_COL1)));
        holder.user_action_delete.setTag(R.string.position, position);

        holder.base_card_view.setTag(R.string.name, mCursor.getString(mCursor.getColumnIndex(CallNotesDBContract.CALL_NOTES_COL2)));
        holder.base_card_view.setTag(R.string.number, mCursor.getString(mCursor.getColumnIndex(CallNotesDBContract.CALL_NOTES_COL3)));
        holder.base_card_view.setTag(R.string.contact_note, mCursor.getString(mCursor.getColumnIndex(CallNotesDBContract.CALL_NOTES_COL4)));
        holder.base_card_view.setTag(R.string.contact_id, mCursor.getString(mCursor.getColumnIndex(CallNotesDBContract.CALL_NOTES_COL1)));


        holder.user_action_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + v.getTag().toString()));
                if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.CALL_PHONE)
                        != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(mContext, "Permission not granted", Toast.LENGTH_LONG).show();
                    return;
                }
                mActivity.startActivity(intent);

            }
        });

        holder.user_action_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    String shareBody = "Please add the note for " + v.getTag().toString()
                            +" - " + v.getTag(R.string.contact_note).toString();
                    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Add call notes");
                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);

                    mActivity.startActivity(Intent.createChooser(sharingIntent, mContext.getResources().getString(R.string.share_using)));
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });

        holder.user_action_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mContext.getContentResolver().delete(CallNotesDataContentProvider.CALL_NOTES_URI,
                        CallNotesDBContract.CALL_NOTES_COL1+"="+v.getTag().toString(), null);
                notifyDataSetChanged();
                ((MainActivity)mActivity).refreshList(Integer.parseInt(v.getTag(R.string.position).toString()));

            }
        });

        holder.base_card_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent in = new Intent(mActivity, AddCallNoteActivity.class);
                in.putExtra("name", v.getTag(R.string.name).toString());
                in.putExtra("number", v.getTag(R.string.number).toString());
                in.putExtra("note", v.getTag(R.string.contact_note).toString());
                in.putExtra("id", v.getTag(R.string.contact_id).toString());
                mActivity.startActivity(in);

            }
        });



    }

    @Override
    public int getItemCount() {


        if(mCursor!=null) {
            Log.i(TAG, mCursor.getCount()+"");
            return mCursor.getCount();

        }else
            return 0;
    }
}

class CallNotesListHolder extends RecyclerView.ViewHolder{

    public TextView user_name_tv;
    public TextView user_call_note;
    public ImageView user_action_share;
    public ImageView user_action_call;
    public ImageView user_action_delete;
    public TextView date_tv;
    CardView base_card_view;


    public CallNotesListHolder(View itemView) {
        super(itemView);

        user_name_tv = (TextView) itemView.findViewById(R.id.user_name_tv);
        user_call_note = (TextView) itemView.findViewById(R.id.user_call_note);
        user_action_share = (ImageView) itemView.findViewById(R.id.user_action_share);
        user_action_call = (ImageView) itemView.findViewById(R.id.user_action_call);
        user_action_delete = (ImageView) itemView.findViewById(R.id.user_action_delete);
        date_tv = (TextView) itemView.findViewById(R.id.date_tv);
        base_card_view = (CardView) itemView.findViewById(R.id.base_card_view);
    }
}
