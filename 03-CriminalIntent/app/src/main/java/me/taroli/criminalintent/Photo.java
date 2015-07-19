package me.taroli.criminalintent;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Matt on 19/07/15.
 */
public class Photo {
    private static  final String JSON_FILENAME = "filename";
    private String filename;

    public Photo(String filename) {
        this.filename = filename;
    }

    public Photo(JSONObject obj) throws JSONException {
        filename = obj.getString(JSON_FILENAME);
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put(JSON_FILENAME, filename);
        return obj;
    }

    public String getFilename() {
        return filename;
    }
}
