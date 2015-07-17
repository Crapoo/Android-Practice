package me.taroli.criminalintent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.UUID;

/**
 * Created by Matt on 3/07/15.
 */
public class Crime {
    private static final String JSON_ID = "id";
    private static final String JSON_TITLE = "title";
    private static final String JSON_SOLVED = "solved";
    private static final String JSON_DATE = "date";

    private UUID id;
    private String title;
    private Date date;
    private boolean solved;

    public Crime() {
        id = UUID.randomUUID();
        date = new Date();
    }

    public Crime(JSONObject obj) throws  JSONException{
        id = UUID.fromString(obj.getString(JSON_ID));
        if (obj.has(JSON_TITLE)) {
            title = obj.getString(JSON_TITLE);
        }
        solved = obj.getBoolean(JSON_SOLVED);
        date = new Date(obj.getLong(JSON_DATE));
    }

    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isSolved() {
        return solved;
    }

    public void setSolved(boolean solved) {
        this.solved = solved;
    }

    @Override
    public String toString() {
        return title;
    }

    public JSONObject toJSON() throws JSONException{
        JSONObject obj = new JSONObject();
        obj.put(JSON_ID, id.toString());
        obj.put(JSON_TITLE, title);
        obj.put(JSON_SOLVED, solved);
        obj.put(JSON_DATE, date.getTime());
        return obj;
    }
}
