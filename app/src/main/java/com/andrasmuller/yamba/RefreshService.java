package com.andrasmuller.yamba;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.marakana.android.yamba.clientlib.YambaClient;
import com.marakana.android.yamba.clientlib.YambaClient.Status;
import com.marakana.android.yamba.clientlib.YambaClientException;

import java.util.List;

/**
 * Created by andras on 2014.07.31..
 */
public class RefreshService extends IntentService {
    static final String TAG = "RefreshService";

    public RefreshService() {
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreated");
    }

    @Override
    public void onHandleIntent(Intent intent) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        final String username = prefs.getString("username", "");
        final String password = prefs.getString("password", "");

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            Toast.makeText(this,
                    getResources().getString(R.string.err_empty_credentials),
                    Toast.LENGTH_LONG).show();
            return;
        }
        Log.d(TAG, "onStarted");
        YambaClient cloud = new YambaClient(username, password); //
        try {
            List<Status> timeline = cloud.getTimeline(20); //
            for (Status status : timeline) { //
                Log.d(TAG,
                        String.format("%s: %s", status.getUser(),
                                status.getMessage())); //
            }
        } catch (YambaClientException e) { //
            Log.e(TAG, "Failed to fetch the timeline", e);
            e.printStackTrace();
        }
        return;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroyed");
    }
}
