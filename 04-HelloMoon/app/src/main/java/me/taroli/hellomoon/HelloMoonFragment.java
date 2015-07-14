package me.taroli.hellomoon;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by Matt on 13/07/15.
 */
public class HelloMoonFragment extends Fragment {
    private Button playBtn;
    private Button stopBtn;
    private AudioPlayer player = new AudioPlayer();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_hello_moon, container, false);

        playBtn = (Button) v.findViewById(R.id.playBtn);
        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player.play(getActivity());
                updateButtons();
            }
        });

        stopBtn = (Button) v.findViewById(R.id.stopBtn);
        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player.stop();
                updateButtons();
            }
        });

        updateButtons();
        return v;
    }

    private void updateButtons() {
        boolean playing = player.isPlaying();
        playBtn.setEnabled(!playing);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        player.stop();
    }
}
