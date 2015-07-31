package me.taroli.draganddraw;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by Matt on 31/07/15.
 */
public class BoxDrawingView extends View {

    private static final String TAG = "BoxDrawingView";
    private static final String KEY_BOXES = "boxes";

    private Box currentBox;
    private ArrayList<Box> boxes = new ArrayList<Box>();
    private Paint boxPaint;
    private Paint backgroundPaint;

    public BoxDrawingView(Context context) {
        this(context, null);
    }

    public BoxDrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);

        boxPaint = new Paint();
        boxPaint.setColor(0x22ff0000);

        backgroundPaint = new Paint();
        backgroundPaint.setColor(0xff8efe0);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        PointF curr = new PointF(event.getX(), event.getY());

        Log.i(TAG, "Received event at (" + curr.x + "," + curr.y + ")");

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.i(TAG, "ACTION_DOWN");
                currentBox = new Box(curr);
                boxes.add(currentBox);
                break;
            case MotionEvent.ACTION_MOVE:
                Log.i(TAG, "ACTION_MOVE");
                if (currentBox != null) {
                    currentBox.setCurrent(curr);
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                Log.i(TAG, "ACTION_UP");
                currentBox = null;
                break;
            case MotionEvent.ACTION_CANCEL:
                Log.i(TAG, "ACTION_CANCEL");
                currentBox = null;
                break;
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawPaint(backgroundPaint);

        for (Box box : boxes) {
            float left = Math.min(box.getOrigin().x, box.getCurrent().x);
            float right = Math.max(box.getOrigin().x, box.getCurrent().x);
            float top = Math.min(box.getOrigin().y, box.getCurrent().y);
            float bottom = Math.max(box.getOrigin().y, box.getCurrent().y);

            canvas.drawRect(left, top, right, bottom, boxPaint);
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_BOXES, super.onSaveInstanceState());
        bundle.putSerializable(KEY_BOXES, boxes);

        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        Bundle bundle = (Bundle) state;
        boxes = (ArrayList<Box>) bundle.getSerializable(KEY_BOXES);
        super.onRestoreInstanceState(bundle.getParcelable(KEY_BOXES));
    }
}
