package me.taroli.runtracker;

import android.content.Context;
import android.database.Cursor;

/**
 * Created by Matt on 5/08/15.
 */
public class LocationListCursorLoader extends SQLiteCursorLoader {

    private final long runId;

    public LocationListCursorLoader(Context context, long runId) {
        super(context);
        this.runId = runId;
    }

    @Override
    protected Cursor loadCursor() {
        return RunManager.get(getContext()).getLocationsForRun(runId);
    }
}
