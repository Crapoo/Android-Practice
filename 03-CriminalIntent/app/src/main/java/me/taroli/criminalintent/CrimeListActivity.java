package me.taroli.criminalintent;

import android.support.v4.app.Fragment;

/**
 * Created by Matt on 13/07/15.
 */
public class CrimeListActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new CrimeListFragment();
    }
}
