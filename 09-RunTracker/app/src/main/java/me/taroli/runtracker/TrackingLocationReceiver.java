package me.taroli.runtracker;

import android.content.Context;
import android.location.Location;

/**
 * Created by Matt on 2/08/15.
 */
public class TrackingLocationReceiver extends LocationReceiver {

    @Override
    public void onLocationReceived(Context context, Location loc) {
        RunManager.get(context).insertLocation(loc);
    }
}
