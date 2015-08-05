package me.taroli.runtracker;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import me.taroli.runtracker.RunDatabaseHelper.RunCursor;

/**
 * Created by Matt on 31/07/15.
 */
public class RunManager {

    private static final String TAG = "RunManager";

    public static final String PREFS_FILE = "runs";
    public static final String PREF_CURRENT_RUN_ID = "RunManager.currentRunId";

    public static final String ACTION_LOCATION =
            "me.taroli.runtracker.ACTION_LOCATION";

    private static RunManager instance;
    private Context appContext;
    private LocationManager locationManager;

    private RunDatabaseHelper dbHelper;
    private SharedPreferences prefs;
    private long currentRunId;

    private RunManager(Context appContext) {
        this.appContext = appContext;
        locationManager = (LocationManager) appContext
                .getSystemService(Context.LOCATION_SERVICE);

        dbHelper = new RunDatabaseHelper(appContext);
        prefs = appContext.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);
        currentRunId = prefs.getLong(PREF_CURRENT_RUN_ID, -1);
    }

    public static RunManager get(Context c) {
        if (instance == null) {
            instance = new RunManager(c.getApplicationContext());
        }
        return instance;
    }

    private PendingIntent getLocationPendingIntent(boolean shouldCreate) {
        Intent broadcast = new Intent(ACTION_LOCATION);
        int flags = shouldCreate ? 0 : PendingIntent.FLAG_NO_CREATE;
        return PendingIntent.getBroadcast(appContext, 0, broadcast, flags);
    }

    public void startLocationUpdates() {
        String provider = LocationManager.GPS_PROVIDER;

        Location lastKnown = locationManager.getLastKnownLocation(provider);
        if (lastKnown != null) {
            lastKnown.setTime(System.currentTimeMillis());
            broadcastLocation(lastKnown);
        }

        PendingIntent pi = getLocationPendingIntent(true);
        locationManager.requestLocationUpdates(provider, 0, 0, pi);
    }

    private void broadcastLocation(Location location) {
        Intent broadcast = new Intent(ACTION_LOCATION);
        broadcast.putExtra(LocationManager.KEY_LOCATION_CHANGED, location);
        appContext.sendBroadcast(broadcast);
    }

    public Run startNewRun() {
        Run run = insertRun();
        startTrackingRun(run);
        return run;
    }

    public void startTrackingRun(Run run) {
        currentRunId = run.getId();
        prefs.edit().putLong(PREF_CURRENT_RUN_ID, currentRunId).commit();
        startLocationUpdates();
    }

    public void stopRun() {
        stopLocationUpdates();
        currentRunId = -1;
        prefs.edit().remove(PREF_CURRENT_RUN_ID).commit();
    }

    public Run insertRun() {
        Run run = new Run();
        run.setId(dbHelper.insertRun(run));
        return run;
    }

    public Run getRun(long id) {
        Run run = null;
        RunCursor cursor = dbHelper.queryRun(id);
        cursor.moveToFirst();
        if (!cursor.isAfterLast()) {
            run = cursor.getRun();
        }
        cursor.close();
        return  run;
    }

    public Location getLastLocationForRun(long runId) {
        Location loc = null;
        RunDatabaseHelper.LocationCursor cursor = dbHelper.queryLastLocationForRun(runId);
        cursor.moveToFirst();
        if (!cursor.isAfterLast()) {
            loc = cursor.getLocation();
        }
        cursor.close();
        return loc;
    }

    public RunDatabaseHelper.LocationCursor getLocationsForRun(long runId) {
        return dbHelper.queryLocationsForRun(runId);
    }

    public RunCursor queryRuns() {
        return dbHelper.queryRuns();
    }

    public void insertLocation(Location location) {
        if (currentRunId != -1) {
            dbHelper.insertLocation(currentRunId, location);
        } else {
            Log.e(TAG, "Location received with no tracking run; ignoring");
        }
    }

    public void stopLocationUpdates() {
        PendingIntent pi = getLocationPendingIntent(false);
        if (pi != null) {
            locationManager.removeUpdates(pi);
            pi.cancel();
        }
    }

    public boolean isTrackingRun() {
        return getLocationPendingIntent(false) != null;
    }

    public boolean isTrackingRun(Run run) {
        return run != null && run.getId() == currentRunId;
    }
}
