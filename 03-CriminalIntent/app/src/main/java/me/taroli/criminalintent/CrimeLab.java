package me.taroli.criminalintent;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.UUID;

/**
 * Created by Matt on 13/07/15.
 */
public class CrimeLab {
    private static final String TAG = "CrimeLab";
    private static final String FILE = "crimes.json";

    private static CrimeLab instance;

    private Context appContext;
    private ArrayList<Crime> crimes;
    private Comparator<Crime> comp;
    private JSONSerializer jsonSerializer;

    private CrimeLab(Context appContext) {
        this.appContext = appContext;
        jsonSerializer = new JSONSerializer(appContext, FILE);

        try {
            crimes = jsonSerializer.loadCrimes();
        } catch (Exception e) {
            crimes = new ArrayList<Crime>();
            Log.e(TAG, "Error loading crimes: ", e);
        }

        comp = new Comparator<Crime>() {
            @Override
            public int compare(Crime lhs, Crime rhs) {
                return lhs.getDate().compareTo(rhs.getDate());
            }
        };
    }

    public static CrimeLab getINSTANCE(Context c) {
        if (instance == null) {
            instance = new CrimeLab(c.getApplicationContext());
        }
        return instance;
    }

    public void addCrime(Crime c) {
        crimes.add(c);
    }

    public void sortCrimes() {
        Collections.sort(crimes, comp);
    }

    public ArrayList<Crime> getCrimes() {
        return crimes;
    }

    public Crime getCrime(UUID id) {
        for (Crime c : crimes) {
            if (c.getId().equals(id)) {
                return c;
            }
        }
        return null;
    }

    public boolean saveCrimes() {
        try {
            jsonSerializer.saveCrimes(crimes);
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error saving crimes: ", e);
            return false;
        }
    }
}
