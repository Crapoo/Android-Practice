package me.taroli.criminalintent;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.io.File;
import java.text.DateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * Created by Matt on 5/07/15.
 */
public class CrimeFragment extends Fragment {
    public static final String EXTRA_CRIME_ID = "me.taroli.criminalIntent.crime.id";
    private static final String TAG = "CrimeFragment";
    public static final String DIALOG_DATE = "date";
    public static final String DIALOG_IMAGE = "photo";
    private static final int REQUEST_DATE = 0;
    public static final int REQUEST_PHOTO = 1;
    public static final int REQUEST_CONTACT = 2;

    private Crime crime;
    private EditText titleField;

    private Button dateBtn;
    private CheckBox solvedCheckBox;
    private ImageButton cameraBtn;
    private ImageView photoView;
    private Button reportBtn;
    private Button suspectBtn;

    public static CrimeFragment newInstance(UUID crimeId) {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_CRIME_ID, crimeId);

        CrimeFragment frag = new CrimeFragment();
        frag.setArguments(args);

        return frag;
    }

    @Override
    public void onStart() {
        super.onStart();
        showPhoto();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        UUID id = (UUID) getArguments().getSerializable(EXTRA_CRIME_ID);
        crime = CrimeLab.getINSTANCE(getActivity()).getCrime(id);
    }

    @Override
    public void onPause() {
        super.onPause();
        CrimeLab.getINSTANCE(getActivity()).saveCrimes();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (NavUtils.getParentActivityName(getActivity()) != null) {
                    NavUtils.navigateUpFromSameTask(getActivity());
                }
                return true;
            case R.id.menu_item_delete_crime:
                CrimeLab.getINSTANCE(getActivity()).deleteCrime(crime);
                if (NavUtils.getParentActivityName(getActivity()) != null) {
                    NavUtils.navigateUpFromSameTask(getActivity());
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @TargetApi(11)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime, container, false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            if (NavUtils.getParentActivityName(getActivity()) != null)
                getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        }

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


        dateBtn = (Button) v.findViewById(R.id.crime_date_btn);
        updateDate();
        dateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance(crime.getDate());
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
                dialog.show(fm, DIALOG_DATE);
            }
        });

        solvedCheckBox = (CheckBox) v.findViewById(R.id.crime_solved_chkbx);
        solvedCheckBox.setChecked(crime.isSolved());
        solvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                crime.setSolved(isChecked);
            }
        });

        cameraBtn = (ImageButton) v.findViewById(R.id.crime_imageBtn);
        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), CrimeCameraActivity.class);
                startActivityForResult(i, REQUEST_PHOTO);
            }
        });
        /* Check if camera exists. If not, disable button */
        PackageManager pm = getActivity().getPackageManager();
        boolean hasCamera = pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)
                || pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)
                || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD && Camera.getNumberOfCameras() > 0);
        if (!hasCamera) {
            cameraBtn.setEnabled(false);
        }

        photoView = (ImageView) v.findViewById(R.id.crime_imageView);
        photoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Photo p = crime.getPhoto();
                if (p == null) {
                    return;
                }

                FragmentManager fm = getActivity().getSupportFragmentManager();
                String path = getActivity().getFileStreamPath(p.getFilename()).getAbsolutePath();
                ImageFragment.newInstance(path).show(fm, DIALOG_IMAGE);
            }
        });

        if (crime.getPhoto() != null) {
            registerForContextMenu(photoView);
        }

        reportBtn = (Button) v.findViewById(R.id.crime_reportBtn);
        reportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
                i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject));
                i = Intent.createChooser(i, getString(R.string.send_report));
                startActivity(i);
            }
        });

        suspectBtn = (Button) v.findViewById(R.id.crime_suspectBtn);
        suspectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(i, REQUEST_CONTACT);
            }
        });

        if (crime.getSuspect() != null) {
            suspectBtn.setText(crime.getSuspect());
        }

        return v;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getActivity().getMenuInflater().inflate(R.menu.crime_fragment_context, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_delete_photo:
                deletePhoto();
                unregisterForContextMenu(photoView);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case REQUEST_DATE:
                Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
                crime.setDate(date);
                updateDate();
                return;
            case REQUEST_PHOTO:
                String filename = data.getStringExtra(CrimeCameraFragment.EXTRA_PHOTO_FILENAME);
                if (filename != null) {
                    if (crime.getPhoto() != null) {
                        deletePhoto();
                    }

                    Photo photo = new Photo(filename);
                    crime.setPhoto(photo);
                    registerForContextMenu(photoView);
                    showPhoto();
                }
                return;
            case REQUEST_CONTACT:
                Uri contactUri = data.getData();

                String[] fields = {ContactsContract.Contacts.DISPLAY_NAME};
                Cursor c = getActivity().getContentResolver()
                        .query(contactUri, fields, null, null, null);
                if (c.getCount() == 0) {
                    c.close();
                    return;
                }

                c.moveToFirst();
                String suspect = c.getString(0);
                crime.setSuspect(suspect);
                suspectBtn.setText(suspect);
                c.close();
                return;
            default:
                return;
        }
    }

    private void deletePhoto() {
        String path = getActivity().getFileStreamPath(crime.getPhoto().getFilename()).getAbsolutePath();
        File file = new File(path);
        file.delete();
        crime.setPhoto(null);
        photoView.setImageDrawable(null);
    }

    @Override
    public void onStop() {
        super.onStop();
        PictureUtils.cleanImageView(photoView);
    }

    private void updateDate() {
        DateFormat df = DateFormat.getDateInstance(DateFormat.FULL);
        dateBtn.setText(df.format(crime.getDate()));
    }

    private void showPhoto() {
        Photo p = crime.getPhoto();
        BitmapDrawable b = null;
        if (p != null) {
            String path = getActivity().getFileStreamPath(p.getFilename()).getAbsolutePath();
            b = PictureUtils.getScaledDrawable(getActivity(), path);
        }

        photoView.setImageDrawable(b);
    }

    private String getCrimeReport() {
        String solved = null;
        if (crime.isSolved()) {
            solved = getString(R.string.crime_report_solved);
        } else {
            solved = getString(R.string.crime_report_unsolved);
        }

        DateFormat df = DateFormat.getDateInstance(DateFormat.FULL);
        String dateString = df.format(crime.getDate()).toString();

        String suspect = crime.getSuspect();
        if (suspect == null) {
            suspect = getString(R.string.crime_report_no_suspect);
        } else {
            suspect = getString(R.string.crime_report_no_suspect, suspect);
        }

        String report = getString(R.string.crime_report_msg, crime.getTitle(),
                dateString, solved, suspect);
        return report;
    }
}
