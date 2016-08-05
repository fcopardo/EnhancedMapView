package com.grizzly.mapview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.FrameLayout;

/**
 * Created by FcoPardo on 12/12/14.
 * Based on http://stackoverflow.com/questions/17223155/creating-ondraglistener-for-google-map-v2-fragment
 */

public class BooleanFrameLayout extends FrameLayout {

    /**
     * Class members.
     */
    private boolean isTouched;
    private OnBlockingDragListener mOnDragListener;
    private boolean captureTouches = false;

    public BooleanFrameLayout(Context context) {
        super(context);
    }

    public BooleanFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
    }

    public BooleanFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public interface OnBlockingDragListener {
        void onDrag(MotionEvent motionEvent);
    }

    public boolean isContentTouched(){
        return isTouched;
    }

    /**
     * Detects the touch events inside the layout.
     * @param event
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isTouched = true;
                break;
            case MotionEvent.ACTION_UP:
                isTouched = false;
                break;
        }
        if (mOnDragListener != null) {
            mOnDragListener.onDrag(event);
        }
        return captureTouches || super.dispatchTouchEvent(event);
    }

    /**
     * Setter for custom listener
     * @param mOnDragListener
     */
    public void setOnBlockingDragListener(OnBlockingDragListener mOnDragListener) {
        this.mOnDragListener = mOnDragListener;
    }

    /**
     * Setter for the capture touch flag. Setting this to true will disable touch interaction with the wrapper's contents.
     * @param bol
     */
    public void setCaptureTouches(boolean bol){
        captureTouches = bol;
    }

    public boolean isCaptureTouches(){
        return captureTouches;
    }
}
