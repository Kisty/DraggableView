package com.app.dragable_views;

import android.annotation.SuppressLint;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Pratik Surela on 30/6/17.
 */

public class MultipleDraggableViewHelper implements View.OnTouchListener, View.OnDragListener {

    public static final String TAG = MultipleDraggableViewHelper.class.getSimpleName();
    public static final float SCALE = 1.f / 20;
    private List<ViewGroup> targetViewGroupList;
    private ArrayList<View> viewsArrayList = new ArrayList<>();
    private List<ViewGroup> originalContainerArrayList;

    /**
     * Keys are indices of dragged items, values are indices of drop targets
     */
    private SparseIntArray selection = new SparseIntArray();

    public OnViewSelectionListener viewSelection;
    private View activeDropTarget;

    @SuppressLint("ClickableViewAccessibility")
    public MultipleDraggableViewHelper(OnViewSelectionListener onViewSelectionListener, List<View> draggableViews, List<ViewGroup> startingViewGroups, List<ViewGroup> targetViewGroups) {
        this.targetViewGroupList = Collections.unmodifiableList(targetViewGroups);
        this.viewSelection = onViewSelectionListener;

        for (ViewGroup viewGroup : targetViewGroups) {
            viewGroup.setOnDragListener(this);
        }
        for (View view : draggableViews) {
            addDraggableView(view);
        }
        originalContainerArrayList = Collections.unmodifiableList(startingViewGroups);
    }

    private void addDraggableView(View view) {
        viewsArrayList.add(view);
        view.setOnTouchListener(this);
    }

    @Override
    public boolean onDrag(View targetView, DragEvent event) {
        int action = event.getAction();
        View view = (View) event.getLocalState();

        boolean hasMatch = false;
        for (ViewGroup viewGroup : targetViewGroupList) {
            hasMatch = targetView.getId() == viewGroup.getId();
            if (hasMatch) {
                break;
            }
        }
        if (hasMatch) {
            switch (action) {
                case DragEvent.ACTION_DROP:
                    return true;
                case DragEvent.ACTION_DRAG_ENDED:
                    ViewGroup targetContainer = (ViewGroup) targetView;
                    if (dropEventNotHandled(event)) {
                        //Move view back to original position
                        moveViewBackToOrigin(view);
                    } else {
                        if (activeDropTarget == targetView) {
                            for (int i = 0; i < viewsArrayList.size(); i++) {
                                if (view.getId() == viewsArrayList.get(i).getId()) {
                                    viewSelection.viewSelectedPosition(i);
                                }
                            }
                            //Move item to new selected target
                            int position = targetViewGroupList.indexOf(targetContainer);
                            int index = selection.indexOfValue(position);
                            if (index > -1) {
                                int item = selection.keyAt(index);
                                View oldSelectedView = viewsArrayList.get(item);
                                if (oldSelectedView != view) {
                                    //First move that to original view
                                    moveViewBackToOrigin(oldSelectedView);
                                }
                            }
                            moveViewToTarget(view, targetContainer);
                        } else {
                            return false;
                        }
                    }
                    Log.d(TAG, "selection: " + selection);
                    view.setVisibility(View.VISIBLE);
                    return true;
                case DragEvent.ACTION_DRAG_LOCATION:
                    return true;
                case DragEvent.ACTION_DRAG_STARTED:
                    //Request to listen to events
                    return true;
                case DragEvent.ACTION_DRAG_ENTERED:
                    activeDropTarget = targetView;
                    return true;
                case DragEvent.ACTION_DRAG_EXITED:
                    if (activeDropTarget == targetView) {
                        activeDropTarget = null;
                    }
                    return true;
                default:
                    break;
            }
        } else {
            Log.d(TAG, "onDrag: ignoring drag event");
        }
        return false;
    }

    private void moveViewToTarget(View view, ViewGroup to) {
        int droppedPosition = targetViewGroupList.indexOf(to);
        int item = viewsArrayList.indexOf(view);
        ViewGroup from = (ViewGroup) view.getParent();
        if (targetViewGroupList.contains(from)) {
            //Remove entry from selection
            // as we're moving out
            selection.delete(item);
        }
        selection.put(item, droppedPosition);
        from.removeView(view);
        to.addView(view);
    }

    private void moveViewBackToOrigin(View view) {
        ViewGroup originalTarget = originalContainerArrayList.get(viewsArrayList.indexOf(view));
        int item = viewsArrayList.indexOf(view);
        selection.delete(item);
        ViewGroup owner = (ViewGroup) view.getParent();
        owner.removeView(view);
        if (originalTarget.getChildCount() == 0) {
            originalTarget.addView(view);
        }
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
