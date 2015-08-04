package me.taroli.runtracker;

import android.content.Context;
import android.location.Location;

/**
 * Created by Matt on 3/08/15.
 */
public class LastLocationLoader extends  DataLoader<Location> {

    public long runId;

    public LastLocationLoader(Context context, long runId) {
        super(context);
        this.runId = runId;
    }

    @Override
    public Location loadInBackground() {
        return RunManager.get(getContext()).getLastLocationForRun(runId);
    }
}
