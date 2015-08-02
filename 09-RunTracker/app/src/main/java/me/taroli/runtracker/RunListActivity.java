package me.taroli.runtracker;

import android.support.v4.app.Fragment;

/**
 * Created by Matt on 3/08/15.
 */
public class RunListActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new RunListFragment();
    }

}
