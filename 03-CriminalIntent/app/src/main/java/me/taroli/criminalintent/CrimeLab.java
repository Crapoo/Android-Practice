package me.taroli.criminalintent;

import android.content.Context;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by Matt on 13/07/15.
 */
public class CrimeLab {
    private static CrimeLab INSTANCE;
    private Context appContext;
    private ArrayList<Crime> crimes;

    private CrimeLab(Context appContext) {
        this.appContext = appContext;
        crimes = new ArrayList<Crime>();
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
