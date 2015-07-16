package me.taroli.criminalintent;

import android.content.Context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.UUID;

/**
 * Created by Matt on 13/07/15.
 */
public class CrimeLab {
    private static CrimeLab INSTANCE;
    private Context appContext;
    private ArrayList<Crime> crimes;
    private Comparator<Crime> comp;

    private CrimeLab(Context appContext) {
        this.appContext = appContext;
        crimes = new ArrayList<Crime>();
        comp = new Comparator<Crime>() {
            @Override
            public int compare(Crime lhs, Crime rhs) {
                return lhs.getDate().compareTo(rhs.getDate());
            }
        };
    }

    public static CrimeLab getINSTANCE(Context c) {
        if (INSTANCE == null) {
            INSTANCE = new CrimeLab(c.getApplicationContext());
        }
        return INSTANCE;
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
}
