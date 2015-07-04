package me.taroli.criminalintent;

import java.util.UUID;

/**
 * Created by Matt on 3/07/15.
 */
public class Crime {
    private UUID id;
    private String title;

    public Crime() {
        id = UUID.randomUUID();
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
}
