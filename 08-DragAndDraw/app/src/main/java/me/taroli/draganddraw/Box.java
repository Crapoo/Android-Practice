package me.taroli.draganddraw;

import android.graphics.PointF;

/**
 * Created by Matt on 31/07/15.
 */
public class Box {

    private PointF origin;
    private PointF current;

    public Box(PointF origin) {
        this.origin = current = origin;
    }

    public PointF getOrigin() {

        return origin;
    }

    public PointF getCurrent() {
        return current;
    }

    public void setCurrent(PointF current) {
        this.current = current;
    }
    
}
