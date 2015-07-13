package me.taroli.criminalintent;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import java.security.spec.ECField;
import java.text.DateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * Created by Matt on 5/07/15.
 */
public class CrimeFragment extends Fragment {
    public static final String EXTRA_CRIME_ID = "me.taroli.criminalIntent.crime.id";

    private Crime crime;
    private EditText titleField;

    private Button dateButton;
    private CheckBox solvedCheckBox;

    public static CrimeFragment newInstance(UUID crimeId) {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_CRIME_ID, crimeId);

        CrimeFragment frag = new CrimeFragment();
        frag.setArguments(args);

        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID id = (UUID) getArguments().getSerializable(EXTRA_CRIME_ID);
        crime = CrimeLab.getINSTANCE(getActivity()).getCrime(id);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime, container, false);

        titleField = (EditText) v.findViewById(R.id.crime_title);
        titleField.setText(crime.getTitle());
        titleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Intentionally left blank
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                crime.setTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Intentionally left blank
            }
        });


        dateButton = (Button) v.findViewById(R.id.crime_date_btn);

        DateFormat df = DateFormat.getDateInstance(DateFormat.FULL);
        dateButton.setText(df.format(crime.getDate()));
        dateButton.setEnabled(false);

        solvedCheckBox = (CheckBox) v.findViewById(R.id.crime_solved_chkbx);
        solvedCheckBox.setChecked(crime.isSolved());
        solvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                crime.setSolved(isChecked);
            }
        });
        return v;
    }
}
