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
    private ArrayList<ViewGroup> originalContainerArrayList = new ArrayList<>();
    private SparseIntArray selection = new SparseIntArray();
    public OnViewSelectionListener viewSelection;
    private View activeDropTarget;

    @SuppressLint("ClickableViewAccessibility")
    public MultipleDraggableViewHelper(OnViewSelectionListener onViewSelectionListener, List<ViewGroup> targetViewGroupList) {
        this.targetViewGroupList = Collections.unmodifiableList(targetViewGroupList);
        this.viewSelection = onViewSelectionListener;

        for (ViewGroup viewGroup : targetViewGroupList) {
            viewGroup.setOnDragListener(this);
        }
    }

    public void addView(View view) {
        viewsArrayList.add(view);
        originalContainerArrayList.add(((ViewGroup) view.getParent()));
        view.setOnTouchListener(this);
    }

    @Override
    public boolean onDrag(View layoutView, DragEvent event) {
        int action = event.getAction();
        View view = (View) event.getLocalState();

        boolean hasMatch = false;
        for (ViewGroup viewGroup : targetViewGroupList) {
            hasMatch = layoutView.getId() == viewGroup.getId();
            if (hasMatch) {
                break;
            }
        }
        if (hasMatch) {
            switch (action) {
                case DragEvent.ACTION_DROP:
                    return true;
                case DragEvent.ACTION_DRAG_ENDED:
                    ViewGroup dropTarget = (ViewGroup) layoutView;
                    ViewGroup owner = (ViewGroup) view.getParent();
                    if (dropEventNotHandled(event)) {
                        ViewGroup originalTarget = originalContainerArrayList.get(viewsArrayList.indexOf(view));
                        int key = targetViewGroupList.indexOf(layoutView);
                        if (selection.get(key, -1) > -1) {
                            selection.delete(key);
                        }
                        owner.removeView(view);
                        originalTarget.addView(view);
                    } else {
                        if (activeDropTarget == layoutView) {
                            for (int i = 0; i < viewsArrayList.size(); i++) {
                                if (view.getId() == viewsArrayList.get(i).getId()) {
                                    viewSelection.viewSelectedPosition(i);
                                    int key = targetViewGroupList.indexOf(dropTarget);
                                    int value = viewsArrayList.indexOf(view);
                                    selection.put(key, value);
                                }
                            }
                            owner.removeView(view);
                            dropTarget.addView(view);
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
                    activeDropTarget = layoutView;
                    return true;
                case DragEvent.ACTION_DRAG_EXITED:
                    if (activeDropTarget == layoutView) {
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
