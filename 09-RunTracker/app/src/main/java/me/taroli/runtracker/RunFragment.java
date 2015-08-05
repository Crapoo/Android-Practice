package me.taroli.runtracker;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Matt on 31/07/15.
 */
public class RunFragment extends Fragment {

    private static final String TAG = "RunFragment";
    private static final int NOTIFICATION_ID = 0;
    private static final int LOAD_RUN = 0;
    private static  final int LOAD_LOCATION = 1;
    public static final String ARG_RUN_ID = "RUN_ID";

    private BroadcastReceiver locationReceiver = new LocationReceiver() {

        @Override
        public void onLocationReceived(Context context, Location loc) {
            if (!runManager.isTrackingRun(run)) {
                return;
            }
            lastLocation = loc;
            if (isVisible()) {
                updateUI();
            }
        }

        @Override
        public void onProviderEnabledChanged(boolean enabled) {
            int toastText = enabled ? R.string.gps_enabled : R.string.gps_disabled;
            Toast.makeText(getActivity(), toastText, Toast.LENGTH_LONG).show();
        }
    };

    private RunManager runManager;

    private Run run;
    private Location lastLocation;

    private Button startBtn, stopBtn, mapBtn;
    private TextView startedTv, latitudeTv, longitudeTv, altitudeTv, durationTv;

    public static RunFragment newInstance(long runId) {
        Bundle args = new Bundle();
        args.putLong(ARG_RUN_ID, runId);
        RunFragment fragment = new RunFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        runManager = RunManager.get(getActivity());

        Bundle args = getArguments();
        if (args != null) {
            long runId = args.getLong(ARG_RUN_ID, -1);
            if (runId != -1) {
                LoaderManager lm = getLoaderManager();
                lm.initLoader(LOAD_RUN, args, new RunLoaderCallbacks());
                lm.initLoader(LOAD_LOCATION, args, new LocationLoaderCallbacks());
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_run, container, false);

        startBtn = (Button) v.findViewById(R.id.run_startButton);
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (run == null) {
                    run = runManager.startNewRun();
                } else {
                    runManager.startTrackingRun(run);
                }
                showNotif();
                updateUI();
            }
        });
        stopBtn = (Button) v.findViewById(R.id.run_stopButton);
        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runManager.stopRun();
                hideNotif();
                updateUI();
            }
        });
        mapBtn = (Button) v.findViewById(R.id.run_mapButton);
        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), RunMapActivity.class);
                i.putExtra(RunMapActivity.EXTRA_RUN_ID, run.getId());
                startActivity(i);
            }
        });
        startedTv = (TextView) v.findViewById(R.id.run_startedTextView);
        latitudeTv = (TextView) v.findViewById(R.id.run_latitudeTextView);
        longitudeTv = (TextView) v.findViewById(R.id.run_longitudeTextView);
        altitudeTv = (TextView) v.findViewById(R.id.run_altitudeTextView);
        durationTv = (TextView) v.findViewById(R.id.run_durationTextView);

        updateUI();

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        getActivity().registerReceiver(locationReceiver,
                new IntentFilter(RunManager.ACTION_LOCATION));
    }

    @Override
    public void onStop() {
        getActivity().unregisterReceiver(locationReceiver);
        super.onStop();
    }

    private void updateUI() {
        boolean started = runManager.isTrackingRun();
        boolean trackingThisRun = runManager.isTrackingRun(run);

        if (run != null) {
            startedTv.setText(run.getStartDate().toString());
        }

        int durationSeconds = 0;
        if (run != null && lastLocation != null) {
            durationSeconds = run.getDurationSeconds(lastLocation.getTime());
            latitudeTv.setText(Double.toString(lastLocation.getLatitude()));
            longitudeTv.setText(Double.toString(lastLocation.getLongitude()));
            altitudeTv.setText(Double.toString(lastLocation.getAltitude()));
            mapBtn.setEnabled(true);
        } else {
            mapBtn.setEnabled(false);
        }
        durationTv.setText(Run.formatDuration(durationSeconds));

        startBtn.setEnabled(!started);
        stopBtn.setEnabled(started && trackingThisRun);
    }

    private void showNotif() {
        long currentRunId = getActivity().getSharedPreferences(
                RunManager.PREFS_FILE, Context.MODE_PRIVATE).
                getLong(RunManager.PREF_CURRENT_RUN_ID, -1);

        if (currentRunId == -1) {
            return;
        }

        Intent i = new Intent(getActivity(), RunActivity.class);
        i.putExtra(RunActivity.EXTRA_RUN_ID, currentRunId);

        PendingIntent pi = PendingIntent
                .getActivity(getActivity(), 0, i, PendingIntent.FLAG_UPDATE_CURRENT);

        Resources resources = getResources();

        Notification notif = new NotificationCompat.Builder(getActivity())
                .setTicker(resources.getString(R.string.app_name))
                .setSmallIcon(android.R.drawable.ic_menu_compass)
                .setContentTitle(resources.getString(R.string.app_name))
                .setContentText(resources.getString(R.string.tracking_run, currentRunId))
                .setContentIntent(pi)
                .setAutoCancel(false)
                .build();

        NotificationManager notificationManager =
                (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(TAG, NOTIFICATION_ID, notif);
    }

    private void hideNotif() {
        NotificationManager notificationManager =
                (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(TAG, NOTIFICATION_ID);
    }

    private class RunLoaderCallbacks implements LoaderManager.LoaderCallbacks<Run> {

        @Override
        public Loader<Run> onCreateLoader(int id, Bundle args) {
            return new RunLoader(getActivity(), args.getLong(ARG_RUN_ID));
        }

        @Override
        public void onLoadFinished(Loader<Run> loader, Run data) {
            run = data;
            updateUI();
        }

        @Override
        public void onLoaderReset(Loader<Run> loader) {
            /* Nothing to do */
        }
    }

    private class LocationLoaderCallbacks implements LoaderManager.LoaderCallbacks<Location> {

        @Override
        public Loader<Location> onCreateLoader(int id, Bundle args) {
            return  new LastLocationLoader(getActivity(), args.getLong(ARG_RUN_ID));
        }

        @Override
        public void onLoadFinished(Loader<Location> loader, Location data) {
            lastLocation = data;
            updateUI();
        }

        @Override
        public void onLoaderReset(Loader<Location> loader) {
            /* Nothing to do */
        }
    }
}
