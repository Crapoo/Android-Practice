package me.taroli.runtracker;

import android.content.res.Resources;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.Date;

import me.taroli.runtracker.RunDatabaseHelper.LocationCursor;

/**
 * Created by Matt on 5/08/15.
 */
public class RunMapFragment extends SupportMapFragment implements LoaderCallbacks<Cursor> {

    private static final String ARG_RUN_ID = "RUN_ID";
    private static final int LOAD_LOCATION = 0;

    private GoogleMap gMap;
    private LocationCursor cursor;

    public static RunMapFragment newInstance(long runId) {

        Bundle args = new Bundle();
        args.putLong(ARG_RUN_ID, runId);
        RunMapFragment fragment = new RunMapFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            long runId = args.getLong(ARG_RUN_ID, -1);
            if (runId != -1) {
                LoaderManager lm = getLoaderManager();
                lm.initLoader(LOAD_LOCATION, args, this);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);

        gMap = getMap();
        gMap.setMyLocationEnabled(true);

        return v;
    }

    @SuppressWarnings("deprecation")
    private void updateUI() {
        if (gMap == null || cursor == null) {
            return;
        }

        PolylineOptions line = new PolylineOptions();

        LatLngBounds.Builder latBuilder = new LatLngBounds.Builder();

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Location loc = cursor.getLocation();
            LatLng latLng = new LatLng(loc.getLatitude(), loc.getLongitude());

            Resources res = getResources();

            if (cursor.isFirst()) {
                String startDate = new Date(loc.getTime()).toString();
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(latLng)
                        .title(res.getString(R.string.run_start))
                        .snippet(res.getString(R.string.run_started_at, startDate));
                gMap.addMarker(markerOptions);
            } else if (cursor.isLast()) {
                String endDate = new Date(loc.getTime()).toString();
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(latLng)
                        .title(res.getString(R.string.run_finish))
                        .snippet(res.getString(R.string.run_finished_at, endDate));
                gMap.addMarker(markerOptions);
            }

            line.add(latLng);
            latBuilder.include(latLng);
            cursor.moveToNext();
        }

        gMap.addPolyline(line);
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        LatLngBounds latLngBounds = latBuilder.build();
        CameraUpdate movement = CameraUpdateFactory
                .newLatLngBounds(latLngBounds, display.getWidth(), display.getHeight(), 15);
        gMap.moveCamera(movement);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        long runId = args.getLong(ARG_RUN_ID, -1);
        return new LocationListCursorLoader(getActivity(), runId);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        cursor = (LocationCursor) data;
        updateUI();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        cursor.close();
        cursor = null;
    }
}
