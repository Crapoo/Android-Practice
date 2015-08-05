package me.taroli.runtracker;

import android.support.v4.app.Fragment;

/**
 * Created by Matt on 5/08/15.
 */
public class RunMapActivity extends SingleFragmentActivity {

    public static final String EXTRA_RUN_ID = "me.taroli.runtracker.run_id";

    @Override
    protected Fragment createFragment() {
        long runId = getIntent().getLongExtra(EXTRA_RUN_ID, -1);
        if (runId != -1) {
            return  RunMapFragment.newInstance(runId);
        } else {
            return new RunMapFragment();
        }
    }

}
