package me.taroli.runtracker;

import android.content.Context;

/**
 * Created by Matt on 3/08/15.
 */
public class RunLoader extends DataLoader<Run> {

    private long runId;

    public RunLoader(Context context, long runId) {
        super(context);
        this.runId = runId;
    }

    @Override
    public Run loadInBackground() {
        return RunManager.get(getContext()).getRun(runId);
    }
}
