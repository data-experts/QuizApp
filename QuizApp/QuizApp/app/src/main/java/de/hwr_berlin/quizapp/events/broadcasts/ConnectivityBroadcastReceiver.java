package de.hwr_berlin.quizapp.events.broadcasts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import de.hwr_berlin.quizapp.activities.MainActivity;
import de.hwr_berlin.quizapp.activities.QuizActivity;
import de.hwr_berlin.quizapp.network.NetworkUtil;
import de.hwr_berlin.quizapp.tasks.CategoryLoaderTask;

public class ConnectivityBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = ConnectivityBroadcastReceiver.class.getSimpleName();

    public ConnectivityBroadcastReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "Connectivity-State has changed.");
        boolean hasConnection = NetworkUtil.hasInternetConnection(context);

        if (hasConnection) {
            Log.i(TAG, "Has connection");
            MainActivity.CATEGORY_STORAGE.clear();
            new CategoryLoaderTask(context).execute(); // We can simply run this, because this broadcastreceiver will not run on QuizActivity -> so no quiz will be interrupted or manipulated.
        } else {
            Log.i(TAG, "Has no connection");
        }
    }
}
