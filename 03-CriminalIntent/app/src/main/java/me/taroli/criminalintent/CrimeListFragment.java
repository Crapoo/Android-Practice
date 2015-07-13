package me.taroli.criminalintent;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.ArrayList;

/**
 * Created by Matt on 13/07/15.
 */
public class CrimeListFragment extends ListFragment {
    private static final String TAG = "CrimeListFragment";

    public ArrayList<Crime> crimes;
    private ArrayAdapter<Crime> adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle(R.string.crimes_title);
        crimes = CrimeLab.getINSTANCE(getActivity()).getCrimes();

        adapter = new CrimeAdapter(crimes);
        setListAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((CrimeAdapter) getListAdapter()).notifyDataSetChanged();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Crime c = ((CrimeAdapter) getListAdapter()).getItem(position);

        Intent i = new Intent(getActivity(), CrimePagerActivity.class);
        i.putExtra(CrimeFragment.EXTRA_CRIME_ID, c.getId());
        startActivity(i);
    }

    private class CrimeAdapter extends ArrayAdapter<Crime> {

        public CrimeAdapter(ArrayList<Crime> crimes) {
            super(getActivity(), 0, crimes);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            /* If no view, inflate one */
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.list_item_crime, null);
            }
            Crime c = getItem(position);

            TextView titleTv = (TextView) convertView.findViewById(R.id.crime_list_item_titleTextView);
            titleTv.setText(c.getTitle());

            TextView dateTv = (TextView) convertView.findViewById(R.id.crime_list_item_dateTextView);
            DateFormat df = DateFormat.getDateInstance(DateFormat.FULL);
            dateTv.setText(df.format(c.getDate()));

            CheckBox solvedChkbx = (CheckBox) convertView.findViewById(R.id.crime_list_item_solvedChkbx);
            solvedChkbx.setChecked(c.isSolved());

            return convertView;
        }
    }
}
