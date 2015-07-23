package me.taroli.remote;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Matt on 23/07/15.
 */
public class RemoteFragment extends Fragment {
    private TextView selectedTv;
    private TextView workingTv;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_remote, container, false);

        selectedTv = (TextView) v.findViewById(R.id.fragment_remote_selectedTextView);
        workingTv = (TextView) v.findViewById(R.id.fragment_remote_workingTextView);

        View.OnClickListener nbButtonListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView tv = (TextView) v;
                String working = workingTv.getText().toString();
                String text = tv.getText().toString();

                if (working.equals("0")) {
                    workingTv.setText(text);
                } else {
                    workingTv.setText(working + text);
                }
            }
        };

        Button zeroBtn = (Button) v.findViewById(R.id.fragment_remote_zeroBtn);
        zeroBtn.setOnClickListener(nbButtonListener);

        Button oneBtn = (Button) v.findViewById(R.id.fragment_remote_oneBtn);
        oneBtn.setOnClickListener(nbButtonListener);

        Button enterBtn = (Button) v.findViewById(R.id.fragment_remote_enterBtn);
        enterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence working = workingTv.getText();
                if (working.length() > 0) {
                    selectedTv.setText(working);
                }
                workingTv.setText("0");
            }
        });
        return v;
    }
}
