package com.app.dragable_views;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pratik Surela on 30/6/17.
 */

public class DraggableViewMain implements View.OnTouchListener, View.OnDragListener {

    public static final String TAG = DraggableViewMain.class.getSimpleName();
    public static final float SCALE = 1.f / 20;
    private List<ViewGroup> destiViewGroupList;
    private ArrayList<View> viewsArrayList = new ArrayList<>();
    private ArrayList<ViewGroup> originalContainerArrayList = new ArrayList<>();
    public OnViewSelectionListener viewSelection;
    private View activeDropTarget;

    @SuppressLint("ClickableViewAccessibility")
    public DraggableViewMain(OnViewSelectionListener onViewSelectionListener, List<ViewGroup> destiViewGroupList) {
        this.destiViewGroupList = destiViewGroupList;
        this.viewSelection = onViewSelectionListener;

        for (ViewGroup viewGroup : destiViewGroupList) {
            viewGroup.setOnDragListener(this);
        }
    }

    public void addView(View view) {
        viewsArrayList.add(view);
        originalContainerArrayList.add(((ViewGroup) view.getParent()));
        for (int i = 0; i < viewsArrayList.size(); i++) {
            viewsArrayList.get(i).setOnTouchListener(this);
        }
    }

    @Override
    public boolean onDrag(View layoutView, DragEvent event) {
        int action = event.getAction();
        View view = (View) event.getLocalState();

        boolean hasMatch = false;
        for (ViewGroup viewGroup : destiViewGroupList) {
            hasMatch = layoutView.getId() == viewGroup.getId();
            if (hasMatch) {
                break;
            }
        }
        if (hasMatch) {
            Log.d(TAG, "onDrag() called with: layoutView = [" + layoutView + "], event = [" + event + "]");
            switch (action) {
                case DragEvent.ACTION_DROP:
                    Log.d(TAG, "dropped in target " + layoutView.getId());
                    return true;
                case DragEvent.ACTION_DRAG_ENDED:
                    Log.d(TAG, "onDrag: drag ended. result: " + event.getResult());
//                    if (dropEventNotHandled(event)) {
//                        view.setVisibility(View.VISIBLE);
//                    }
                    ViewGroup dropTarget;
                    ViewGroup owner = (ViewGroup) view.getParent();
                    if (dropEventNotHandled(event)) {
                        dropTarget = originalContainerArrayList.get(viewsArrayList.indexOf(view));
                        owner.removeView(view);
                        dropTarget.addView(view);
                        Log.d(TAG, "onDrag: drag not handled for " + layoutView.getId());
                    } else {
                        if (activeDropTarget == layoutView) {
                            for (int i = 0; i < viewsArrayList.size(); i++) {
                                if (view.getId() == viewsArrayList.get(i).getId()) {
                                    viewSelection.viewSelectedPosition(i);
                                }
                            }
                            dropTarget = (ViewGroup) layoutView;
                            owner.removeView(view);
                            dropTarget.addView(view);
                        } else {
                            return false;
                        }
                    }
                    view.setVisibility(View.VISIBLE);
                    return true;
                case DragEvent.ACTION_DRAG_LOCATION:
                    Log.d(TAG, "Drag event location ");
                    return true;
                case DragEvent.ACTION_DRAG_STARTED:
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
