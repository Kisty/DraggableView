package com.app.dragable_views;

import android.view.DragEvent;
import android.view.View;
import android.widget.ScrollView;

public class AutoScrollDragHelper {
    private boolean shouldAutoScroll = false;
    private int direction = 0;
    private final Runnable autoScrollTask;
    private final View.OnDragListener autoScrollDragListener;
    private final ScrollView scrollView;

    public AutoScrollDragHelper(ScrollView scrollView) {
        this.scrollView = scrollView;
        autoScrollTask = new Runnable() {

            public void run() {
                if (shouldAutoScroll) {
                    if (direction > 0) {
                        AutoScrollDragHelper.this.scrollView.scrollBy(0, 10);
                    } else if (direction < 0) {
                        AutoScrollDragHelper.this.scrollView.scrollBy(0, -10);
                    }
                }
                AutoScrollDragHelper.this.scrollView.postDelayed(this, 10);
            }
        };
        autoScrollDragListener = new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                float y = event.getY();
                int action = event.getAction();
                if (action == DragEvent.ACTION_DRAG_LOCATION) {
                    int height = AutoScrollDragHelper.this.scrollView.getHeight();
                    if (y > height * (1 - DraggableViewMain.SCALE)) {
                        shouldAutoScroll = true;
                        direction = 1;
                    } else if (y < height * DraggableViewMain.SCALE) {
                        shouldAutoScroll = true;
                        direction = -1;
                    } else {
                        shouldAutoScroll = false;
                        direction = 0;
                    }
                    if (shouldAutoScroll) {
                        AutoScrollDragHelper.this.scrollView.removeCallbacks(autoScrollTask);
                        AutoScrollDragHelper.this.scrollView.post(autoScrollTask);
                    } else {
                        AutoScrollDragHelper.this.scrollView.removeCallbacks(autoScrollTask);
                    }
                    return true;
                } else if (action == DragEvent.ACTION_DRAG_EXITED
                        || action == DragEvent.ACTION_DROP) {
                    AutoScrollDragHelper.this.scrollView.removeCallbacks(autoScrollTask);
                    return false;
                } else if (action == DragEvent.ACTION_DRAG_STARTED) {
                    return true;
                }
                return false;
            }
        };
    }

    public void start() {
        scrollView.setOnDragListener(autoScrollDragListener);
    }
}
