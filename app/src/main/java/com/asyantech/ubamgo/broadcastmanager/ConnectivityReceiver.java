package com.asyantech.ubamgo.broadcastmanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ConnectivityReceiver extends BroadcastReceiver {

    public static ConnectivityReceiverListner connectivityReceiverListner;
    public ConnectivityReceiver() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork!= null && activeNetwork.isConnectedOrConnecting();

        if(connectivityReceiverListner != null){
            connectivityReceiverListner.onNetworkConnectionChanged(isConnected);
        }
    }

    //Create a method to check manually like click on button
    public static boolean isConnected(){
        ConnectivityManager cm = (ConnectivityManager) MyApp.getInstance().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork!= null && activeNetwork.isConnectedOrConnecting();
    }

    public interface ConnectivityReceiverListner{
        void onNetworkConnectionChanged(boolean isConnected);
    }
}
