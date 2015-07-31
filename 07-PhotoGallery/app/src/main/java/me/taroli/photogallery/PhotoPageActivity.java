package me.taroli.photogallery;

import android.support.v4.app.Fragment;

/**
 * Created by Matt on 31/07/15.
 */
public class PhotoPageActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new PhotoPageFragment();
    }
}
