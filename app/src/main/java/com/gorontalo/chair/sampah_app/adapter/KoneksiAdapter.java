package com.gorontalo.chair.sampah_app.adapter;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class KoneksiAdapter {
    private Context _context;

    public KoneksiAdapter(Context context){
        this._context = context;
    }

    public boolean isConnectingToInternet(){
        ConnectivityManager cm = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiNetwork != null && wifiNetwork.isConnected()){
            return true;
        }
        NetworkInfo mobileNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (mobileNetwork != null && mobileNetwork.isConnected()){
            return true;
        }
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected()){
            return true;
        }
        return false;
    }
}
