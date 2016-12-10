package de.hwr_berlin.quizapp.network;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import de.hwr_berlin.quizapp.BuildConfig;
import de.hwr_berlin.quizapp.R;

/**
 * Created by oruckdeschel on 29.02.2016.
 */
public class NetworkUtil {

    public static final String BASE_URL = BuildConfig.SERVER_IP;
    private static final String TAG = NetworkUtil.class.getSimpleName();

    public static boolean hasInternetConnection(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        boolean conntection = networkInfo != null && networkInfo.isConnected();

        Log.d(TAG, "Internet connection check: " + conntection);
        return conntection;
    }

    public static InputStream makeRequest(final Context context, String urlStr) throws IOException {
        Log.d(TAG, "Make request to: " + urlStr);
        if (NetworkUtil.hasInternetConnection(context)) {
            int resCode = -1;

            URL url = new URL(urlStr);
            URLConnection urlConnection = url.openConnection();
            urlConnection.setConnectTimeout(3000);

            if (!(urlConnection instanceof HttpURLConnection)) {
                Log.e(TAG, "URL is no HttpURLConnection.");
                throw new IOException ("URL is no HttpURLConnection.");
            }

            HttpURLConnection httpConn = (HttpURLConnection) urlConnection;
            httpConn.setAllowUserInteraction(false);
            httpConn.setInstanceFollowRedirects(true);
            httpConn.setRequestMethod("GET");
            httpConn.connect();

            resCode = httpConn.getResponseCode();

            if (resCode == HttpURLConnection.HTTP_OK) {
                return httpConn.getInputStream();
            }

        } else {
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, R.string.error_no_connection, Toast.LENGTH_LONG).show();
                }
            });
        }

        Log.wtf(TAG, "The result of the internet fetch is null. But why? URL: " + urlStr);

        return null;
    }

}
