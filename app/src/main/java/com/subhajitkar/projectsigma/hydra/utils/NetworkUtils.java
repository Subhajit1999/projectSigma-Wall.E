package com.subhajitkar.projectsigma.hydra.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class NetworkUtils {
    private static final String TAG = "NetworkUtils";
    private Context mContext;

    public NetworkUtils(Context context){
        mContext = context;
    }

    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager)mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }

    public boolean checkConnection(){
        if(isOnline()){
            return true;
        }else{
            Toast.makeText(mContext, "You are not connected to Internet", Toast.LENGTH_SHORT).show();
            return false;
        }
    }
}
