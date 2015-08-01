package me.taroli.runtracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

    private BroadcastReceiver locationReceiver = new LocationReceiver() {

        @Override
        public void onLocationReceived(Context context, Location loc) {
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

    private Button startBtn, stopBtn;
    private TextView startedTv, latitudeTv, longitudeTv, altitudeTv, durationTv;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        runManager = RunManager.get(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_run, container, false);

        startBtn = (Button) v.findViewById(R.id.run_startButton);
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                run = runManager.startNewRun();
                updateUI();
            }
        });
        stopBtn = (Button) v.findViewById(R.id.run_stopButton);
        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runManager.stopRun();
                updateUI();
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

        if (run != null) {
            startedTv.setText(run.getStartDate().toString());
        }

        int durationSeconds = 0;
        if (run != null && lastLocation != null) {
            durationSeconds = run.getDurationSeconds(lastLocation.getTime());
            latitudeTv.setText(Double.toString(lastLocation.getLatitude()));
            longitudeTv.setText(Double.toString(lastLocation.getLongitude()));
            altitudeTv.setText(Double.toString(lastLocation.getAltitude()));
        }
        durationTv.setText(Run.formatDuration(durationSeconds));

        startBtn.setEnabled(!started);
        stopBtn.setEnabled(started);
    }
}
