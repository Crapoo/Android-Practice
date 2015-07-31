package me.taroli.runtracker;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Matt on 31/07/15.
 */
public class RunFragment extends Fragment {

    private Button startBtn, stopBtn;
    private TextView startedTv, latitudeTv, longitudeTv, altitudeTv, durationTv;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_run, container, false);

        startBtn = (Button) v.findViewById(R.id.run_startButton);
        stopBtn = (Button) v.findViewById(R.id.run_stopButton);

        startedTv = (TextView) v.findViewById(R.id.run_startedTextView);
        latitudeTv = (TextView) v.findViewById(R.id.run_latitudeTextView);
        longitudeTv = (TextView) v.findViewById(R.id.run_longitudeTextView);
        altitudeTv = (TextView) v.findViewById(R.id.run_altitudeTextView);
        durationTv = (TextView) v.findViewById(R.id.run_durationTextView);

        return v;
    }
}
