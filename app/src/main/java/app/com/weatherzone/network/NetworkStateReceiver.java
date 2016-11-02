package app.com.weatherzone.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import app.com.weatherzone.utils.Constants;

/**
 * This class handles changes in network connectivity
 * Author : Varun
 * Date : 2/11/2016.
 */

public class NetworkStateReceiver extends BroadcastReceiver {

    //Listeners for change in the network state
    protected List<NetworkStateReceiverListener> listeners;
    //Connectivity flag
    protected Boolean connected;

    /**
     * This constructor intialises the Network State Listeners and connectivity flag
     */
    public NetworkStateReceiver() {
        listeners = new ArrayList<>();
        connected = null;
    }

    /**
     * This method is called whenever there is a change in network connectivity
     * @param context - application context
     * @param intent - the intent being received
     */
    public void onReceive(Context context, Intent intent) {
        if(intent == null || intent.getExtras() == null)
            return;

        //Fetch Network Information
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = manager.getActiveNetworkInfo();
        //Set connectivity flag to true if connected to the network
        if(ni != null && ni.getState() == NetworkInfo.State.CONNECTED) {
            Log.d(Constants.TAG,getClass().getName()+"onReceive - Connected to network" );
            connected = true;
        }
        //Set connectivity flag to false if disconnected from the network
        else if(intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY,Boolean.FALSE)) {
            Log.d(Constants.TAG,getClass().getName()+"onReceive - Disconnected from network" );
            connected = false;
        }
        //Notify to all listeners
        notifyStateToAll();
    }

    /**
     * This method notifies the change in network state to all listeners
     */
    private void notifyStateToAll() {
        for(NetworkStateReceiverListener listener : listeners)
            notifyState(listener);
    }

    /**
     * This method
     * @param listener
     */
    private void notifyState(NetworkStateReceiverListener listener) {
        if(connected == null || listener == null)
            return;
        //Notify that network is available
        if(connected == true) {
            Log.d(Constants.TAG, getClass().getName() + "notifyState - Performing network available" +
                    "post-operations");
            listener.networkAvailable();
        }
        //Notify that network is unavailable
        else
        {
            Log.d(Constants.TAG,getClass().getName()+"notifyState - Performing network unavailable" +
                    "post-operations" );
            listener.networkUnavailable();
        }
    }

    /**
     * This method adds network state receiver listeners to the list of listeners
     * @param l - Networkstatereceiver listener instance
     */
    public void addListener(NetworkStateReceiverListener l) {
        listeners.add(l);
        notifyState(l);
    }

    /**
     * This interface manages operations to be executed on network connectivity/disconnectivity
     */
    public interface NetworkStateReceiverListener {
        void networkAvailable();
        void networkUnavailable();
    }
}