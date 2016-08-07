package com.foster.softwares.callnotes.service;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Created by Pranav on 30/07/16.
 */
public class CallNotesWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new CallNoteWidgetRemoteViewFactory(this.getApplicationContext(), intent);
    }
}
