package com.okuloj.nhacchill;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int action = intent.getIntExtra(Constants.EXTRA_ACTION_MUSIC, -2);

        Intent intentService = new Intent(Constants.ACTION_SEND_ACTION_TO_SERVICE);
        intentService.putExtra(Constants.EXTRA_ACTION_MUSIC_TO_SERVICE, action);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intentService);
    }
}
