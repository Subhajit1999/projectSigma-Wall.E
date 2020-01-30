package com.subhajitkar.projectsigma.hydra.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

public class NetworkUtils {
    private static final String TAG = "NetworkUtils";
    private Context mContext;

    public NetworkUtils(Context context){
        mContext = context;
    }

    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager)mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public boolean checkConnection(View view){
        if(isOnline()){
            return true;
        }else{
            Snackbar.make(view,StaticUtils.NETWORK_ERROR_MESSAGE,Snackbar.LENGTH_SHORT).show();
            return false;
        }
    }
}
