package com.app.dragable_views;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.v4.widget.AutoScrollHelper;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import java.util.ArrayList;

/**
 * Created by Pratik Surela on 30/6/17.
 */

public class DraggableViewMain implements View.OnTouchListener, View.OnDragListener {

    public static final String TAG = DraggableViewMain.class.getSimpleName();
    private ViewGroup destiViewGroup;
    private ArrayList<View> viewsArrayList = new ArrayList<>();
    public OnViewSelectionListener viewSelection;

    private boolean shouldAutoscroll = false;
    private int direction = 0;
    private final Runnable autoScrollTask;

    @SuppressLint("ClickableViewAccessibility")
    public DraggableViewMain(OnViewSelectionListener onViewSelectionListener, ViewGroup destiViewGroup, @NonNull final ScrollView scrollView) {
        this.destiViewGroup = destiViewGroup;
        this.viewSelection = onViewSelectionListener;

        destiViewGroup.setOnDragListener(this);

        autoScrollTask = new Runnable() {

            public void run() {
                if (shouldAutoscroll) {
                    if (direction > 0) {
                        scrollView.scrollBy(0, 10);
                    } else if (direction < 0) {
                        scrollView.scrollBy(0, -10);
                    }
                }
                scrollView.postDelayed(this, 10);
            }
        };
        scrollView.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                float x = event.getX();
                float y = event.getY();
                Log.d(TAG, "onDrag: " + x + ", " + y);
                int action = event.getAction();
                if (action == DragEvent.ACTION_DRAG_LOCATION) {
                    int height = scrollView.getHeight();
                    if (y > height * 7 / 8) {
                        shouldAutoscroll = true;
                        direction = 1;
                    } else if (y < height / 8) {
                        shouldAutoscroll = true;
                        direction = -1;
                    } else {
                        shouldAutoscroll = false;
                        direction = 0;
                    }
                    if (shouldAutoscroll) {
                        scrollView.removeCallbacks(autoScrollTask);
                        scrollView.post(autoScrollTask);
                    } else {
                        scrollView.removeCallbacks(autoScrollTask);
                    }
                } else if (action == DragEvent.ACTION_DRAG_EXITED
                        || action == DragEvent.ACTION_DROP) {
                    scrollView.removeCallbacks(autoScrollTask);
                }
                return true;
            }
        });

        scrollView.post(autoScrollTask);
    }

    public void addView(View view) {
        viewsArrayList.add(view);
        for (int i = 0; i < viewsArrayList.size(); i++) {
            viewsArrayList.get(i).setOnTouchListener(this);
        }
    }

    @Override
    public boolean onDrag(View layoutView, DragEvent event) {
        int action = event.getAction();
        View view = (View) event.getLocalState();

        switch (action) {
            case DragEvent.ACTION_DROP:
                ViewGroup owner = (ViewGroup) view.getParent();
//                view.setVisibility(View.INVISIBLE);
                owner.removeView(view);
                ViewGroup container = (ViewGroup) layoutView;
                container.addView(view);
                view.setVisibility(View.VISIBLE);


                for (int i = 0; i < viewsArrayList.size(); i++) {
                    if (view.getId() == viewsArrayList.get(i).getId()) {
                        viewSelection.viewSelectedPosition(i);
                    }
                }

                if (container.getId() == destiViewGroup.getId()) {
//                    view.setOnTouchListener(null);
//                    owner.setOnDragListener(null);
                    return true;
                } else {
                    return false;
                }

            case DragEvent.ACTION_DRAG_ENDED:
                if (dropEventNotHandled(event)) {
                    view.setVisibility(View.VISIBLE);
                }
                break;
            default:
                break;
        }
        return true;
    }

    private boolean dropEventNotHandled(DragEvent dragEvent) {
        return !dragEvent.getResult();
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        Log.d(TAG, "onTouch() called with: view = [" + view + "], motionEvent = [" + motionEvent + "]");
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
            view.startDrag(null, shadowBuilder, view, 0);
            view.setVisibility(View.INVISIBLE);
            return true;
        } else {
            return false;
        }
    }
}
