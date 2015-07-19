package me.taroli.criminalintent;

import android.support.v4.app.Fragment;

/**
 * Created by Matt on 19/07/15.
 */
public class CrimeCameraActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new CrimeCameraFragment();
    }
}
