package me.taroli.runtracker;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.AsyncTaskLoader;

/**
 * Created by Matt on 3/08/15.
 */
public abstract class SQLiteCursorLoader extends AsyncTaskLoader<Cursor> {

    private Cursor cursor;

    public SQLiteCursorLoader(Context context) {
        super(context);
    }

    protected abstract Cursor loadCursor();

    @Override
    public Cursor loadInBackground() {
        Cursor cur = loadCursor();
        if (cur != null) {
            cur.getCount();
        }
        return cur;
    }

    @Override
    public void deliverResult(Cursor data) {
        Cursor oldCursor = cursor;
        cursor = data;

        if (isStarted()) {
            super.deliverResult(data);
        }

        if (oldCursor != null && oldCursor != data && !oldCursor.isClosed()) {
            oldCursor.close();
        }
    }

    @Override
    protected void onStartLoading() {
        if (cursor != null) {
            deliverResult(cursor);
        }
        if (takeContentChanged() || cursor == null) {
            forceLoad();
        }
    }

    @Override
    protected void onStopLoading() {
        cancelLoad();
    }

    @Override
    public void onCanceled(Cursor data) {
        if (data != null && !data.isClosed()) {
            data.close();
        }
    }

    @Override
    protected void onReset() {
        super.onReset();

        onStopLoading();

        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        cursor = null;
    }
}
